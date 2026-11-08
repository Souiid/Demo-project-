package com.souiid.demoproject.ktor.viewmodel

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.room.domain.repository.IFavoriteMealRepository
import com.souiid.demoproject.room.domain.usecase.ObserveFavoriteMealsUseCase
import com.souiid.demoproject.ui.screens.app.room.FavoriteMealsViewModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteMealsViewModelTest {

    private val repository = mock<IFavoriteMealRepository>()
    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun favoriteMealsInitiallyEmpty() = runTest(dispatcher) {
        val mealsFlow = MutableStateFlow<List<Meal>>(emptyList())
        every { repository.observeFavorites() } returns mealsFlow

        val viewModel = FavoriteMealsViewModel(ObserveFavoriteMealsUseCase(repository))

        assertTrue(viewModel.favoriteMeals.value.isEmpty())
    }

    @Test
    fun favoriteMealsUpdatesWhenRepositoryEmits() = runTest(dispatcher) {
        val mealsFlow = MutableStateFlow<List<Meal>>(emptyList())
        every { repository.observeFavorites() } returns mealsFlow

        val viewModel = FavoriteMealsViewModel(ObserveFavoriteMealsUseCase(repository))
        val collector = backgroundScope.launch { viewModel.favoriteMeals.collect {} }
        runCurrent()

        val meal = Meal("1", "Pasta", "Vegetarian", "Italian", null, null)
        mealsFlow.value = listOf(meal)
        runCurrent()

        assertEquals(listOf(meal), viewModel.favoriteMeals.value)

        collector.cancelAndJoin()
    }

    @Test
    fun favoriteMealsBecomesEmptyWhenRepositoryEmitsEmptyList() = runTest(dispatcher) {
        val meal = Meal("1", "Pasta", "Vegetarian", "Italian", null, null)
        val mealsFlow = MutableStateFlow(listOf(meal))
        every { repository.observeFavorites() } returns mealsFlow

        val viewModel = FavoriteMealsViewModel(ObserveFavoriteMealsUseCase(repository))
        val collector = backgroundScope.launch { viewModel.favoriteMeals.collect {} }
        runCurrent()

        assertEquals(listOf(meal), viewModel.favoriteMeals.value)

        mealsFlow.value = emptyList()
        runCurrent()

        assertTrue(viewModel.favoriteMeals.value.isEmpty())

        collector.cancelAndJoin()
    }
}