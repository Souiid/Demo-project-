package com.souiid.demoproject.firebase.domain.post

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.repository.IPostRepository
import com.souiid.demoproject.firebase.domain.usecase.post.CreatePostUseCase
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CreatePostUseCaseTest {

    private val repository = mock<IPostRepository>(MockMode.autoUnit)
    private val useCase = CreatePostUseCase(repository)

    @Test
    fun validContentReturnsSuccess() = runTest {
        everySuspend { repository.createPost("Hello world") } returns AppResult.Success(Unit)

        val result = useCase("Hello world")

        assertEquals(AppResult.Success(Unit), result)
        verifySuspend { repository.createPost("Hello world") }
    }

    @Test
    fun contentIsTrimmedBeforeCreatingPost() = runTest {
        everySuspend { repository.createPost("Hello world") } returns AppResult.Success(Unit)

        val result = useCase("  Hello world  ")

        assertEquals(AppResult.Success(Unit), result)
        verifySuspend { repository.createPost("Hello world") }
    }

    @Test
    fun emptyContentReturnsFailure() = runTest {
        val result = useCase("")

        assertEquals(AppResult.Failure(AppError.Unknown()), result)
        verifySuspend(mode = VerifyMode.not) { repository.createPost("Test") }
    }

    @Test
    fun blankContentReturnsFailure() = runTest {
        val result = useCase("   ")

        assertEquals(AppResult.Failure(AppError.Unknown()), result)
        verifySuspend(mode = VerifyMode.not) { repository.createPost("Test") }
    }

    @Test
    fun contentLongerThanThousandCharactersReturnsFailure() = runTest {
        val content = "a".repeat(1001)

        val result = useCase(content)

        assertEquals(AppResult.Failure(AppError.Unknown()), result)
        verifySuspend(mode = VerifyMode.not) { repository.createPost(content) }
    }

    @Test
    fun contentWithExactlyThousandCharactersReturnsSuccess() = runTest {
        val content = "a".repeat(1000)
        everySuspend { repository.createPost(content) } returns AppResult.Success(Unit)

        val result = useCase(content)

        assertEquals(AppResult.Success(Unit), result)
        verifySuspend { repository.createPost(content) }
    }

    @Test
    fun repositoryFailureIsPropagated() = runTest {
        val failure = AppResult.Failure(AppError.Unknown())
        everySuspend { repository.createPost("Hello world") } returns failure

        val result = useCase("Hello world")

        assertEquals(failure, result)
        verifySuspend { repository.createPost("Hello world") }
    }
}