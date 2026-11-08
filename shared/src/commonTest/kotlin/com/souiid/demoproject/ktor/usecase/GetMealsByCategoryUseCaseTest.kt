package com.souiid.demoproject.ktor.usecase

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.repository.IMealRepository
import com.souiid.demoproject.ktor.domain.usecase.GetMealsByCategoryUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetMealsByCategoryUseCaseTest {

    private val repository = mock<IMealRepository>()
    private val useCase = GetMealsByCategoryUseCase(repository)

    @Test
    fun getMealsByCategorySuccessfully() = runTest {
        val meals = listOf(
            Meal("1", "Vegetable Soup", "Vegetarian", null, null, null),
            Meal("2", "Vegetable Pasta", "Vegetarian", null, null, null)
        )
        everySuspend { repository.getMealsByCategory("Vegetarian") } returns meals

        val result = useCase("Vegetarian")

        assertEquals(meals, result)
        verifySuspend { repository.getMealsByCategory("Vegetarian") }
    }

    @Test
    fun getMealsByCategoryWithEmptyList() = runTest {
        everySuspend { repository.getMealsByCategory("Unknown") } returns emptyList()

        val result = useCase("Unknown")

        assertTrue(result.isEmpty())
        verifySuspend { repository.getMealsByCategory("Unknown") }
    }
}