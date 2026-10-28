package com.souiid.demoproject.ktor.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MealCategoryDTO(
    val idCategory: String,
    val strCategory: String,
    val strCategoryThumb: String,
    val strCategoryDescription: String
)