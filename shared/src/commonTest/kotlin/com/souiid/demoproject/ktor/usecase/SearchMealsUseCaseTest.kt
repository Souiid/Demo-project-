package com.souiid.demoproject.ktor.usecase

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.repository.IMealRepository
import com.souiid.demoproject.ktor.domain.usecase.SearchMealsUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchMealsUseCaseTest {

    private val repository = mock<IMealRepository>()
    private val useCase = SearchMealsUseCase(repository)

    @Test
    fun searchMealsSuccessfully() = runTest {
        val meals = listOf(
            Meal("1", "Chicken Pasta", "Pasta", null, null, null),
            Meal("2", "Vegetable Pasta", "Vegetarian", null, null, null)
        )
        everySuspend { repository.searchMeals("Pasta") } returns meals

        val result = useCase("Pasta")

        assertEquals(meals, result)
        verifySuspend { repository.searchMeals("Pasta") }
    }

    @Test
    fun searchMealsWithNoResults() = runTest {
        everySuspend { repository.searchMeals("Unknown") } returns emptyList()

        val result = useCase("Unknown")

        assertTrue(result.isEmpty())
        verifySuspend { repository.searchMeals("Unknown") }
    }
}