package com.souiid.demoproject.firebase.domain.user

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.Post
import com.souiid.demoproject.firebase.domain.model.ProfileStats
import com.souiid.demoproject.firebase.domain.repository.IPostRepository
import com.souiid.demoproject.firebase.domain.repository.IUserRepository
import com.souiid.demoproject.firebase.domain.usecase.post.GetUserPostsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.GetFollowerUserIdsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.GetFollowingUserIdsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.GetProfileStatsUseCase
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetProfileStatsUseCaseTest {

    private val userRepository = mock<IUserRepository>(MockMode.autoUnit)
    private val postRepository = mock<IPostRepository>(MockMode.autoUnit)

    private val useCase = GetProfileStatsUseCase(
        GetFollowerUserIdsUseCase(userRepository),
        GetFollowingUserIdsUseCase(userRepository),
        GetUserPostsUseCase(postRepository)
    )

    @Test
    fun getProfileStatsReturnsCorrectCounts() = runTest {
        everySuspend { userRepository.getFollowerUserIds("user1") } returns AppResult.Success(setOf("user2", "user3"))
        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Success(setOf("user4"))
        everySuspend { postRepository.getUserPosts("user1") } returns AppResult.Success(
            listOf(
                Post("post1", "user1", "Hello", 1000, 0),
                Post("post2", "user1", "World", 2000, 0)
            )
        )

        val result = useCase("user1")

        assertEquals(AppResult.Success(ProfileStats(posts = 2, followers = 2, following = 1)), result)
        verifySuspend { userRepository.getFollowerUserIds("user1") }
        verifySuspend { userRepository.getFollowingUserIds("user1") }
        verifySuspend { postRepository.getUserPosts("user1") }
    }

    @Test
    fun getProfileStatsReturnsZeroCountsWhenListsAreEmpty() = runTest {
        everySuspend { userRepository.getFollowerUserIds("user1") } returns AppResult.Success(emptySet())
        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Success(emptySet())
        everySuspend { postRepository.getUserPosts("user1") } returns AppResult.Success(emptyList())

        val result = useCase("user1")

        assertEquals(AppResult.Success(ProfileStats(posts = 0, followers = 0, following = 0)), result)
    }

    @Test
    fun followerFailureIsPropagated() = runTest {
        val failure = AppResult.Failure(AppError.Unknown())
        everySuspend { userRepository.getFollowerUserIds("user1") } returns failure

        val result = useCase("user1")

        assertEquals(failure, result)
    }

    @Test
    fun followingFailureIsPropagated() = runTest {
        val failure = AppResult.Failure(AppError.Unknown())
        everySuspend { userRepository.getFollowerUserIds("user1") } returns AppResult.Success(emptySet())
        everySuspend { userRepository.getFollowingUserIds("user1") } returns failure

        val result = useCase("user1")

        assertEquals(failure, result)
    }

    @Test
    fun postsFailureIsPropagated() = runTest {
        val failure = AppResult.Failure(AppError.Unknown())
        everySuspend { userRepository.getFollowerUserIds("user1") } returns AppResult.Success(emptySet())
        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Success(emptySet())
        everySuspend { postRepository.getUserPosts("user1") } returns failure

        val result = useCase("user1")

        assertEquals(failure, result)
    }
}