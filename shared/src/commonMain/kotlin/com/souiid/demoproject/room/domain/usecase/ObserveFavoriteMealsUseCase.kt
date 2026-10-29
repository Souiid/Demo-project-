package com.souiid.demoproject.room.domain.usecase

import com.souiid.demoproject.room.domain.repository.IFavoriteMealRepository

class ObserveFavoriteMealsUseCase(
    private val repository: IFavoriteMealRepository
) {
    operator fun invoke() = repository.observeFavorites()
}
