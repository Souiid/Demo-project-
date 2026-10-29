package com.souiid.demoproject.room.domain.repository

import com.souiid.demoproject.ktor.domain.model.Meal
import kotlinx.coroutines.flow.Flow

interface IFavoriteMealRepository {
    fun observeFavorites(): Flow<List<Meal>>
    fun observeIsFavorite(mealId: String): Flow<Boolean>
    suspend fun addFavorite(meal: Meal)
    suspend fun removeFavorite(mealId: String)
    suspend fun getFavoriteById(mealId: String): Meal?
}