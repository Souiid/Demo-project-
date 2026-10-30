package com.souiid.demoproject.ui.screens.app.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.usecase.user.FollowUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.GetFollowingUserIdsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.UnfollowUserUseCase
import com.souiid.demoproject.ui.screens.app.firebase.feed.FirebaseFeedViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class FollowViewModel(
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase,
    private val getFollowingUserIdsUseCase: GetFollowingUserIdsUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val feedViewModel: FirebaseFeedViewModel
) : ViewModel() {

    private val _followingUserIds = MutableStateFlow<Set<String>>(emptySet())
    val followingUserIds = _followingUserIds.asStateFlow()

    private val _updatingUserIds = MutableStateFlow<Set<String>>(emptySet())
    val updatingUserIds = _updatingUserIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<AppError?>(null)
    val error = _error.asStateFlow()

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->
                _followingUserIds.value = emptySet()
                _updatingUserIds.value = emptySet()

                if (user == null) {
                    _isLoading.value = false
                    return@collect
                }

                _isLoading.value = true
                try {
                    when (val result = getFollowingUserIdsUseCase(user.id)) {
                        is AppResult.Success -> _followingUserIds.value = result.data
                        is AppResult.Failure -> _error.value = result.error
                    }
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun toggleFollow(userId: String) {
        val currentUserId = observeCurrentUserUseCase().value?.id ?: return
        if (userId == currentUserId || _isLoading.value || userId in _updatingUserIds.value) return

        _updatingUserIds.value += userId

        viewModelScope.launch {
            val wasFollowing = userId in _followingUserIds.value

            try {
                val result = if (wasFollowing) unfollowUserUseCase(userId) else followUserUseCase(userId)

                when (result) {
                    is AppResult.Success -> {
                        _followingUserIds.value = if (wasFollowing) _followingUserIds.value - userId else _followingUserIds.value + userId
                        feedViewModel.refreshPosts()
                    }
                    is AppResult.Failure -> _error.value = result.error
                }
            } catch (e: CancellationException) {
                throw e
            } finally {
                _updatingUserIds.value -= userId
            }
        }
    }
}
