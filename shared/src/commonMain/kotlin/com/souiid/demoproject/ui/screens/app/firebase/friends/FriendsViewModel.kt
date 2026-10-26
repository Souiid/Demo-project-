package com.souiid.demoproject.ui.screens.app.firebase.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.model.User
import com.souiid.demoproject.domain.usecase.user.FollowUserUseCase
import com.souiid.demoproject.domain.usecase.user.GetFollowingUserIdsUseCase
import com.souiid.demoproject.domain.usecase.user.GetFollowingUsersUseCase
import com.souiid.demoproject.domain.usecase.user.ObserveCurrentUserUseCase
import com.souiid.demoproject.domain.usecase.user.SearchUsersUseCase
import com.souiid.demoproject.domain.usecase.user.UnfollowUserUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class FriendsViewModel(
    private val searchUsersUseCase: SearchUsersUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase,
    private val getFollowingUserIdsUseCase: GetFollowingUserIdsUseCase,
    private val getFollowingUsersUseCase: GetFollowingUsersUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<AppError?>(null)
    val error = _error.asStateFlow()

    private val _followingUserIds = MutableStateFlow<Set<String>>(emptySet())
    val followingUserIds = _followingUserIds.asStateFlow()

    private val _updatingUserIds = MutableStateFlow<Set<String>>(emptySet())
    val updatingUserIds = _updatingUserIds.asStateFlow()

    private val _isFollowingLoading = MutableStateFlow(true)
    val isFollowingLoading = _isFollowingLoading.asStateFlow()

    private val _followingUsers = MutableStateFlow<List<User>>(emptyList())
    val followingUsers = _followingUsers.asStateFlow()

    private val _isFollowingUsersLoading = MutableStateFlow(false)
    val isFollowingUsersLoading = _isFollowingUsersLoading.asStateFlow()

    val userId = observeCurrentUserUseCase().value?.id

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(400.milliseconds)
                .distinctUntilChanged()
                .collectLatest { query ->
                    _users.value = emptyList()
                    _error.value = null

                    if (query.isBlank()) {
                        _isLoading.value = false
                        return@collectLatest
                    }

                    _isLoading.value = true
                    try {
                        when (val result = searchUsersUseCase(query)) {
                            is AppResult.Success -> _users.value = result.data
                            is AppResult.Failure -> _error.value = result.error
                        }
                    } finally {
                        _isLoading.value = false
                    }
                }
        }

        loadFollowing()
        loadFollowingUsers()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun followUser(userId: String) {
        updateFollow(userId, true)
    }

    fun unfollowUser(userId: String) {
        updateFollow(userId, false)
    }


    private fun updateFollow(userId: String, follow: Boolean) {
        if (userId in _updatingUserIds.value) return

        viewModelScope.launch {
            _updatingUserIds.value += userId

            val result = if (follow) followUserUseCase(userId) else unfollowUserUseCase(userId)

            when (result) {
                is AppResult.Success -> {
                    _followingUserIds.value = if (follow) {
                        _followingUserIds.value + userId
                    } else {
                        _followingUserIds.value - userId
                    }
                    loadFollowingUsers()
                }
                is AppResult.Failure -> _error.value = result.error
            }

            _updatingUserIds.value -= userId
        }
    }

    private fun loadFollowing() {

        val userId = userId ?: return
        viewModelScope.launch {
            _isFollowingLoading.value = true
            try {
                when (val result = getFollowingUserIdsUseCase(userId)) {
                    is AppResult.Success -> _followingUserIds.value = result.data
                    is AppResult.Failure -> _error.value = result.error
                }
            } finally {
                _isFollowingLoading.value = false
            }
        }
    }

    private fun loadFollowingUsers() {
        val userId = userId ?: return
        viewModelScope.launch {
            _isFollowingUsersLoading.value = true
            try {
                when (val result = getFollowingUsersUseCase(userId)) {
                    is AppResult.Success -> _followingUsers.value = result.data
                    is AppResult.Failure -> _error.value = result.error
                }
            } finally {
                _isFollowingUsersLoading.value = false
            }
        }
    }
}