package com.souiid.demoproject.room.usecase

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.room.domain.repository.IFavoriteMealRepository
import com.souiid.demoproject.room.domain.usecase.GetFavoriteMealByIdUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetFavoriteMealByIdUseCaseTest {

    private val repository = mock<IFavoriteMealRepository>()
    private val useCase = GetFavoriteMealByIdUseCase(repository)

    @Test
    fun getFavoriteSuccessfully() = runTest {
        val meal = Meal("1", "Pasta", "Vegetarian", "Italian", null, null)
        everySuspend { repository.getFavoriteById("1") } returns meal

        val result = useCase("1")

        assertEquals(meal, result)
        verifySuspend { repository.getFavoriteById("1") }
    }

    @Test
    fun getFavoriteNotFound() = runTest {
        everySuspend { repository.getFavoriteById("999") } returns null

        val result = useCase("999")

        assertNull(result)
        verifySuspend { repository.getFavoriteById("999") }
    }
}