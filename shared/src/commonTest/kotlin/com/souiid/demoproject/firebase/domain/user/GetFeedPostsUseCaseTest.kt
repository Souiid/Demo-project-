package com.souiid.demoproject.firebase.domain.user

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.Post
import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.firebase.domain.repository.IPostRepository
import com.souiid.demoproject.firebase.domain.repository.IUserRepository
import com.souiid.demoproject.firebase.domain.usecase.post.GetFeedPostsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.GetFollowingUserIdsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetFeedPostsUseCaseTest {

    private val userRepository = mock<IUserRepository>(MockMode.autoUnit)
    private val postRepository = mock<IPostRepository>(MockMode.autoUnit)
    private val currentUser = MutableStateFlow<User?>(null)

    private val useCase = GetFeedPostsUseCase(
        postRepository,
        GetFollowingUserIdsUseCase(userRepository),
        ObserveCurrentUserUseCase(userRepository)
    )

    private fun mockCurrentUser(userId: String?) {
        currentUser.value = userId?.let {
            User(
                id = it,
                firstName = "John",
                lastName = "Doe",
                description = "Test user"
            )
        }
        every { userRepository.currentUser } returns currentUser
    }

    @Test
    fun noCurrentUserReturnsFailure() = runTest {
        mockCurrentUser(null)

        val result = useCase()

        assertIs<AppResult.Failure>(result)
        verifySuspend(mode = VerifyMode.not) { postRepository.getFeedPosts(any()) }
    }

    @Test
    fun feedIncludesCurrentUserAndFollowingUsers() = runTest {
        mockCurrentUser("user1")
        val authors = listOf("user2", "user3", "user1")
        val posts = listOf(Post("post1", "user2", "Hello", 1000, 0))
        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Success(setOf("user2", "user3"))
        everySuspend { postRepository.getFeedPosts(any()) } returns AppResult.Success(posts)

        val result = useCase()

        assertEquals(AppResult.Success(posts), result)
        verifySuspend { postRepository.getFeedPosts(authors) }
    }

    @Test
    fun feedIncludesOnlyCurrentUserWhenNotFollowingAnyone() = runTest {
        mockCurrentUser("user1")
        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Success(emptySet())
        everySuspend { postRepository.getFeedPosts(listOf("user1")) } returns AppResult.Success(emptyList())

        val result = useCase()

        assertEquals(AppResult.Success(emptyList<Post>()), result)
        verifySuspend { postRepository.getFeedPosts(listOf("user1")) }
    }

    @Test
    fun followingFailureIsPropagated() = runTest {
        mockCurrentUser("user1")
        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Failure(AppError.Unknown())

        val result = useCase()

        assertIs<AppResult.Failure>(result)
        verifySuspend(mode = VerifyMode.not) { postRepository.getFeedPosts(any()) }
    }

    @Test
    fun postRepositoryFailureIsPropagated() = runTest {
        mockCurrentUser("user1")
        val failure = AppResult.Failure(AppError.Unknown())
        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Success(emptySet())
        everySuspend { postRepository.getFeedPosts(listOf("user1")) } returns failure

        val result = useCase()

        assertEquals(failure, result)
        verifySuspend { postRepository.getFeedPosts(listOf("user1")) }
    }
}