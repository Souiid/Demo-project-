package com.souiid.demoproject.room.domain.usecase

import com.souiid.demoproject.room.domain.repository.IFavoriteMealRepository

class ObserveIsFavoriteUseCase(
    private val repository: IFavoriteMealRepository
) {
    operator fun invoke(mealId: String) = repository.observeIsFavorite(mealId)
}
