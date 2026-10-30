package com.souiid.demoproject.ui.screens.app.firebase.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.LikeSyncState
import com.souiid.demoproject.firebase.domain.model.Post
import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.firebase.domain.usecase.post.GetLikedPostIdsUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.GetUserPostsUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.LikePostUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.UnlikePostUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.FetchUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.GetFollowerUserIdsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.GetFollowingUsersUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds


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

    private val _author = MutableStateFlow<User?>(null)
    val author = _author.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<AppError?>(null)
    val error = _error.asStateFlow()

    private val likeSyncStates = mutableMapOf<String, LikeSyncState>()
    private val likeSyncJobs = mutableMapOf<String, Job>()
    private var loadJob: Job? = null
    private var generation = 0

    fun loadData(userId: String, type: ProfileListType) {
        loadJob?.cancel()
        resetLikeSync()
        val currentGeneration = ++generation

        loadJob = viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _posts.value = emptyList()
            _users.value = emptyList()
            _likedPostIds.value = emptySet()
            _author.value = null

            try {
                when (type) {
                    ProfileListType.POSTS -> loadPosts(userId, currentGeneration)
                    ProfileListType.FOLLOWERS -> loadFollowers(userId, currentGeneration)
                    ProfileListType.FOLLOWING -> loadFollowing(userId, currentGeneration)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                if (currentGeneration == generation) _error.value = AppError.Unknown()
            } finally {
                if (currentGeneration == generation) _isLoading.value = false
            }
        }
    }

    private suspend fun loadPosts(userId: String, currentGeneration: Int) {
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
                    is AppResult.Success -> {
                        if (currentGeneration != generation) return

                        _likedPostIds.value = likesResult.data

                        result.data.forEach { post ->
                            likeSyncStates[post.id] = LikeSyncState(
                                confirmedLiked = post.id in likesResult.data
                            )
                        }
                    }
                    is AppResult.Failure -> _error.value = likesResult.error
                }
            }
            is AppResult.Failure -> _error.value = result.error
        }
    }

    private suspend fun loadFollowers(userId: String, currentGeneration: Int) {
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

                if (currentGeneration == generation) _users.value = followers
            }
            is AppResult.Failure -> _error.value = result.error
        }
    }

    private suspend fun loadFollowing(userId: String, currentGeneration: Int) {
        when (val result = getFollowingUsersUseCase(userId)) {
            is AppResult.Success -> {
                if (currentGeneration == generation) _users.value = result.data
            }
            is AppResult.Failure -> _error.value = result.error
        }
    }

    fun toggleLike(postId: String) {
        if (_posts.value.none { it.id == postId }) return
        val state = likeSyncStates[postId] ?: return

        val wasLiked = postId in _likedPostIds.value
        val desiredLiked = !wasLiked

        state.revision++

        _likedPostIds.value = if (desiredLiked) _likedPostIds.value + postId else _likedPostIds.value - postId
        _posts.value = _posts.value.map {
            if (it.id == postId) {
                it.copy(likesCount = (it.likesCount + if (desiredLiked) 1 else -1).coerceAtLeast(0))
            } else it
        }

        state.debounceJob?.cancel()

        val currentGeneration = generation
        val revision = state.revision

        state.debounceJob = viewModelScope.launch {
            delay(500.milliseconds)

            if (currentGeneration != generation || revision != state.revision) return@launch

            state.debounceJob = null
            syncLike(postId, currentGeneration)
        }
    }

    private fun syncLike(postId: String, currentGeneration: Int) {
        if (currentGeneration != generation) return

        val state = likeSyncStates[postId] ?: return
        if (state.syncing || state.debounceJob?.isActive == true) return

        val desiredLiked = postId in _likedPostIds.value
        if (desiredLiked == state.confirmedLiked) return

        state.syncing = true
        val requestRevision = state.revision

        val job = viewModelScope.launch {
            try {
                val result = if (desiredLiked) likePostUseCase(postId) else unlikePostUseCase(postId)
                if (currentGeneration != generation || likeSyncStates[postId] !== state) return@launch

                when (result) {
                    is AppResult.Success -> state.confirmedLiked = desiredLiked
                    is AppResult.Failure -> {
                        _error.value = result.error
                        if (state.revision == requestRevision) {
                            rollbackLike(postId, state.confirmedLiked)
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } finally {
                if (currentGeneration == generation && likeSyncStates[postId] === state) {
                    state.syncing = false

                    if (state.revision != requestRevision && state.debounceJob == null) {
                        syncLike(postId, currentGeneration)
                    }
                }
            }
        }

        likeSyncJobs[postId] = job
        job.invokeOnCompletion {
            if (likeSyncJobs[postId] === job) likeSyncJobs.remove(postId)
        }
    }

    private fun rollbackLike(postId: String, confirmedLiked: Boolean) {
        val currentLiked = postId in _likedPostIds.value
        if (currentLiked == confirmedLiked) return

        _likedPostIds.value = if (confirmedLiked) _likedPostIds.value + postId else _likedPostIds.value - postId

        _posts.value = _posts.value.map {
            if (it.id == postId) {
                it.copy(likesCount = (it.likesCount + if (confirmedLiked) 1 else -1).coerceAtLeast(0))
            } else it
        }
    }

    private fun resetLikeSync() {
        likeSyncStates.values.forEach { it.debounceJob?.cancel() }
        likeSyncJobs.values.forEach { it.cancel() }
        likeSyncStates.clear()
        likeSyncJobs.clear()
    }

    override fun onCleared() {
        resetLikeSync()
        super.onCleared()
    }
}
