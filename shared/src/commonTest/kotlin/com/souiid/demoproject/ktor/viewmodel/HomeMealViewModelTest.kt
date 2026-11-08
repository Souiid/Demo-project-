package com.souiid.demoproject.ktor.viewmodel

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.model.MealCategory
import com.souiid.demoproject.ktor.domain.repository.IMealRepository
import com.souiid.demoproject.ktor.domain.usecase.GetMealCategoriesUseCase
import com.souiid.demoproject.ktor.domain.usecase.GetMealsByCategoryUseCase
import com.souiid.demoproject.ktor.domain.usecase.SearchMealsUseCase
import com.souiid.demoproject.ui.screens.app.ktor.home.HomeMealViewModel
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class HomeMealViewModelTest {

    private val repository = mock<IMealRepository>()
    private val category = MealCategory("1", "Vegetarian", "https://example.com/image.jpg", "Vegetarian meals")
    private val meal = Meal("1", "Vegetable Pasta", "Vegetarian", null, null, null)

    private val categoriesUseCase = GetMealCategoriesUseCase(repository)
    private val mealsByCategoryUseCase = GetMealsByCategoryUseCase(repository)
    private val searchMealsUseCase = SearchMealsUseCase(repository)

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): HomeMealViewModel {
        return HomeMealViewModel(categoriesUseCase, mealsByCategoryUseCase, searchMealsUseCase)
    }

    private fun mockInitialCategories(categories: List<MealCategory> = listOf(category)) {
        everySuspend { repository.getCategories() } returns categories
        everySuspend { repository.getMealsByCategory(category.name) } returns listOf(meal)
    }

    @Test
    fun loadCategoriesSuccessfully() = runTest(dispatcher) {
        mockInitialCategories()

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(listOf(category), viewModel.categories.value)
        assertEquals(category, viewModel.selectedCategory.value)
        assertEquals(listOf(meal), viewModel.meals.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun loadCategoriesWithEmptyList() = runTest(dispatcher) {
        mockInitialCategories(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.categories.value.isEmpty())
        assertNull(viewModel.selectedCategory.value)
        assertTrue(viewModel.meals.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun loadCategoriesWithError() = runTest(dispatcher) {
        everySuspend { repository.getCategories() } throws Exception("Failed to load categories")

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals("Failed to load categories", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.categories.value.isEmpty())
    }

    @Test
    fun selectCategorySuccessfully() = runTest(dispatcher) {
        mockInitialCategories(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.selectCategory(category)
        advanceUntilIdle()

        assertEquals(category, viewModel.selectedCategory.value)
        assertEquals(listOf(meal), viewModel.meals.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun selectSameCategoryTwice() = runTest(dispatcher) {
        mockInitialCategories()

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.selectCategory(category)
        advanceUntilIdle()

        verifySuspend { repository.getMealsByCategory(category.name) }
    }

    @Test
    fun updateSearchQuerySuccessfully() = runTest(dispatcher) {
        mockInitialCategories(emptyList())
        everySuspend { repository.searchMeals("Pasta") } returns listOf(meal)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateSearchQuery("  Pasta  ")

        advanceTimeBy(399.milliseconds)
        runCurrent()
        assertTrue(viewModel.meals.value.isEmpty())

        advanceTimeBy(1.milliseconds)
        runCurrent()

        assertEquals("  Pasta  ", viewModel.searchQuery.value)
        assertEquals(listOf(meal), viewModel.meals.value)
        assertFalse(viewModel.isLoading.value)
        verifySuspend { repository.searchMeals("Pasta") }
    }

    @Test
    fun updateSearchQueryCancelsPreviousSearch() = runTest(dispatcher) {
        mockInitialCategories(emptyList())
        everySuspend { repository.searchMeals("Pasta") } returns listOf(meal)

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateSearchQuery("Pizza")
        advanceTimeBy(200.milliseconds)
        viewModel.updateSearchQuery("Pasta")

        advanceTimeBy(400.milliseconds)
        runCurrent()

        assertEquals("Pasta", viewModel.searchQuery.value)
        assertEquals(listOf(meal), viewModel.meals.value)
        verifySuspend { repository.searchMeals("Pasta") }
    }

    @Test
    fun updateSearchQueryWithBlankQuery() = runTest(dispatcher) {
        mockInitialCategories()

        val viewModel = createViewModel()
        advanceUntilIdle()

        everySuspend { repository.searchMeals("Pizza") } returns emptyList()
        viewModel.updateSearchQuery("Pizza")
        advanceUntilIdle()

        viewModel.updateSearchQuery("")
        advanceUntilIdle()

        assertEquals("", viewModel.searchQuery.value)
        assertEquals(category, viewModel.selectedCategory.value)
        assertEquals(listOf(meal), viewModel.meals.value)
    }

    @Test
    fun searchMealsWithError() = runTest(dispatcher) {
        mockInitialCategories(emptyList())
        everySuspend { repository.searchMeals("Pizza") } throws Exception("Search failed")

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateSearchQuery("Pizza")
        advanceUntilIdle()

        assertEquals("Search failed", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }
}