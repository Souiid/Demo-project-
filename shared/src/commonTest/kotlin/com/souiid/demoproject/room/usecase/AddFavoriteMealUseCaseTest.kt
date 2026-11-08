package com.souiid.demoproject.room.usecase

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.room.domain.repository.IFavoriteMealRepository
import com.souiid.demoproject.room.domain.usecase.AddFavoriteMealUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class AddFavoriteMealUseCaseTest {

    private val repository = mock<IFavoriteMealRepository>()
    private val useCase = AddFavoriteMealUseCase(repository)

    @Test
    fun addFavoriteSuccessfully() = runTest {
        val meal = Meal("1", "Pasta", "Vegetarian", "Italian", null, null)
        everySuspend { repository.addFavorite(meal) } returns Unit

        useCase(meal)

        verifySuspend { repository.addFavorite(meal) }
    }
}