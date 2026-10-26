package com.souiid.demoproject.ui.screens.app.firebase.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.model.Post
import com.souiid.demoproject.domain.model.User
import com.souiid.demoproject.domain.usecase.post.GetLikedPostIdsUseCase
import com.souiid.demoproject.domain.usecase.post.GetUserPostsUseCase
import com.souiid.demoproject.domain.usecase.post.LikePostUseCase
import com.souiid.demoproject.domain.usecase.post.UnlikePostUseCase
import com.souiid.demoproject.domain.usecase.user.FetchUserUseCase
import com.souiid.demoproject.domain.usecase.user.GetFollowerUserIdsUseCase
import com.souiid.demoproject.domain.usecase.user.GetFollowingUsersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FirebaseProfileListViewModel(
    private val getUserPostsUseCase: GetUserPostsUseCase,
    private val getFollowerUserIdsUseCase: GetFollowerUserIdsUseCase,
    private val getFollowingUsersUseCase: GetFollowingUsersUseCase,
    private val fetchUserUseCase: FetchUserUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    private val getLikedPostIdsUseCase: GetLikedPostIdsUseCase
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val _likedPostIds = MutableStateFlow<Set<String>>(emptySet())
    val likedPostIds = _likedPostIds.asStateFlow()

    private val _updatingLikePostIds = MutableStateFlow<Set<String>>(emptySet())
    val updatingLikePostIds = _updatingLikePostIds.asStateFlow()

    private val _author = MutableStateFlow<User?>(null)
    val author = _author.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<AppError?>(null)
    val error = _error.asStateFlow()

    fun loadData(userId: String, type: ProfileListType) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _posts.value = emptyList()
            _users.value = emptyList()
            _updatingLikePostIds.value = emptySet()
            _author.value = null

            try {
                when (type) {
                    ProfileListType.POSTS -> loadPosts(userId)
                    ProfileListType.FOLLOWERS -> loadFollowers(userId)
                    ProfileListType.FOLLOWING -> loadFollowing(userId)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadPosts(userId: String) {
        when (val result = fetchUserUseCase(userId)) {
            is AppResult.Success -> _author.value = result.data
            is AppResult.Failure -> {
                _error.value = result.error
                return
            }
        }

        when (val result = getUserPostsUseCase(userId)) {
            is AppResult.Success -> {
                _posts.value = result.data
                when (val likesResult = getLikedPostIdsUseCase(result.data.map { it.id })) {
                    is AppResult.Success -> _likedPostIds.value = likesResult.data
                    is AppResult.Failure -> _error.value = likesResult.error
                }
            }
            is AppResult.Failure -> _error.value = result.error
        }
    }

    private suspend fun loadFollowers(userId: String) {
        when (val result = getFollowerUserIdsUseCase(userId)) {
            is AppResult.Success -> {
                val followers = mutableListOf<User>()

                for (followerId in result.data) {
                    when (val userResult = fetchUserUseCase(followerId)) {
                        is AppResult.Success -> followers.add(userResult.data)
                        is AppResult.Failure -> {
                            _error.value = userResult.error
                            return
                        }
                    }
                }

                _users.value = followers
            }
            is AppResult.Failure -> _error.value = result.error
        }
    }

    private suspend fun loadFollowing(userId: String) {
        when (val result = getFollowingUsersUseCase(userId)) {
            is AppResult.Success -> _users.value = result.data
            is AppResult.Failure -> _error.value = result.error
        }
    }

    fun toggleLike(postId: String) {
        if (postId in _updatingLikePostIds.value || _posts.value.none { it.id == postId }) return

        _updatingLikePostIds.value += postId

        viewModelScope.launch {
            try {
                val wasLiked = postId in _likedPostIds.value
                val result = if (wasLiked) unlikePostUseCase(postId) else likePostUseCase(postId)

                when (result) {
                    is AppResult.Success -> {
                        _likedPostIds.value = if (wasLiked) _likedPostIds.value - postId else _likedPostIds.value + postId

                        _posts.value = _posts.value.map { post ->
                            if (post.id == postId) {
                                post.copy(likesCount = (post.likesCount + if (wasLiked) -1 else 1).coerceAtLeast(0))
                            } else post
                        }
                    }
                    is AppResult.Failure -> _error.value = result.error
                }
            } finally {
                _updatingLikePostIds.value -= postId
            }
        }
    }
}