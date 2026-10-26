
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
import com.souiid.demoproject.domain.usecase.post.GetLikedPostIdsUseCase
import com.souiid.demoproject.domain.usecase.post.LikePostUseCase
import com.souiid.demoproject.domain.usecase.post.ObserveFeedPostsUseCase
import com.souiid.demoproject.domain.usecase.post.UnlikePostUseCase
import com.souiid.demoproject.domain.usecase.user.FetchUserUseCase
import com.souiid.demoproject.domain.usecase.user.ObserveCurrentUserUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FirebaseFeedViewModel(
    private val fetchUserUseCase: FetchUserUseCase,
    private val createPostUseCase: CreatePostUseCase,
    private val observeFeedPostsUseCase: ObserveFeedPostsUseCase,
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
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

    private var observedUserId: String? = null
    private var feedJob: Job? = null
    private var feedGeneration = 0
    private var checkedLikePostIds = emptySet<String>()

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

        val generation = ++feedGeneration

        feedJob = viewModelScope.launch {
            isLoadingPosts = posts.isEmpty()
            error = null

            if (forceRestart) {
                checkedLikePostIds = emptySet()
                likedPostIds = emptySet()
            }

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

                        posts = updatedPosts
                        likedPostIds = likedPostIds.intersect(currentPostIds)
                        checkedLikePostIds = checkedLikePostIds.intersect(currentPostIds)

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

    fun toggleLike(postId: String) {
        if (postId in updatingLikePostIds || posts.none { it.id == postId }) return

        updatingLikePostIds = updatingLikePostIds + postId
        val generation = feedGeneration

        viewModelScope.launch {
            error = null

            try {
                val wasLiked = postId in likedPostIds
                val result = if (wasLiked) unlikePostUseCase(postId) else likePostUseCase(postId)

                if (generation != feedGeneration) return@launch

                when (result) {
                    is AppResult.Success -> {
                        likedPostIds = if (wasLiked) likedPostIds - postId else likedPostIds + postId
                    }
                    is AppResult.Failure -> error = result.error
                }
            } finally {
                if (generation == feedGeneration) {
                    updatingLikePostIds = updatingLikePostIds - postId
                }
            }
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
                    likedPostIds = likedPostIds + result.data
                    checkedLikePostIds = checkedLikePostIds + uncheckedPostIds
                }
            }
            is AppResult.Failure -> {
                if (generation == feedGeneration) error = result.error
            }
        }
    }

    fun clear() {
        feedJob?.cancel()
        feedJob = null
        feedGeneration++
        observedUserId = null

        posts = emptyList()
        authors = emptyMap()
        likedPostIds = emptySet()
        checkedLikePostIds = emptySet()
        updatingLikePostIds = emptySet()

        content = ""
        isLoading = false
        isLoadingPosts = false
        isPublishing = false
        isPostCreated = false
        error = null
    }

    fun refreshPosts() {
        observePosts(forceRestart = true)
    }
}
