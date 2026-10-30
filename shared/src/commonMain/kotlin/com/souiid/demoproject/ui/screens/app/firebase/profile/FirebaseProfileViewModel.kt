
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
import com.souiid.demoproject.firebase.domain.usecase.user.GetProfileStatsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import com.souiid.demoproject.ui.screens.app.firebase.FollowViewModel
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
    private val followViewModel: FollowViewModel,
    private val feedViewModel: FirebaseFeedViewModel
) : ViewModel() {

    val currentUser = observeCurrentUserUseCase()

    private val _displayedUser = MutableStateFlow<User?>(null)
    val displayedUser = _displayedUser.asStateFlow()

    private val _profileStats = MutableStateFlow(ProfileStats())
    val profileStats = _profileStats.asStateFlow()

    var isLoadingStats by mutableStateOf(false)
        private set

    var error by mutableStateOf<AppError?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            var previousIds: Set<String>? = null

            followViewModel.followingUserIds.collect { ids ->
                val previous = previousIds
                previousIds = ids

                if (previous == null || isLoadingStats) return@collect

                val displayedId = _displayedUser.value?.id ?: return@collect
                if (displayedId == currentUser.value?.id) return@collect

                val wasFollowing = displayedId in previous
                val isFollowing = displayedId in ids

                if (wasFollowing != isFollowing) {
                    _profileStats.value = _profileStats.value.copy(
                        followers = (_profileStats.value.followers + if (isFollowing) 1 else -1).coerceAtLeast(0)
                    )
                }
            }
        }
    }

    fun loadUser(userId: String?) {
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true
            error = null
            _displayedUser.value = null
            _profileStats.value = ProfileStats()

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
