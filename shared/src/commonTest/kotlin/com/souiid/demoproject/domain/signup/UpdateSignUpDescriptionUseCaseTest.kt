package com.souiid.demoproject.domain.signup

import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.repository.ISignUpRepository
import com.souiid.demoproject.domain.usecase.signup.UpdateSignUpDescriptionUseCase
import dev.mokkery.MockMode
import dev.mokkery.mock
import dev.mokkery.verify
import kotlin.test.Test
import kotlin.test.assertIs

class UpdateSignUpDescriptionUseCaseTest {

    private val repository = mock<ISignUpRepository>(MockMode.autoUnit)
    private val useCase = UpdateSignUpDescriptionUseCase(repository)

    @Test
    fun validDescriptionReturnsSuccess() {
        val result = useCase("Hello, I'm John!")

        assertIs<AppResult.Success<Unit>>(result)
        verify { repository.updateDescription("Hello, I'm John!") }
    }

    @Test
    fun descriptionIsTrimmedBeforeUpdatingRepository() {
        val result = useCase("  Hello, I'm John!  ")

        assertIs<AppResult.Success<Unit>>(result)
        verify { repository.updateDescription("Hello, I'm John!") }
    }

    @Test
    fun emptyDescriptionReturnsSuccess() {
        val result = useCase("   ")

        assertIs<AppResult.Success<Unit>>(result)
        verify { repository.updateDescription("") }
    }
}