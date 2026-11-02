package com.souiid.demoproject.firebase.domain.user

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppError.Field
import com.souiid.demoproject.firebase.domain.model.AppError.ValidationReason
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.repository.IUserRepository
import com.souiid.demoproject.firebase.domain.usecase.user.UpdateUserUseCase
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateUserUseCaseTest {

    private val repository = mock<IUserRepository>(MockMode.autoUnit)
    private val useCase = UpdateUserUseCase(repository)

    @Test
    fun validUserReturnsSuccess() = runTest {
        everySuspend { repository.updateUser("John", "Doe", "Hello", null) } returns AppResult.Success(Unit)

        val result = useCase("John", "Doe", "Hello")

        assertEquals(AppResult.Success(Unit), result)
        verifySuspend { repository.updateUser("John", "Doe", "Hello", null) }
    }

    @Test
    fun firstNameIsBlankReturnsValidationFailure() = runTest {
        val result = useCase("   ", "Doe", "Hello")

        assertEquals(AppResult.Failure(AppError.Validation(Field.FIRST_NAME, ValidationReason.REQUIRED)), result)
        verifySuspend(mode = VerifyMode.not) { repository.updateUser(any(), any(), any(), any()) }
    }

    @Test
    fun lastNameIsBlankReturnsValidationFailure() = runTest {
        val result = useCase("John", "   ", "Hello")

        assertEquals(AppResult.Failure(AppError.Validation(Field.LAST_NAME, ValidationReason.REQUIRED)), result)
        verifySuspend(mode = VerifyMode.not) { repository.updateUser(any(), any(), any(), any()) }
    }

    @Test
    fun firstNameValidationHasPriority() = runTest {
        val result = useCase("", "", "Hello")

        assertEquals(AppResult.Failure(AppError.Validation(Field.FIRST_NAME, ValidationReason.REQUIRED)), result)
        verifySuspend(mode = VerifyMode.not) { repository.updateUser(any(), any(), any(), any()) }
    }

    @Test
    fun fieldsAreTrimmedBeforeUpdating() = runTest {
        everySuspend { repository.updateUser("John", "Doe", "Hello", null) } returns AppResult.Success(Unit)

        val result = useCase("  John  ", "  Doe  ", "  Hello  ")

        assertEquals(AppResult.Success(Unit), result)
        verifySuspend { repository.updateUser("John", "Doe", "Hello", null) }
    }

    @Test
    fun emptyDescriptionIsAccepted() = runTest {
        everySuspend { repository.updateUser("John", "Doe", "", null) } returns AppResult.Success(Unit)

        val result = useCase("John", "Doe", "")

        assertEquals(AppResult.Success(Unit), result)
        verifySuspend { repository.updateUser("John", "Doe", "", null) }
    }

    @Test
    fun photoUrlIsPassedToRepository() = runTest {
        val photoUrl = "https://example.com/photo.jpg"
        everySuspend { repository.updateUser("John", "Doe", "Hello", photoUrl) } returns AppResult.Success(Unit)

        val result = useCase("John", "Doe", "Hello", photoUrl)

        assertEquals(AppResult.Success(Unit), result)
        verifySuspend { repository.updateUser("John", "Doe", "Hello", photoUrl) }
    }

    @Test
    fun repositoryFailureIsPropagated() = runTest {
        val failure = AppResult.Failure(AppError.Unknown())
        everySuspend { repository.updateUser("John", "Doe", "Hello", null) } returns failure

        val result = useCase("John", "Doe", "Hello")

        assertEquals(failure, result)
        verifySuspend { repository.updateUser("John", "Doe", "Hello", null) }
    }
}