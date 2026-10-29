package com.souiid.demoproject.room.data.repository

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.room.data.local.FavoriteMealDao
import com.souiid.demoproject.room.data.mapper.toDomain
import com.souiid.demoproject.room.data.mapper.toFavoriteEntity
import com.souiid.demoproject.room.data.mapper.toFavoriteIngredientEntities
import com.souiid.demoproject.room.domain.repository.IFavoriteMealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class FavoriteMealRepository(
    private val dao: FavoriteMealDao
) : IFavoriteMealRepository {

    override fun observeFavorites(): Flow<List<Meal>> {
        return dao.observeFavorites().map { favorites ->
            favorites.map { it.meal.toDomain(it.ingredients) }
        }
    }

    override fun observeIsFavorite(mealId: String): Flow<Boolean> {
        return dao.observeIsFavorite(mealId)
    }

    override suspend fun addFavorite(meal: Meal) {
        dao.saveFavorite(
            meal = meal.toFavoriteEntity(Clock.System.now().toEpochMilliseconds()),
            ingredients = meal.toFavoriteIngredientEntities()
        )
    }

    override suspend fun removeFavorite(mealId: String) {
        dao.deleteFavorite(mealId)
    }
}
