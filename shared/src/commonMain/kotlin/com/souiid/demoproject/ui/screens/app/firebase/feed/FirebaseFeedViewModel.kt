
package com.souiid.demoproject.ui.screens.app.firebase.feed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.LikeSyncState
import com.souiid.demoproject.firebase.domain.model.Post
import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.firebase.domain.usecase.notification.SaveNotificationTokenUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.CreatePostUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.GetLikedPostIdsUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.LikePostUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.ObserveFeedPostsUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.UnlikePostUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.FetchUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds


class FirebaseFeedViewModel(
    private val fetchUserUseCase: FetchUserUseCase,
    private val createPostUseCase: CreatePostUseCase,
    private val observeFeedPostsUseCase: ObserveFeedPostsUseCase,
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    private val getLikedPostIdsUseCase: GetLikedPostIdsUseCase,
    private val saveNotificationTokenUseCase: SaveNotificationTokenUseCase
) : ViewModel()
{

    val currentUser = observeCurrentUserUseCase()

    private val _deletedPostCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val deletedPostCounts = _deletedPostCounts.asStateFlow()

    var error by mutableStateOf<AppError?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var posts by mutableStateOf<List<Post>>(emptyList())
        private set

    var isLoadingPosts by mutableStateOf(false)
        private set


    var isPublishing by mutableStateOf(false)
        private set

    var isPostCreated by mutableStateOf(false)
        private set

    var authors by mutableStateOf<Map<String, User>>(emptyMap())
        private set

    var likedPostIds by mutableStateOf<Set<String>>(emptySet())
        private set

    private var observedUserId: String? = null
    private var feedJob: Job? = null
    private var feedGeneration = 0
    private var checkedLikePostIds = emptySet<String>()

    private val likeSyncStates = mutableMapOf<String, LikeSyncState>()
    private val likeSyncJobs = mutableMapOf<String, Job>()
    private val remoteLikeCounts = mutableMapOf<String, Int>()
    private val pendingLikeAcknowledgements = mutableMapOf<String, Boolean>()

    init {
        observeNotificationToken()
    }

    fun loadCurrentUser() {
        val userId = Firebase.auth.currentUser?.uid ?: run {
            error = AppError.Unknown()
            return
        }

        if (isLoading) return

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

    fun observePosts(forceRestart: Boolean = false) {
        val userId = currentUser.value?.id ?: return

        if (!forceRestart && feedJob?.isActive == true && observedUserId == userId) return

        if (observedUserId != userId) {
            clear()
        }

        observedUserId = userId
        feedJob?.cancel()

        if (forceRestart) {
            resetLikeSync()
            checkedLikePostIds = emptySet()
            likedPostIds = emptySet()
        }

        val generation = ++feedGeneration

        feedJob = viewModelScope.launch {
            isLoadingPosts = posts.isEmpty()
            error = null

            try {
                observeFeedPostsUseCase()
                    .catch { exception ->
                        if (exception is CancellationException) throw exception
                        if (generation == feedGeneration) error = AppError.Unknown()
                    }
                    .collect { updatedPosts ->
                        if (generation != feedGeneration) return@collect

                        val currentPostIds = updatedPosts.map { it.id }.toSet()
                        val newPosts = updatedPosts.filter { it.id !in checkedLikePostIds }

                        updatedPosts.forEach { remotePost ->
                            remoteLikeCounts[remotePost.id] = remotePost.likesCount

                            val acknowledgedLiked = pendingLikeAcknowledgements[remotePost.id]
                            if (acknowledgedLiked != null) {
                                val state = likeSyncStates[remotePost.id]
                                val confirmedLiked = state?.confirmedLiked ?: acknowledgedLiked
                                val expectedCount = if (acknowledgedLiked) 1 else -1
                                val localPost = posts.find { it.id == remotePost.id }

                                if (state != null && !state.syncing && state.debounceJob?.isActive != true &&
                                    confirmedLiked == acknowledgedLiked && localPost != null &&
                                    remotePost.likesCount == localPost.likesCount
                                ) {
                                    pendingLikeAcknowledgements.remove(remotePost.id)
                                }
                            }
                        }

                        posts = updatedPosts.map { remotePost ->
                            val state = likeSyncStates[remotePost.id]
                            val localPost = posts.find { it.id == remotePost.id }
                            val hasPendingLike = state != null &&
                                    (state.syncing || state.debounceJob?.isActive == true ||
                                            remotePost.id in pendingLikeAcknowledgements)

                            if (hasPendingLike && localPost != null) {
                                remotePost.copy(likesCount = localPost.likesCount)
                            } else {
                                remotePost
                            }
                        }

                        likedPostIds = likedPostIds.intersect(currentPostIds)
                        checkedLikePostIds = checkedLikePostIds.intersect(currentPostIds)

                        val removedPostIds = likeSyncStates.keys.filter { it !in currentPostIds }
                        removedPostIds.forEach { postId ->
                            likeSyncStates.remove(postId)?.debounceJob?.cancel()
                            likeSyncJobs.remove(postId)?.cancel()
                            remoteLikeCounts.remove(postId)
                            pendingLikeAcknowledgements.remove(postId)
                        }

                        isLoadingPosts = false

                        loadMissingAuthors(updatedPosts, generation)
                        loadMissingLikes(newPosts, generation)
                    }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                if (generation == feedGeneration) error = AppError.Unknown()
            } finally {
                if (generation == feedGeneration) isLoadingPosts = false
            }
        }
    }

    fun createPost(content: String) {
        if (isPublishing || content.isBlank() || content.length > 1000) return

        viewModelScope.launch {
            isPublishing = true
            error = null
            isPostCreated = false

            try {
                when (val result = createPostUseCase(content)) {
                    is AppResult.Success -> isPostCreated = true
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

    fun toggleLike(postId: String) {
        val post = posts.find { it.id == postId } ?: return
        if (postId !in checkedLikePostIds) return

        val state = likeSyncStates.getOrPut(postId) {
            LikeSyncState(confirmedLiked = postId in likedPostIds)
        }

        val wasLiked = postId in likedPostIds
        val desiredLiked = !wasLiked

        state.revision++

        likedPostIds = if (desiredLiked) likedPostIds + postId else likedPostIds - postId
        posts = posts.map {
            if (it.id == postId) {
                it.copy(likesCount = (it.likesCount + if (desiredLiked) 1 else -1).coerceAtLeast(0))
            } else it
        }

        state.debounceJob?.cancel()

        val generation = feedGeneration
        val revision = state.revision

        state.debounceJob = viewModelScope.launch {
            delay(500.milliseconds)

            if (generation != feedGeneration || revision != state.revision) return@launch

            state.debounceJob = null
            syncLike(postId, generation)
        }
    }

    private fun syncLike(postId: String, generation: Int) {
        if (generation != feedGeneration || postId !in checkedLikePostIds) return

        val state = likeSyncStates[postId] ?: return
        if (state.syncing || state.debounceJob?.isActive == true) return

        val desiredLiked = postId in likedPostIds
        if (desiredLiked == state.confirmedLiked) return

        state.syncing = true
        val requestRevision = state.revision

        val job = viewModelScope.launch {
            try {
                val result = if (desiredLiked) likePostUseCase(postId) else unlikePostUseCase(postId)
                if (generation != feedGeneration || likeSyncStates[postId] !== state) return@launch

                when (result) {
                    is AppResult.Success -> {
                        state.confirmedLiked = desiredLiked
                        pendingLikeAcknowledgements[postId] = desiredLiked
                    }

                    is AppResult.Failure -> {
                        error = result.error

                        if (state.revision == requestRevision) {
                            rollbackLike(postId, state.confirmedLiked)
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } finally {
                if (generation == feedGeneration && likeSyncStates[postId] === state) {
                    state.syncing = false

                    if (state.revision != requestRevision && state.debounceJob == null) {
                        syncLike(postId, generation)
                    }
                }
            }
        }

        likeSyncJobs[postId] = job
        job.invokeOnCompletion {
            if (likeSyncJobs[postId] === job) {
                likeSyncJobs.remove(postId)
            }
        }
    }

    private fun rollbackLike(postId: String, confirmedLiked: Boolean) {
        val currentLiked = postId in likedPostIds
        if (currentLiked == confirmedLiked) return

        likedPostIds = if (confirmedLiked) likedPostIds + postId else likedPostIds - postId

        posts = posts.map {
            if (it.id == postId) {
                it.copy(likesCount = (it.likesCount + if (confirmedLiked) 1 else -1).coerceAtLeast(0))
            } else it
        }
    }

    private suspend fun loadMissingAuthors(updatedPosts: List<Post>, generation: Int) {
        val missingAuthorIds = updatedPosts.map { it.authorId }
            .distinct()
            .filter { it !in authors }

        for (authorId in missingAuthorIds) {
            when (val result = fetchUserUseCase(authorId)) {
                is AppResult.Success -> {
                    if (generation == feedGeneration) {
                        authors = authors + (authorId to result.data)
                    }
                }
                is AppResult.Failure -> {
                    if (generation == feedGeneration) error = result.error
                }
            }
        }
    }

    private suspend fun loadMissingLikes(updatedPosts: List<Post>, generation: Int) {
        val uncheckedPostIds = updatedPosts.map { it.id }
            .filter { it !in checkedLikePostIds }

        if (uncheckedPostIds.isEmpty()) return

        when (val result = getLikedPostIdsUseCase(uncheckedPostIds)) {
            is AppResult.Success -> {
                if (generation == feedGeneration) {
                    uncheckedPostIds.forEach { postId ->
                        val isLiked = postId in result.data
                        likeSyncStates[postId] = LikeSyncState(confirmedLiked = isLiked)
                    }

                    likedPostIds = likedPostIds + result.data
                    checkedLikePostIds = checkedLikePostIds + uncheckedPostIds
                }
            }

            is AppResult.Failure -> {
                if (generation == feedGeneration) error = result.error
            }
        }
    }

    private fun resetLikeSync() {
        likeSyncStates.values.forEach { it.debounceJob?.cancel() }
        likeSyncJobs.values.forEach { it.cancel() }

        likeSyncStates.clear()
        likeSyncJobs.clear()
        remoteLikeCounts.clear()
        pendingLikeAcknowledgements.clear()
    }

    fun clear() {
        feedJob?.cancel()
        feedJob = null
        feedGeneration++
        observedUserId = null

        resetLikeSync()

        posts = emptyList()
        authors = emptyMap()
        likedPostIds = emptySet()
        checkedLikePostIds = emptySet()

        isLoading = false
        isLoadingPosts = false
        isPublishing = false
        isPostCreated = false
        error = null
    }

    fun refreshPosts() {
        observePosts(forceRestart = true)
    }

    fun observeNotificationToken() {
        viewModelScope.launch {
            currentUser.collectLatest { user ->
                if (user == null) return@collectLatest

                try {
                    saveNotificationTokenUseCase(user.id)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {

                }
            }
        }
    }

    fun notifyPostDeleted(authorId: String) {
        _deletedPostCounts.value = _deletedPostCounts.value.toMutableMap().apply {
            this[authorId] = (this[authorId] ?: 0) + 1
        }
    }
}

