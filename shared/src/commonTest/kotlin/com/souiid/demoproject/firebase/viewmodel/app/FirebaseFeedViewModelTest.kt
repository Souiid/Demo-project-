package com.souiid.demoproject.firebase.viewmodel.app

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.Post
import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.firebase.domain.repository.INotificationRepository
import com.souiid.demoproject.firebase.domain.repository.IPostRepository
import com.souiid.demoproject.firebase.domain.repository.IUserRepository
import com.souiid.demoproject.firebase.domain.usecase.notification.SaveNotificationTokenUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.CreatePostUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.GetLikedPostIdsUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.LikePostUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.ObserveFeedPostsUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.UnlikePostUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.FetchUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.GetFollowingUserIdsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.FollowUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.UnfollowUserUseCase
import com.souiid.demoproject.ui.screens.app.firebase.FollowViewModel
import com.souiid.demoproject.ui.screens.app.firebase.feed.FirebaseFeedViewModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.collections.emptySet
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseFeedViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val userRepository = mock<IUserRepository>(MockMode.autoUnit)
    private val postRepository = mock<IPostRepository>(MockMode.autoUnit)
    private val notificationRepository = mock<INotificationRepository>(MockMode.autoUnit)

    private val currentUser = MutableStateFlow<User?>(null)

    private val observeCurrentUserUseCase = ObserveCurrentUserUseCase(userRepository)

    private lateinit var followViewModel: FollowViewModel
    private lateinit var viewModel: FirebaseFeedViewModel

    private val user1 = User("user1", "John", "Doe", "Hello")

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        every { userRepository.currentUser } returns currentUser

        followViewModel = FollowViewModel(
            FollowUserUseCase(userRepository),
            UnfollowUserUseCase(userRepository),
            GetFollowingUserIdsUseCase(userRepository),
            observeCurrentUserUseCase
        )

        viewModel = FirebaseFeedViewModel(
            FetchUserUseCase(userRepository),
            CreatePostUseCase(postRepository),
            ObserveFeedPostsUseCase(postRepository, observeCurrentUserUseCase),
            observeCurrentUserUseCase,
            followViewModel,
            LikePostUseCase(postRepository),
            UnlikePostUseCase(postRepository),
            GetLikedPostIdsUseCase(postRepository),
            SaveNotificationTokenUseCase(notificationRepository)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun createPostSuccess() = runTest(dispatcher) {
        everySuspend { postRepository.createPost("Hello world") } returns AppResult.Success(Unit)

        viewModel.createPost("Hello world")
        advanceUntilIdle()

        assertTrue(viewModel.isPostCreated)
        assertFalse(viewModel.isPublishing)
        assertNull(viewModel.error)

        verifySuspend { postRepository.createPost("Hello world") }
    }

    @Test
    fun createPostFailure() = runTest(dispatcher) {
        everySuspend { postRepository.createPost("Hello world") } returns AppResult.Failure(AppError.Unknown())

        viewModel.createPost("Hello world")
        advanceUntilIdle()

        assertFalse(viewModel.isPostCreated)
        assertFalse(viewModel.isPublishing)
        assertEquals(AppError.Unknown(), viewModel.error)

        verifySuspend { postRepository.createPost("Hello world") }
    }

    @Test
    fun observePostsLoadsFeed() = runTest(dispatcher) {
        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Success(emptySet())
        every { postRepository.observeFeedPosts(listOf("user1")) } returns flowOf(emptyList())

        currentUser.value = user1
        runCurrent()

        viewModel.observePosts()
        advanceUntilIdle()

        assertTrue(viewModel.posts.isEmpty())
        assertFalse(viewModel.isLoadingPosts)
        assertNull(viewModel.error)

        verify { postRepository.observeFeedPosts(listOf("user1")) }
    }

    @Test
    fun toggleLikeUpdatesPost() = runTest(dispatcher) {
        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Success(emptySet())
        every { postRepository.observeFeedPosts(emptyList()) } returns flowOf(emptyList())

        currentUser.value = user1
        runCurrent()

        viewModel.observePosts()
        advanceUntilIdle()

        viewModel.toggleLike("post1")

        assertFalse("post1" in viewModel.likedPostIds)
        assertTrue(viewModel.posts.isEmpty())
    }

    @Test
    fun clearResetsState() = runTest(dispatcher) {
        everySuspend { postRepository.createPost("Hello world") } returns AppResult.Success(Unit)

        viewModel.createPost("Hello world")
        advanceUntilIdle()

        assertTrue(viewModel.isPostCreated)

        viewModel.clear()

        assertTrue(viewModel.posts.isEmpty())
        assertTrue(viewModel.authors.isEmpty())
        assertTrue(viewModel.likedPostIds.isEmpty())
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isLoadingPosts)
        assertFalse(viewModel.isPublishing)
        assertFalse(viewModel.isPostCreated)
        assertNull(viewModel.error)
    }


    @Test
    fun createPostWithBlankContentDoesNothing() = runTest(dispatcher) {
        viewModel.createPost("   ")
        advanceUntilIdle()

        assertFalse(viewModel.isPublishing)
        assertFalse(viewModel.isPostCreated)
        assertNull(viewModel.error)

        verifySuspend(mode = VerifyMode.not) {
            postRepository.createPost("   ")
        }
    }

    @Test
    fun createPostWithTooLongContentDoesNothing() = runTest(dispatcher) {
        val content = "a".repeat(1001)

        viewModel.createPost(content)
        advanceUntilIdle()

        assertFalse(viewModel.isPublishing)
        assertFalse(viewModel.isPostCreated)
        assertNull(viewModel.error)

        verifySuspend(mode = VerifyMode.not) {
            postRepository.createPost(content)
        }
    }

    @Test
    fun onPostCreatedHandledResetsFlag() = runTest(dispatcher) {
        everySuspend { postRepository.createPost("Hello world") } returns AppResult.Success(Unit)

        viewModel.createPost("Hello world")
        advanceUntilIdle()

        assertTrue(viewModel.isPostCreated)

        viewModel.onPostCreatedHandled()

        assertFalse(viewModel.isPostCreated)
    }

    @Test
    fun refreshPostsRestartsObservation() = runTest(dispatcher) {
        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Success(emptySet())
        every { postRepository.observeFeedPosts(listOf("user1")) } returns flowOf(emptyList())

        currentUser.value = user1
        runCurrent()

        viewModel.observePosts()
        advanceUntilIdle()

        viewModel.refreshPosts()
        advanceUntilIdle()

        assertTrue(viewModel.posts.isEmpty())
        assertFalse(viewModel.isLoadingPosts)
        assertNull(viewModel.error)

        verify(mode = VerifyMode.exactly(2)) {
            postRepository.observeFeedPosts(listOf("user1"))
        }
    }

    @Test
    fun observePostsWithoutCurrentUserDoesNothing() = runTest(dispatcher) {
        viewModel.observePosts()
        advanceUntilIdle()

        assertTrue(viewModel.posts.isEmpty())
        assertFalse(viewModel.isLoadingPosts)
        assertNull(viewModel.error)

        verify(mode = VerifyMode.not) {
            postRepository.observeFeedPosts(listOf("user1"))
        }
    }


    @Test
    fun toggleLikeUpdatesPostOptimistically() = runTest(dispatcher) {
        val post = Post(id = "post1", authorId = "user1", content = "Hello", likesCount = 2)

        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Success(emptySet())
        every { postRepository.observeFeedPosts(listOf("user1")) } returns flowOf(listOf(post))
        everySuspend { userRepository.fetchUser("user1") } returns AppResult.Success(user1)
        everySuspend { postRepository.getLikedPostIds(listOf("post1")) } returns AppResult.Success(emptySet())
        everySuspend { postRepository.likePost("post1") } returns AppResult.Success(Unit)

        currentUser.value = user1
        runCurrent()
        viewModel.observePosts()
        runCurrent()

        viewModel.toggleLike("post1")

        assertTrue("post1" in viewModel.likedPostIds)
        assertEquals(3, viewModel.posts.first().likesCount)

        advanceTimeBy(500.milliseconds)
        runCurrent()

        verifySuspend { postRepository.likePost("post1") }
    }

    @Test
    fun toggleUnlikeUpdatesPostOptimistically() = runTest(dispatcher) {
        val post = Post(id = "post1", authorId = "user1", content = "Hello", likesCount = 2)

        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Success(emptySet())
        every { postRepository.observeFeedPosts(listOf("user1")) } returns flowOf(listOf(post))
        everySuspend { userRepository.fetchUser("user1") } returns AppResult.Success(user1)
        everySuspend { postRepository.getLikedPostIds(listOf("post1")) } returns AppResult.Success(setOf("post1"))
        everySuspend { postRepository.unlikePost("post1") } returns AppResult.Success(Unit)

        currentUser.value = user1
        runCurrent()
        viewModel.observePosts()
        runCurrent()

        viewModel.toggleLike("post1")

        assertFalse("post1" in viewModel.likedPostIds)
        assertEquals(1, viewModel.posts.first().likesCount)

        advanceTimeBy(500.milliseconds)
        runCurrent()

        verifySuspend { postRepository.unlikePost("post1") }
    }

    @Test
    fun toggleLikeWaitsForDebounce() = runTest(dispatcher) {
        val post = Post(id = "post1", authorId = "user1", content = "Hello")

        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Success(emptySet())
        every { postRepository.observeFeedPosts(listOf("user1")) } returns flowOf(listOf(post))
        everySuspend { userRepository.fetchUser("user1") } returns AppResult.Success(user1)
        everySuspend { postRepository.getLikedPostIds(listOf("post1")) } returns AppResult.Success(emptySet())
        everySuspend { postRepository.likePost("post1") } returns AppResult.Success(Unit)

        currentUser.value = user1
        runCurrent()
        viewModel.observePosts()
        runCurrent()

        viewModel.toggleLike("post1")

        advanceTimeBy(499.milliseconds)
        runCurrent()

        verifySuspend(mode = VerifyMode.not) { postRepository.likePost("post1") }

        advanceTimeBy(1.milliseconds)
        runCurrent()

        verifySuspend { postRepository.likePost("post1") }
    }

    @Test
    fun toggleLikeFailureRollsBack() = runTest(dispatcher) {
        val post = Post(id = "post1", authorId = "user1", content = "Hello", likesCount = 2)

        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Success(emptySet())
        every { postRepository.observeFeedPosts(listOf("user1")) } returns flowOf(listOf(post))
        everySuspend { userRepository.fetchUser("user1") } returns AppResult.Success(user1)
        everySuspend { postRepository.getLikedPostIds(listOf("post1")) } returns AppResult.Success(emptySet())
        everySuspend { postRepository.likePost("post1") } returns AppResult.Failure(AppError.Unknown())

        currentUser.value = user1
        runCurrent()
        viewModel.observePosts()
        runCurrent()

        viewModel.toggleLike("post1")

        assertTrue("post1" in viewModel.likedPostIds)
        assertEquals(3, viewModel.posts.first().likesCount)

        advanceTimeBy(500.milliseconds)
        runCurrent()

        assertFalse("post1" in viewModel.likedPostIds)
        assertEquals(2, viewModel.posts.first().likesCount)
        assertEquals(AppError.Unknown(), viewModel.error)
    }

    @Test
    fun doubleToggleLikeDoesNotCallRepository() = runTest(dispatcher) {
        val post = Post(id = "post1", authorId = "user1", content = "Hello", likesCount = 2)

        everySuspend { userRepository.getFollowingUserIds("user1") } returns AppResult.Success(emptySet())
        every { postRepository.observeFeedPosts(listOf("user1")) } returns flowOf(listOf(post))
        everySuspend { userRepository.fetchUser("user1") } returns AppResult.Success(user1)
        everySuspend { postRepository.getLikedPostIds(listOf("post1")) } returns AppResult.Success(emptySet())

        currentUser.value = user1
        runCurrent()
        viewModel.observePosts()
        runCurrent()

        viewModel.toggleLike("post1")
        viewModel.toggleLike("post1")

        advanceTimeBy(500.milliseconds)
        runCurrent()

        assertFalse("post1" in viewModel.likedPostIds)
        assertEquals(2, viewModel.posts.first().likesCount)

        verifySuspend(mode = VerifyMode.not) { postRepository.likePost("post1") }
        verifySuspend(mode = VerifyMode.not) { postRepository.unlikePost("post1") }
    }


}
