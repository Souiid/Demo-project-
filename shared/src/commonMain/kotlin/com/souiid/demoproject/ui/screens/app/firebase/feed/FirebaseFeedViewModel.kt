package com.souiid.demoproject.ui.screens.app.firebase.feed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.model.Post
import com.souiid.demoproject.domain.model.User
import com.souiid.demoproject.domain.usecase.post.CreatePostUseCase
import com.souiid.demoproject.domain.usecase.post.GetFeedPostsUseCase
import com.souiid.demoproject.domain.usecase.post.GetLikedPostIdsUseCase
import com.souiid.demoproject.domain.usecase.post.LikePostUseCase
import com.souiid.demoproject.domain.usecase.post.UnlikePostUseCase
import com.souiid.demoproject.domain.usecase.user.FetchUserUseCase
import com.souiid.demoproject.domain.usecase.user.ObserveCurrentUserUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

class FirebaseFeedViewModel(
    private val fetchUserUseCase: FetchUserUseCase,
    private val createPostUseCase: CreatePostUseCase,
    private val getFeedPostsUseCase: GetFeedPostsUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    private val getLikedPostIdsUseCase: GetLikedPostIdsUseCase

) : ViewModel() {

    val currentUser = observeCurrentUserUseCase()

    var error by mutableStateOf<AppError?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var posts by mutableStateOf<List<Post>>(emptyList())
        private set

    var isLoadingPosts by mutableStateOf(false)
        private set

    var content by mutableStateOf("")
        private set

    var isPublishing by mutableStateOf(false)
        private set

    var isPostCreated by mutableStateOf(false)
        private set

    var authors by mutableStateOf<Map<String, User>>(emptyMap())
        private set

    var likedPostIds by mutableStateOf<Set<String>>(emptySet())
        private set

    var updatingLikePostIds by mutableStateOf<Set<String>>(emptySet())
        private set

    fun loadCurrentUser() {
        if (isLoading) return
        val userId = Firebase.auth.currentUser?.uid ?: run {
            error = AppError.Unknown()
            return
        }
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                when (val result = fetchUserUseCase(userId)) {
                    is AppResult.Success -> Unit
                    is AppResult.Failure -> error = result.error
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun loadPosts() {
        if (isLoadingPosts) return
        viewModelScope.launch {
            isLoadingPosts = true
            error = null
            try {
                when (val result = getFeedPostsUseCase()) {
                    is AppResult.Success -> {
                        posts = result.data
                        loadLikedPosts(result.data)
                        loadAuthors(result.data)
                    }
                    is AppResult.Failure -> error = result.error
                }
            } finally {
                isLoadingPosts = false
            }
        }
    }

    fun onContentChange(value: String) {
        content = value
    }

    fun createPost() {
        if (isPublishing || content.isBlank() || content.length > 1000) return
        viewModelScope.launch {
            isPublishing = true
            error = null
            isPostCreated = false
            try {
                when (val result = createPostUseCase(content)) {
                    is AppResult.Success -> {
                        content = ""
                        isPostCreated = true
                        loadPosts()
                    }
                    is AppResult.Failure -> error = result.error
                }
            } finally {
                isPublishing = false
            }
        }
    }

    fun onPostCreatedHandled() {
        isPostCreated = false
    }

    private suspend fun loadAuthors(posts: List<Post>) {
        val users = mutableMapOf<String, User>()

        posts.map { it.authorId }.distinct().forEach { authorId ->
            when (val result = fetchUserUseCase(authorId)) {
                is AppResult.Success -> users[authorId] = result.data
                is AppResult.Failure -> Unit
            }
        }

        authors = users
    }

    private suspend fun loadLikedPosts(posts: List<Post>) {
        when (val result = getLikedPostIdsUseCase(posts.map { it.id })) {
            is AppResult.Success -> likedPostIds = result.data
            is AppResult.Failure -> error = result.error
        }
    }

    fun toggleLike(postId: String) {
        if (postId in updatingLikePostIds || posts.none { it.id == postId }) return

        updatingLikePostIds = updatingLikePostIds + postId

        viewModelScope.launch {
            error = null
            try {
                val wasLiked = postId in likedPostIds
                val result = if (wasLiked) unlikePostUseCase(postId) else likePostUseCase(postId)

                when (result) {
                    is AppResult.Success -> {
                        likedPostIds = if (wasLiked) likedPostIds - postId else likedPostIds + postId

                        posts = posts.map { post ->
                            if (post.id == postId) {
                                post.copy(likesCount = (post.likesCount + if (wasLiked) -1 else 1).coerceAtLeast(0))
                            } else {
                                post
                            }
                        }
                    }
                    is AppResult.Failure -> error = result.error
                }
            } finally {
                updatingLikePostIds = updatingLikePostIds - postId
            }
        }
    }
}