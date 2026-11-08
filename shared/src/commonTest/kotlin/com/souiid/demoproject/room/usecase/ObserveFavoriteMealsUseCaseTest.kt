package com.souiid.demoproject.room.usecase

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.room.domain.repository.IFavoriteMealRepository
import com.souiid.demoproject.room.domain.usecase.ObserveFavoriteMealsUseCase
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObserveFavoriteMealsUseCaseTest {

    private val repository = mock<IFavoriteMealRepository>()
    private val useCase = ObserveFavoriteMealsUseCase(repository)

    @Test
    fun observeFavoritesSuccessfully() = runTest {
        val meal = Meal("1", "Pasta", "Vegetarian", "Italian", null, null)
        every { repository.observeFavorites() } returns flowOf(listOf(meal))

        val result = useCase().first()

        assertEquals(listOf(meal), result)
        verify { repository.observeFavorites() }
    }

    @Test
    fun observeFavoritesWithEmptyList() = runTest {
        every { repository.observeFavorites() } returns flowOf(emptyList())

        val result = useCase().first()

        assertTrue(result.isEmpty())
    }
}