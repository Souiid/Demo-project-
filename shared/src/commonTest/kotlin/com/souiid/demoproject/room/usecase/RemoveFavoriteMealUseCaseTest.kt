package com.souiid.demoproject.room.usecase

import com.souiid.demoproject.room.domain.repository.IFavoriteMealRepository
import com.souiid.demoproject.room.domain.usecase.RemoveFavoriteMealUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RemoveFavoriteMealUseCaseTest {

    private val repository = mock<IFavoriteMealRepository>()
    private val useCase = RemoveFavoriteMealUseCase(repository)

    @Test
    fun removeFavoriteSuccessfully() = runTest {
        everySuspend { repository.removeFavorite("1") } returns Unit

        useCase("1")

        verifySuspend { repository.removeFavorite("1") }
    }
}