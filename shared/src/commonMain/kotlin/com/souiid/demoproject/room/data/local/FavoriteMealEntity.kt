package com.souiid.demoproject.room.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_meals")
data class FavoriteMealEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String?,
    val area: String?,
    val instructions: String?,
    val imageUrl: String?,
    val updatedAt: Long
)