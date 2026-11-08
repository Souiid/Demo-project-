package com.souiid.demoproject.ktor.usecase

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.repository.IMealRepository
import com.souiid.demoproject.ktor.domain.usecase.GetMealByIdUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetMealByIdUseCaseTest {

    private val repository = mock<IMealRepository>()
    private val useCase = GetMealByIdUseCase(repository)

    @Test
    fun getMealByIdSuccessfully() = runTest {
        val meal = Meal(
            id = "1",
            name = "Pasta",
            category = "Vegetarian",
            area = "Italian",
            instructions = "Cook the pasta",
            imageUrl = "https://example.com/pasta.jpg"
        )

        everySuspend { repository.getMealById("1") } returns meal

        val result = useCase("1")

        assertEquals(meal, result)
        verifySuspend { repository.getMealById("1") }
    }

    @Test
    fun getMealByIdNotFound() = runTest {
        everySuspend { repository.getMealById("999") } returns null

        val result = useCase("999")

        assertNull(result)
        verifySuspend { repository.getMealById("999") }
    }
}