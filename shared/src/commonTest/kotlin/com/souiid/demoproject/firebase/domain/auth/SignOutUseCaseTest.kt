package com.souiid.demoproject.firebase.domain.auth

import com.souiid.demoproject.firebase.domain.repository.IAuthRepository
import com.souiid.demoproject.firebase.domain.repository.INotificationRepository
import com.souiid.demoproject.firebase.domain.repository.IUserRepository
import com.souiid.demoproject.firebase.domain.usecase.auth.SignOutUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SignOutUseCaseTest {

    private val repository = mock<IAuthRepository>(MockMode.autoUnit)
    private val notificationRepository = mock<INotificationRepository>(MockMode.autoUnit)
    private val userRepository = mock<IUserRepository>()
    private val currentUser = MutableStateFlow<com.souiid.demoproject.firebase.domain.model.User?>(null)

    private val useCase by lazy {
        every { userRepository.currentUser } returns currentUser
        SignOutUseCase(repository, notificationRepository, ObserveCurrentUserUseCase(userRepository))
    }

    @Test
    fun signOutCallsRepository() = runTest {
        useCase()

        verifySuspend { repository.signOut() }
    }

    @Test
    fun signOutWithoutUserSkipsTokenDeletion() = runTest {
        useCase()

        verifySuspend(VerifyMode.not) {
            notificationRepository.deleteToken(any())
        }
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
