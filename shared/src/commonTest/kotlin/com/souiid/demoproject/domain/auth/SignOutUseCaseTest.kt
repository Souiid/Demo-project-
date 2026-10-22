package com.souiid.demoproject.domain.auth

import com.souiid.demoproject.domain.repository.IAuthRepository
import com.souiid.demoproject.domain.usecase.auth.SignOutUseCase
import dev.mokkery.MockMode
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SignOutUseCaseTest {

    private val repository = mock<IAuthRepository>(MockMode.autoUnit)
    private val useCase = SignOutUseCase(repository)

    @Test
    fun signOutCallsRepository() = runTest {
        useCase()

        verifySuspend { repository.signOut() }
    }

    @Test
    fun signOutFailurePropagatesException() = runTest {
        everySuspend { repository.signOut() } throws IllegalStateException("Sign out failed")

        assertFailsWith<IllegalStateException> {
            useCase()
        }
    }
}