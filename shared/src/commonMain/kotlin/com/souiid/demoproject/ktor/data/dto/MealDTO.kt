package com.souiid.demoproject.ktor.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MealDTO(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String? = null,
    val strArea: String? = null,
    val strInstructions: String? = null,
    val strMealThumb: String? = null
)