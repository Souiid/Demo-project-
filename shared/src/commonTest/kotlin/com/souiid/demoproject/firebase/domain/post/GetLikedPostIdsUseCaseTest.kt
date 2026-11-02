package com.souiid.demoproject.firebase.domain.post

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.repository.IPostRepository
import com.souiid.demoproject.firebase.domain.usecase.post.GetLikedPostIdsUseCase
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetLikedPostIdsUseCaseTest {

    private val repository = mock<IPostRepository>(MockMode.autoUnit)
    private val useCase = GetLikedPostIdsUseCase(repository)

    @Test
    fun getLikedPostIdsReturnsSuccess() = runTest {
        val postIds = listOf("post1", "post2", "post3")
        val likedIds = setOf("post1", "post3")
        everySuspend { repository.getLikedPostIds(postIds) } returns AppResult.Success(likedIds)

        val result = useCase(postIds)

        assertEquals(AppResult.Success(likedIds), result)
        verifySuspend { repository.getLikedPostIds(postIds) }
    }

    @Test
    fun getLikedPostIdsReturnsEmptySet() = runTest {
        val postIds = listOf("post1", "post2")
        everySuspend { repository.getLikedPostIds(postIds) } returns AppResult.Success(emptySet())

        val result = useCase(postIds)

        assertEquals(AppResult.Success(emptySet<String>()), result)
        verifySuspend { repository.getLikedPostIds(postIds) }
    }

    @Test
    fun repositoryFailureIsPropagated() = runTest {
        val postIds = listOf("post1")
        val failure = AppResult.Failure(AppError.Unknown())
        everySuspend { repository.getLikedPostIds(postIds) } returns failure

        val result = useCase(postIds)

        assertEquals(failure, result)
        verifySuspend { repository.getLikedPostIds(postIds) }
    }
}