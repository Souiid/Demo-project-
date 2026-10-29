package com.souiid.demoproject.room.data.local

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "favorite_meal_ingredients",
    primaryKeys = ["mealId", "position"],
    foreignKeys = [
        ForeignKey(
            entity = FavoriteMealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class FavoriteMealIngredientEntity(
    val mealId: String,
    val position: Int,
    val name: String,
    val measure: String
)