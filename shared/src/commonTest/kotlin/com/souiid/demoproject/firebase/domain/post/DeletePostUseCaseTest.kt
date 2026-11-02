package com.souiid.demoproject.firebase.domain.post

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.repository.IPostRepository
import com.souiid.demoproject.firebase.domain.usecase.post.DeletePostUseCase
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DeletePostUseCaseTest {

    private val repository = mock<IPostRepository>(MockMode.autoUnit)
    private val useCase = DeletePostUseCase(repository)

    @Test
    fun deletePostReturnsSuccess() = runTest {
        everySuspend { repository.deletePost("post1") } returns AppResult.Success(Unit)

        val result = useCase("post1")

        assertEquals(AppResult.Success(Unit), result)
        verifySuspend { repository.deletePost("post1") }
    }

    @Test
    fun repositoryFailureIsPropagated() = runTest {
        val failure = AppResult.Failure(AppError.Unknown())
        everySuspend { repository.deletePost("post1") } returns failure

        val result = useCase("post1")

        assertEquals(failure, result)
        verifySuspend { repository.deletePost("post1") }
    }
}