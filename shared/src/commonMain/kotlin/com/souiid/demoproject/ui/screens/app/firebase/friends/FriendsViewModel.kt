package com.souiid.demoproject.ui.screens.app.firebase.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.model.User
import com.souiid.demoproject.domain.usecase.user.SearchUsersUseCase
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
    private val searchUsersUseCase: SearchUsersUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<AppError?>(null)
    val error = _error.asStateFlow()

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
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}