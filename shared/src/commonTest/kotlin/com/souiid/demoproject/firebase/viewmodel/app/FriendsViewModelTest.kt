package com.souiid.demoproject.firebase.viewmodel.app

import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.firebase.domain.repository.IUserRepository
import com.souiid.demoproject.firebase.domain.usecase.user.FollowUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.GetFollowingUserIdsUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.GetFollowingUsersUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.SearchUsersUseCase
import com.souiid.demoproject.firebase.domain.usecase.user.UnfollowUserUseCase
import com.souiid.demoproject.ui.screens.app.firebase.FollowViewModel
import com.souiid.demoproject.ui.screens.app.firebase.friends.FriendsViewModel
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
import kotlinx.coroutines.test.advanceTimeBy
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
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class FriendsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = mock<IUserRepository>(MockMode.autoUnit)
    private val currentUser = MutableStateFlow<User?>(null)

    private lateinit var followViewModel: FollowViewModel
    private lateinit var viewModel: FriendsViewModel

    private val user1 = User("user1", "John", "Doe", "Hello")
    private val user2 = User("user2", "Jane", "Smith", "Hi")
    private val user3 = User("user3", "Alex", "Brown", "Hey")

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(dispatcher)
        every { repository.currentUser } returns currentUser

        val observeCurrentUser = ObserveCurrentUserUseCase(repository)
        followViewModel = FollowViewModel(
            FollowUserUseCase(repository),
            UnfollowUserUseCase(repository),
            GetFollowingUserIdsUseCase(repository),
            observeCurrentUser
        )
        viewModel = FriendsViewModel(
            SearchUsersUseCase(repository),
            GetFollowingUsersUseCase(repository),
            observeCurrentUser,
            followViewModel
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateIsEmpty() = runTest(dispatcher) {
        runCurrent()

        assertEquals("", viewModel.searchQuery.value)
        assertEquals(emptyList(), viewModel.users.value)
        assertEquals(emptyList(), viewModel.followingUsers.value)
        assertFalse(viewModel.isLoading.value)
        assertFalse(viewModel.isFollowingUsersLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun searchQueryIsUpdatedImmediately() = runTest(dispatcher) {
        everySuspend { repository.searchUsers("John") } returns AppResult.Success(emptyList())

        viewModel.onSearchQueryChange("John")

        assertEquals("John", viewModel.searchQuery.value)
    }

    @Test
    fun searchStartsAfterDebounce() = runTest(dispatcher) {
        everySuspend { repository.searchUsers("John") } returns AppResult.Success(listOf(user1))
        runCurrent()

        viewModel.onSearchQueryChange("John")
        runCurrent()
        advanceTimeBy(399.milliseconds)
        runCurrent()

        verifySuspend(mode = VerifyMode.not) { repository.searchUsers(any()) }

        advanceTimeBy(1.milliseconds)
        runCurrent()

        assertEquals(listOf(user1), viewModel.users.value)
        assertFalse(viewModel.isLoading.value)
        verifySuspend { repository.searchUsers("John") }
    }

    @Test
    fun rapidSearchChangesOnlySearchLatestQuery() = runTest(dispatcher) {
        everySuspend { repository.searchUsers("Jane") } returns AppResult.Success(listOf(user2))
        runCurrent()

        viewModel.onSearchQueryChange("John")
        runCurrent()
        advanceTimeBy(200.milliseconds)
        runCurrent()

        viewModel.onSearchQueryChange("Jane")
        runCurrent()
        advanceUntilIdle()

        assertEquals(listOf(user2), viewModel.users.value)
        verifySuspend(mode = VerifyMode.not) { repository.searchUsers("John") }
        verifySuspend { repository.searchUsers("Jane") }
    }

    @Test
    fun blankSearchClearsResults() = runTest(dispatcher) {
        everySuspend { repository.searchUsers("John") } returns AppResult.Success(listOf(user1))
        runCurrent()

        viewModel.onSearchQueryChange("John")
        advanceUntilIdle()
        assertEquals(listOf(user1), viewModel.users.value)

        viewModel.onSearchQueryChange("")
        advanceUntilIdle()

        assertEquals(emptyList(), viewModel.users.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun searchFailureSetsError() = runTest(dispatcher) {
        everySuspend { repository.searchUsers("John") } returns AppResult.Failure(AppError.Unknown())
        runCurrent()

        viewModel.onSearchQueryChange("John")
        advanceUntilIdle()

        assertEquals(AppError.Unknown(), viewModel.error.value)
        assertEquals(emptyList(), viewModel.users.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun followingUsersAreLoadedForCurrentUser() = runTest(dispatcher) {
        everySuspend { repository.getFollowingUserIds("user1") } returns AppResult.Success(setOf("user2", "user3"))
        everySuspend { repository.getFollowingUsers("user1") } returns AppResult.Success(listOf(user2, user3))

        currentUser.value = user1
        advanceUntilIdle()

        assertEquals(listOf(user2, user3), viewModel.followingUsers.value)
        assertFalse(viewModel.isFollowingUsersLoading.value)
        verifySuspend { repository.getFollowingUsers("user1") }
    }

    @Test
    fun followingUsersFailureSetsError() = runTest(dispatcher) {
        everySuspend { repository.getFollowingUserIds("user1") } returns AppResult.Success(setOf("user2"))
        everySuspend { repository.getFollowingUsers("user1") } returns AppResult.Failure(AppError.Unknown())

        runCurrent()

        currentUser.value = user1
        runCurrent()

        assertEquals(setOf("user2"), followViewModel.followingUserIds.value)
        assertEquals(AppError.Unknown(), viewModel.error.value)
        assertFalse(viewModel.isFollowingUsersLoading.value)

        verifySuspend { repository.getFollowingUsers("user1") }
    }
}