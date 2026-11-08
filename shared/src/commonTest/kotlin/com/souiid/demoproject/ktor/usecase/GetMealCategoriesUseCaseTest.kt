package com.souiid.demoproject.ktor.usecase

import com.souiid.demoproject.ktor.domain.model.MealCategory
import com.souiid.demoproject.ktor.domain.repository.IMealRepository
import com.souiid.demoproject.ktor.domain.usecase.GetMealCategoriesUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetMealCategoriesUseCaseTest {

    private val repository = mock<IMealRepository>()
    private val useCase = GetMealCategoriesUseCase(repository)

    @Test
    fun getCategoriesSuccessfully() = runTest {
        val categories = listOf(
            MealCategory("1", "Vegetarian", "https://example.com/vegetarian.jpg", "Vegetarian meals"),
            MealCategory("2", "Seafood", "https://example.com/seafood.jpg", "Seafood meals")
        )
        everySuspend { repository.getCategories() } returns categories

        val result = useCase()

        assertEquals(categories, result)
        verifySuspend { repository.getCategories() }
    }

    @Test
    fun getCategoriesWithEmptyList() = runTest {
        everySuspend { repository.getCategories() } returns emptyList()

        val result = useCase()

        assertTrue(result.isEmpty())
        verifySuspend { repository.getCategories() }
    }
}