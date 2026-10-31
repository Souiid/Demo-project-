package com.souiid.demoproject.room.data.local

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = RoomConstants.Tables.FAVORITE_MEAL_INGREDIENTS,
    primaryKeys = [RoomConstants.Columns.MEAL_ID, RoomConstants.Columns.POSITION],
    foreignKeys = [
        ForeignKey(
            entity = FavoriteMealEntity::class,
            parentColumns = [RoomConstants.Columns.ID],
            childColumns = [RoomConstants.Columns.MEAL_ID],
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