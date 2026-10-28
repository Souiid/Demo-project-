package com.souiid.demoproject.ktor.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MealResponseDTO(
    val meals: List<MealDTO>? = null
)