package com.souiid.demoproject.room.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMealDao {

    @Transaction
    @Query("SELECT * FROM favorite_meals")
    fun observeFavorites(): Flow<List<FavoriteMealWithIngredients>>

    @Query("SELECT * FROM favorite_meals WHERE id = :mealId LIMIT 1")
    suspend fun getFavoriteById(mealId: String): FavoriteMealEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_meals WHERE id = :mealId)")
    fun observeIsFavorite(mealId: String): Flow<Boolean>

    @Query("SELECT * FROM favorite_meal_ingredients WHERE mealId = :mealId ORDER BY position")
    suspend fun getIngredients(mealId: String): List<FavoriteMealIngredientEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(meal: FavoriteMealEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<FavoriteMealIngredientEntity>)

    @Query("DELETE FROM favorite_meal_ingredients WHERE mealId = :mealId")
    suspend fun deleteIngredients(mealId: String)

    @Query("DELETE FROM favorite_meals WHERE id = :mealId")
    suspend fun deleteFavorite(mealId: String)

    @Transaction
    suspend fun saveFavorite(
        meal: FavoriteMealEntity,
        ingredients: List<FavoriteMealIngredientEntity>
    ) {
        insertFavorite(meal)
        deleteIngredients(meal.id)
        insertIngredients(ingredients)
    }
}