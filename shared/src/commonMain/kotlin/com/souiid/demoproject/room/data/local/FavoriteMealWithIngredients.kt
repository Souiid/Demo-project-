package com.souiid.demoproject.room.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class FavoriteMealWithIngredients(
    @Embedded val meal: FavoriteMealEntity,
    @Relation(
        parentColumn = RoomConstants.Columns.ID,
        entityColumn = RoomConstants.Columns.MEAL_ID
    )
    val ingredients: List<FavoriteMealIngredientEntity>
)