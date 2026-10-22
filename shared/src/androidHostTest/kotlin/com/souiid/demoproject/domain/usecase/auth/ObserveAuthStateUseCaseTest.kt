package com.souiid.demoproject.domain.usecase.auth

import com.souiid.demoproject.domain.model.AuthState
import com.souiid.demoproject.domain.repository.IAuthRepository
import dev.gitlive.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveAuthStateUseCaseTest {

    private val repository = mockk<IAuthRepository>()
    private val authState = MutableStateFlow<FirebaseUser?>(null)
    private val useCase = ObserveAuthStateUseCase(repository)

    @Test
    fun unauthenticatedUserReturnsUnauthenticated() = runTest {
        every { repository.authState } returns authState

        assertEquals(AuthState.UNAUTHENTICATED, useCase().first())
    }

    @Test
    fun authenticatedUserWithoutProfileReturnsLoading() = runTest {
        val user = mockk<FirebaseUser>()
        every { user.uid } returns "user123"
        every { repository.authState } returns authState
        every { repository.observeUserProfile("user123") } returns MutableStateFlow(false)
        authState.value = user

        val results = mutableListOf<AuthState>()
        backgroundScope.launch { useCase().collect { results.add(it) } }
        runCurrent()

        assertEquals(AuthState.LOADING, results.last())
    }

    @Test
    fun authenticatedUserWithProfileReturnsAuthenticated() = runTest {
        val user = mockk<FirebaseUser>()
        every { user.uid } returns "user123"
        every { repository.authState } returns authState
        every { repository.observeUserProfile("user123") } returns MutableStateFlow(true)
        authState.value = user

        val results = mutableListOf<AuthState>()
        backgroundScope.launch { useCase().collect { results.add(it) } }
        runCurrent()

        assertEquals(AuthState.AUTHENTICATED, results.last())
    }

    @Test
    fun profileCreationChangesLoadingToAuthenticated() = runTest {
        val user = mockk<FirebaseUser>()
        val profileExists = MutableStateFlow(false)
        every { user.uid } returns "user123"
        every { repository.authState } returns authState
        every { repository.observeUserProfile("user123") } returns profileExists
        authState.value = user

        val results = mutableListOf<AuthState>()
        backgroundScope.launch { useCase().collect { results.add(it) } }
        runCurrent()

        assertEquals(AuthState.LOADING, results.last())

        profileExists.value = true
        runCurrent()

        assertEquals(AuthState.AUTHENTICATED, results.last())
    }

    @Test
    fun signOutChangesStateToUnauthenticated() = runTest {
        val user = mockk<FirebaseUser>()
        every { user.uid } returns "user123"
        every { repository.authState } returns authState
        every { repository.observeUserProfile("user123") } returns MutableStateFlow(true)
        authState.value = user

        val results = mutableListOf<AuthState>()
        backgroundScope.launch { useCase().collect { results.add(it) } }
        runCurrent()

        assertEquals(AuthState.AUTHENTICATED, results.last())

        authState.value = null
        runCurrent()

        assertEquals(AuthState.UNAUTHENTICATED, results.last())
    }

    @Test
    fun signOutStopsObservingPreviousProfile() = runTest {
        val user = mockk<FirebaseUser>()
        val profileExists = MutableStateFlow(true)
        every { user.uid } returns "user123"
        every { repository.authState } returns authState
        every { repository.observeUserProfile("user123") } returns profileExists
        authState.value = user

        val results = mutableListOf<AuthState>()
        backgroundScope.launch { useCase().collect { results.add(it) } }
        runCurrent()

        assertEquals(AuthState.AUTHENTICATED, results.last())

        authState.value = null
        runCurrent()

        profileExists.value = false
        runCurrent()

        assertEquals(AuthState.UNAUTHENTICATED, results.last())
    }
}