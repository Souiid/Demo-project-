package com.souiid.demoproject.ui.screens.app.firebase.feed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.usecase.auth.SignOutUseCase
import com.souiid.demoproject.domain.usecase.post.CreatePostUseCase
import com.souiid.demoproject.domain.usecase.user.FetchUserUseCase
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch

class FirebaseFeedViewModel(
    private val signOutUseCase: SignOutUseCase,
    private val fetchUserUseCase: FetchUserUseCase,
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {

    var error by mutableStateOf<AppError?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var content by mutableStateOf("")
        private set

    var isPublishing by mutableStateOf(false)
        private set

    var isPostCreated by mutableStateOf(false)
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

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }
}