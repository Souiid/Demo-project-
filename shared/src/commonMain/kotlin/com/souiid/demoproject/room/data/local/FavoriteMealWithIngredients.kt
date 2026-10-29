package com.souiid.demoproject.room.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class FavoriteMealWithIngredients(
    @Embedded val meal: FavoriteMealEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "mealId"
    )
    val ingredients: List<FavoriteMealIngredientEntity>
)
