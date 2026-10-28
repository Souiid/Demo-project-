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
import com.souiid.demoproject.firebase.domain.usecase.user.FetchUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.FollowUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.GetFollowingUserIdsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.GetProfileStatsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.UnfollowUserUseCase
import com.souiid.demoproject.ui.screens.app.firebase.feed.FirebaseFeedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class FirebaseProfileViewModel(
    private val fetchUserUseCase: FetchUserUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getProfileStatsUseCase: GetProfileStatsUseCase,
    private val getFollowingUserIdsUseCase: GetFollowingUserIdsUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase,
    private val feedViewModel: FirebaseFeedViewModel
) : ViewModel() {

    val currentUser = observeCurrentUserUseCase()

    private val _displayedUser = MutableStateFlow<User?>(null)
    val displayedUser = _displayedUser.asStateFlow()

    private val _profileStats = MutableStateFlow(ProfileStats())
    val profileStats = _profileStats.asStateFlow()

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing = _isFollowing.asStateFlow()

    private val _isUpdatingFollow = MutableStateFlow(false)
    val isUpdatingFollow = _isUpdatingFollow.asStateFlow()

    var isLoadingStats by mutableStateOf(false)
        private set

    var error by mutableStateOf<AppError?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadUser(userId: String?) {
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true
            error = null
            _displayedUser.value = null
            _profileStats.value = ProfileStats()
            _isFollowing.value = false

            try {
                val user = currentUser.value

                if (user != null && (userId == null || userId == user.id)) {
                    _displayedUser.value = user
                    loadProfileStats(user.id)
                } else if (userId != null) {
                    when (val result = fetchUserUseCase(userId)) {
                        is AppResult.Success -> {
                            _displayedUser.value = result.data
                            loadProfileStats(result.data.id)

                            if (result.data.id != currentUser.value?.id) {
                                loadFollowStatus(result.data.id)
                            }
                        }
                        is AppResult.Failure -> error = result.error
                    }
                } else {
                    error = AppError.Unknown()
                }
            } finally {
                isLoading = false
            }
        }
    }

    private fun loadProfileStats(userId: String) {
        viewModelScope.launch {
            isLoadingStats = true

            try {
                when (val result = getProfileStatsUseCase(userId)) {
                    is AppResult.Success -> {
                        if (_displayedUser.value?.id == userId) {
                            _profileStats.value = result.data
                        }
                    }
                    is AppResult.Failure -> error = result.error
                }
            } finally {
                isLoadingStats = false
            }
        }
    }

    private suspend fun loadFollowStatus(userId: String) {
        val currentUserId = currentUser.value?.id ?: return

        when (val result = getFollowingUserIdsUseCase(currentUserId)) {
            is AppResult.Success -> {
                if (_displayedUser.value?.id == userId) {
                    _isFollowing.value = userId in result.data
                }
            }
            is AppResult.Failure -> error = result.error
        }
    }

    fun toggleFollow() {
        val userId = _displayedUser.value?.id ?: return
        val currentUserId = currentUser.value?.id ?: return
        if (userId == currentUserId || _isUpdatingFollow.value || isLoading) return

        viewModelScope.launch {
            _isUpdatingFollow.value = true
            error = null

            try {
                val wasFollowing = _isFollowing.value
                val result = if (wasFollowing) {
                    unfollowUserUseCase(userId)
                } else {
                    followUserUseCase(userId)
                }

                when (result) {
                    is AppResult.Success -> {
                        if (_displayedUser.value?.id == userId) {
                            _isFollowing.value = !wasFollowing
                            loadProfileStats(userId)
                        }

                        feedViewModel.refreshPosts()
                    }
                    is AppResult.Failure -> error = result.error
                }
            } finally {
                _isUpdatingFollow.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                signOutUseCase()
                feedViewModel.clear()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error = AppError.Unknown()
            }
        }
    }
}