package com.souiid.demoproject.domain.signup

import com.souiid.demoproject.domain.model.SignUpInfo
import com.souiid.demoproject.domain.repository.ISignUpRepository
import com.souiid.demoproject.domain.usecase.signup.ObserveSignUpInfoUseCase
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ObserveSignUpInfoUseCaseTest {

    private val repository = mock<ISignUpRepository>()
    private val signUpInfo = MutableStateFlow(SignUpInfo())
    private val useCase = ObserveSignUpInfoUseCase(repository)

    @Test
    fun initialSignUpInfoIsReturned() = runTest {
        every { repository.signUpInfo } returns signUpInfo

        val result = useCase()

        assertEquals(SignUpInfo(), result.value)
        assertSame(signUpInfo, result)
    }

    @Test
    fun signUpInfoUpdatesAreObserved() = runTest {
        every { repository.signUpInfo } returns signUpInfo

        val result = useCase()

        signUpInfo.value = SignUpInfo(firstName = "John", lastName = "Doe")

        assertEquals("John", result.value.firstName)
        assertEquals("Doe", result.value.lastName)
    }
}