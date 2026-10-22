package com.souiid.demoproject.domain

import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.repository.ISignUpRepository
import com.souiid.demoproject.domain.usecase.signup.UpdateSignUpNameUseCase
import dev.mokkery.MockMode
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UpdateSignUpNameUseCaseTest {

    private val repository = mock<ISignUpRepository>(MockMode.autoUnit)
    private val useCase = UpdateSignUpNameUseCase(repository)

    @Test
    fun emptyFirstNameReturnsRequiredError() = runTest {
        val result = useCase("", "Doe")

        assertEquals(
            AppResult.Failure(AppError.Validation(AppError.Field.FIRST_NAME, AppError.ValidationReason.REQUIRED)),
            result
        )
    }

    @Test
    fun emptyLastNameReturnsRequiredError() = runTest {
        val result = useCase("John", "")

        assertEquals(
            AppResult.Failure(AppError.Validation(AppError.Field.LAST_NAME, AppError.ValidationReason.REQUIRED)),
            result
        )
    }

    @Test
    fun blankFirstNameReturnsRequiredError() = runTest {
        val result = useCase("   ", "Doe")

        assertEquals(
            AppResult.Failure(AppError.Validation(AppError.Field.FIRST_NAME, AppError.ValidationReason.REQUIRED)),
            result
        )
    }

    @Test
    fun validNameReturnsSuccess() = runTest {
        val result = useCase("John", "Doe")

        assertIs<AppResult.Success<Unit>>(result)
        verify { repository.updateFirstName("John") }
        verify { repository.updateLastName("Doe") }
    }

    @Test
    fun nameIsTrimmedBeforeUpdatingRepository() = runTest {
        val result = useCase("  John  ", "  Doe  ")

        assertIs<AppResult.Success<Unit>>(result)
        verify { repository.updateFirstName("John") }
        verify { repository.updateLastName("Doe") }
    }
}