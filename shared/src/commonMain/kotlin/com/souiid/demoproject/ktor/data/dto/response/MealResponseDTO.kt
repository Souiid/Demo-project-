package com.souiid.demoproject.ktor.data.dto.response

import com.souiid.demoproject.ktor.data.dto.MealDTO
import kotlinx.serialization.Serializable

@Serializable
data class MealResponseDTO(
    val meals: List<MealDTO>? = null
)