package com.souiid.demoproject.firebase.viewmodel.app

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.ProfileStats
import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.firebase.domain.repository.IAuthRepository
import com.souiid.demoproject.firebase.domain.repository.INotificationRepository
import com.souiid.demoproject.firebase.domain.repository.IPostRepository
import com.souiid.demoproject.firebase.domain.repository.IUserRepository
import com.souiid.demoproject.firebase.domain.usecase.auth.SignOutUseCase
import com.souiid.demoproject.firebase.domain.usecase.post.ObserveUserPostsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.FetchUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveProfileFollowsUseCase
import com.souiid.demoproject.ui.screens.app.firebase.profile.FirebaseProfileViewModel
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseProfileViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val userRepository = mock<IUserRepository>(MockMode.autoUnit)
    private val postRepository = mock<IPostRepository>(MockMode.autoUnit)
    private val authRepository = mock<IAuthRepository>(MockMode.autoUnit)
    private val notificationRepository = mock<INotificationRepository>(MockMode.autoUnit)

    private val fetchUserUseCase = FetchUserUseCase(userRepository)
    private val observeCurrentUserUseCase = ObserveCurrentUserUseCase(userRepository)
    private val signOutUseCase = SignOutUseCase(authRepository, notificationRepository, observeCurrentUserUseCase)
    private val observeProfileFollowsUseCase = ObserveProfileFollowsUseCase(userRepository)
    private val observeUserPostsUseCase = ObserveUserPostsUseCase(postRepository)

    private val currentUser = MutableStateFlow<User?>(null)
    private lateinit var viewModel: FirebaseProfileViewModel

    private val user1 = User("user1", "John", "Doe", "Hello")
    private val user2 = User("user2", "Jane", "Smith", "Hi")

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        every { userRepository.currentUser } returns currentUser

        viewModel = FirebaseProfileViewModel(
            fetchUserUseCase,
            observeCurrentUserUseCase,
            signOutUseCase,
            observeProfileFollowsUseCase,
            observeUserPostsUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun mockStats(userId: String, followers: Int = 2, following: Int = 3) {
        every { userRepository.observeFollowerUserIds(userId) } returns flowOf((1..followers).map { "follower$it" }.toSet())
        every { userRepository.observeFollowingUserIds(userId) } returns flowOf((1..following).map { "following$it" }.toSet())
        every { postRepository.observeUserPosts(userId) } returns flowOf(emptyList())
    }

    @Test
    fun initialStateIsEmpty() {
        assertNull(viewModel.displayedUser.value)
        assertEquals(ProfileStats(), viewModel.profileStats.value)
        assertFalse(viewModel.hasLoadedStats)
        assertFalse(viewModel.isLoading)
        assertNull(viewModel.error)
    }

    @Test
    fun loadCurrentUserUsesLocalUser() = runTest(dispatcher) {
        currentUser.value = user1
        mockStats("user1")

        viewModel.loadUser(null)
        advanceUntilIdle()

        assertEquals(user1, viewModel.displayedUser.value)
        assertFalse(viewModel.isLoading)
        assertNull(viewModel.error)
        assertTrue(viewModel.hasLoadedStats)
        assertEquals(ProfileStats(posts = 0, followers = 2, following = 3), viewModel.profileStats.value)

        verifySuspend(mode = VerifyMode.not) { userRepository.fetchUser("user1") }
    }

    @Test
    fun loadOtherUserFetchesUser() = runTest(dispatcher) {
        everySuspend { userRepository.fetchUser("user2") } returns AppResult.Success(user2)
        mockStats("user2")

        viewModel.loadUser("user2")
        advanceUntilIdle()

        assertEquals(user2, viewModel.displayedUser.value)
        assertFalse(viewModel.isLoading)
        assertNull(viewModel.error)
        assertTrue(viewModel.hasLoadedStats)

        verifySuspend { userRepository.fetchUser("user2") }
    }

    @Test
    fun loadUserFailureSetsError() = runTest(dispatcher) {
        everySuspend { userRepository.fetchUser("user2") } returns AppResult.Failure(AppError.Unknown())

        viewModel.loadUser("user2")
        advanceUntilIdle()

        assertNull(viewModel.displayedUser.value)
        assertEquals(AppError.Unknown(), viewModel.error)
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.hasLoadedStats)

        verifySuspend { userRepository.fetchUser("user2") }
    }

    @Test
    fun loadUserWithoutIdDoesNothing() = runTest(dispatcher) {
        viewModel.loadUser(null)
        advanceUntilIdle()

        assertNull(viewModel.displayedUser.value)
        assertFalse(viewModel.isLoading)
        assertNull(viewModel.error)
    }

    @Test
    fun profileStatsAreLoaded() = runTest(dispatcher) {
        currentUser.value = user1
        mockStats("user1", followers = 5, following = 8)

        viewModel.loadUser(null)
        advanceUntilIdle()

        assertEquals(ProfileStats(posts = 0, followers = 5, following = 8), viewModel.profileStats.value)
        assertTrue(viewModel.hasLoadedStats)
    }

    @Test
    fun loadingAnotherUserResetsPreviousStats() = runTest(dispatcher) {
        currentUser.value = user1
        mockStats("user1", followers = 5, following = 8)

        viewModel.loadUser(null)
        advanceUntilIdle()

        assertTrue(viewModel.hasLoadedStats)

        everySuspend { userRepository.fetchUser("user2") } returns AppResult.Success(user2)
        mockStats("user2", followers = 1, following = 2)

        viewModel.loadUser("user2")

        assertNull(viewModel.displayedUser.value)
        assertEquals(ProfileStats(), viewModel.profileStats.value)
        assertFalse(viewModel.hasLoadedStats)
        assertTrue(viewModel.isLoading)

        advanceUntilIdle()

        assertEquals(user2, viewModel.displayedUser.value)
        assertEquals(ProfileStats(posts = 0, followers = 1, following = 2), viewModel.profileStats.value)
        assertTrue(viewModel.hasLoadedStats)

        verifySuspend { userRepository.fetchUser("user2") }
    }

    @Test
    fun currentUserChangesUpdateDisplayedProfile() = runTest(dispatcher) {
        currentUser.value = user1
        mockStats("user1")

        viewModel.loadUser(null)
        advanceUntilIdle()

        val updatedUser = user1.copy(firstName = "Johnny")
        currentUser.value = updatedUser
        runCurrent()

        assertEquals(updatedUser, viewModel.displayedUser.value)
    }

    @Test
    fun signOutResetsProfileState() = runTest(dispatcher) {
        currentUser.value = user1
        mockStats("user1")

        viewModel.loadUser(null)
        advanceUntilIdle()

        everySuspend { notificationRepository.deleteToken("user1") } returns Unit
        everySuspend { authRepository.signOut() } returns Unit

        viewModel.signOut()
        advanceUntilIdle()

        assertNull(viewModel.displayedUser.value)
        assertEquals(ProfileStats(), viewModel.profileStats.value)
        assertFalse(viewModel.hasLoadedStats)
        assertFalse(viewModel.isLoading)
        assertNull(viewModel.error)

        verifySuspend { notificationRepository.deleteToken("user1") }
        verifySuspend { authRepository.signOut() }
    }
}