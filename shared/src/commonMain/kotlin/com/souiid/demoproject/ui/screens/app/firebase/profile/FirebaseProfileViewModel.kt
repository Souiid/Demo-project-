package com.souiid.demoproject.ui.screens.app.firebase.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.ProfileStats
import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.firebase.domain.usecase.auth.SignOutUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.ObserveUserPostsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.FetchUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveProfileFollowsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class FirebaseProfileViewModel(
    private val fetchUserUseCase: FetchUserUseCase,
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val observeProfileFollowsUseCase: ObserveProfileFollowsUseCase,
    private val observeUserPostsUseCase: ObserveUserPostsUseCase
) : ViewModel() {

    val currentUser = observeCurrentUserUseCase()

    private var loadedUserId: String? = null
    private var loadJob: Job? = null
    private var statsJob: Job? = null
    private var generation = 0

    private val _displayedUser = MutableStateFlow<User?>(null)
    val displayedUser = _displayedUser.asStateFlow()

    private val _profileStats = MutableStateFlow(ProfileStats())
    val profileStats = _profileStats.asStateFlow()

    var hasLoadedStats by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<AppError?>(null)
        private set

    init {
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null && _displayedUser.value?.id == user.id) {
                    _displayedUser.value = user
                }
            }
        }
    }

    fun loadUser(userId: String?, forceRefresh: Boolean = false) {
        val targetUserId = userId ?: currentUser.value?.id ?: return
        if (!forceRefresh && loadedUserId == targetUserId && _displayedUser.value != null) return

        val currentGeneration = ++generation
        loadJob?.cancel()
        statsJob?.cancel()

        val isDifferentUser = loadedUserId != targetUserId

        if (isDifferentUser) {
            loadedUserId = null
            _displayedUser.value = null
            _profileStats.value = ProfileStats()
            hasLoadedStats = false
        }

        isLoading = _displayedUser.value == null
        error = null

        loadJob = viewModelScope.launch {
            try {
                val user = currentUser.value
                val result = if (user != null && user.id == targetUserId) {
                    AppResult.Success(user)
                } else {
                    fetchUserUseCase(targetUserId)
                }

                if (currentGeneration != generation) return@launch

                when (result) {
                    is AppResult.Success -> {
                        _displayedUser.value = result.data
                        loadedUserId = targetUserId
                        observeProfileStats(targetUserId, currentGeneration)
                    }
                    is AppResult.Failure -> error = result.error
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                if (currentGeneration == generation) error = AppError.Unknown()
            } finally {
                if (currentGeneration == generation) isLoading = false
            }
        }
    }

    private fun observeProfileStats(userId: String, currentGeneration: Int) {
        statsJob?.cancel()

        statsJob = viewModelScope.launch {
            try {
                combine(
                    observeProfileFollowsUseCase(userId),
                    observeUserPostsUseCase(userId)
                ) { follows, posts ->
                    ProfileStats(
                        posts = posts.size,
                        followers = follows.first,
                        following = follows.second
                    )
                }.collect { stats ->
                    if (currentGeneration != generation || _displayedUser.value?.id != userId) return@collect

                    _profileStats.value = stats
                    hasLoadedStats = true
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                if (currentGeneration == generation) {
                    error = AppError.Unknown()
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                signOutUseCase()

                generation++
                loadJob?.cancel()
                statsJob?.cancel()

                loadedUserId = null
                _displayedUser.value = null
                _profileStats.value = ProfileStats()
                hasLoadedStats = false
                isLoading = false
                error = null
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                error = AppError.Unknown()
            }
        }
    }
}
