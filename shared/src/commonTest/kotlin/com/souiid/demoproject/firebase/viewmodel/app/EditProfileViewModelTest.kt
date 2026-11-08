package com.souiid.demoproject.firebase.viewmodel.app

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppError.Field
import com.souiid.demoproject.firebase.domain.model.AppError.ValidationReason
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.firebase.domain.repository.IUserRepository
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.UpdateUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.UploadPhotoUseCase
import com.souiid.demoproject.ui.screens.app.firebase.editprofile.EditProfileViewModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mock<IUserRepository>(MockMode.autoUnit)
    private val currentUser = MutableStateFlow<User?>(User(
        id = "user1",
        firstName = "John",
        lastName = "Doe",
        description = "Hello"
    ))

    private lateinit var viewModel: EditProfileViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        every { repository.currentUser } returns currentUser

        viewModel = EditProfileViewModel(
            ObserveCurrentUserUseCase(repository),
            UpdateUserUseCase(repository),
            UploadPhotoUseCase(repository)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateContainsCurrentUserData() {
        assertEquals("John", viewModel.firstName)
        assertEquals("Doe", viewModel.lastName)
        assertEquals("Hello", viewModel.description)
        assertNull(viewModel.photoUri)
        assertNull(viewModel.photoMimeType)
        assertFalse(viewModel.isLoading)
        assertNull(viewModel.error)
    }

    @Test
    fun updateUserReturnsSuccess() = runTest(dispatcher) {
        everySuspend { repository.updateUser("John", "Doe", "Hello", null) } returns AppResult.Success(Unit)

        viewModel.updateUser()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertNull(viewModel.error)
        verifySuspend { repository.updateUser("John", "Doe", "Hello", null) }
    }

    @Test
    fun blankFirstNameReturnsValidationError() = runTest(dispatcher) {
        viewModel.firstName = " "

        viewModel.updateUser()
        advanceUntilIdle()

        assertEquals(AppError.Validation(Field.FIRST_NAME, ValidationReason.REQUIRED), viewModel.error)
        assertFalse(viewModel.isLoading)
        verifySuspend(mode = VerifyMode.not) { repository.updateUser(any(), any(), any(), any()) }
    }

    @Test
    fun blankLastNameReturnsValidationError() = runTest(dispatcher) {
        viewModel.lastName = " "

        viewModel.updateUser()
        advanceUntilIdle()

        assertEquals(AppError.Validation(Field.LAST_NAME, ValidationReason.REQUIRED), viewModel.error)
        assertFalse(viewModel.isLoading)
        verifySuspend(mode = VerifyMode.not) { repository.updateUser(any(), any(), any(), any()) }
    }

    @Test
    fun fieldsAreTrimmedBeforeUpdating() = runTest(dispatcher) {
        viewModel.firstName = "  John  "
        viewModel.lastName = "  Doe  "
        viewModel.description = "  Hello  "
        everySuspend { repository.updateUser("John", "Doe", "Hello", null) } returns AppResult.Success(Unit)

        viewModel.updateUser()
        advanceUntilIdle()

        assertNull(viewModel.error)
        verifySuspend { repository.updateUser("John", "Doe", "Hello", null) }
    }

    @Test
    fun photoIsUploadedBeforeUpdatingUser() = runTest(dispatcher) {
        viewModel.photoUri = "content://photo"
        viewModel.photoMimeType = "image/jpeg"
        everySuspend { repository.uploadPhoto("content://photo", "image/jpeg") } returns AppResult.Success("https://example.com/photo.jpg")
        everySuspend { repository.updateUser("John", "Doe", "Hello", "https://example.com/photo.jpg") } returns AppResult.Success(Unit)

        viewModel.updateUser()
        advanceUntilIdle()

        assertNull(viewModel.error)
        assertFalse(viewModel.isLoading)
        verifySuspend { repository.uploadPhoto("content://photo", "image/jpeg") }
        verifySuspend { repository.updateUser("John", "Doe", "Hello", "https://example.com/photo.jpg") }
    }

    @Test
    fun photoUploadFailurePreventsUserUpdate() = runTest(dispatcher) {
        val failure = AppResult.Failure(AppError.Unknown())
        viewModel.photoUri = "content://photo"
        viewModel.photoMimeType = "image/jpeg"
        everySuspend { repository.uploadPhoto("content://photo", "image/jpeg") } returns failure

        viewModel.updateUser()
        advanceUntilIdle()

        assertEquals(failure.error, viewModel.error)
        assertFalse(viewModel.isLoading)
        verifySuspend(mode = VerifyMode.not) { repository.updateUser(any(), any(), any(), any()) }
    }

    @Test
    fun updateUserFailureIsPropagated() = runTest(dispatcher) {
        val failure = AppResult.Failure(AppError.Unknown())
        everySuspend { repository.updateUser("John", "Doe", "Hello", null) } returns failure

        viewModel.updateUser()
        advanceUntilIdle()

        assertEquals(failure.error, viewModel.error)
        assertFalse(viewModel.isLoading)

        viewModel.clearError()

        assertNull(viewModel.error)
    }
}
