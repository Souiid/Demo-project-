package com.souiid.demoproject.room.usecase

import com.souiid.demoproject.room.domain.repository.IFavoriteMealRepository
import com.souiid.demoproject.room.domain.usecase.ObserveIsFavoriteUseCase
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObserveIsFavoriteUseCaseTest {

    private val repository = mock<IFavoriteMealRepository>()
    private val useCase = ObserveIsFavoriteUseCase(repository)

    @Test
    fun observeIsFavoriteReturnsTrue() = runTest {
        every { repository.observeIsFavorite("1") } returns flowOf(true)

        val result = useCase("1").first()

        assertTrue(result)
        verify { repository.observeIsFavorite("1") }
    }

    @Test
    fun observeIsFavoriteReturnsFalse() = runTest {
        every { repository.observeIsFavorite("1") } returns flowOf(false)

        val result = useCase("1").first()

        assertFalse(result)
    }
}