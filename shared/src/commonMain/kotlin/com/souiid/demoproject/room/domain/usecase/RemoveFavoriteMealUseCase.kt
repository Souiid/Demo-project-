package com.souiid.demoproject.room.domain.usecase

import com.souiid.demoproject.room.domain.repository.IFavoriteMealRepository

class RemoveFavoriteMealUseCase(
    private val repository: IFavoriteMealRepository
) {
    suspend operator fun invoke(mealId: String) = repository.removeFavorite(mealId)
}
