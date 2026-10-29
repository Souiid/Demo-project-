package com.souiid.demoproject.room.domain.usecase

import com.souiid.demoproject.room.domain.repository.IFavoriteMealRepository

class GetFavoriteMealByIdUseCase(
    private val repository: IFavoriteMealRepository
) {
    suspend operator fun invoke(mealId: String) = repository.getFavoriteById(mealId)
}
