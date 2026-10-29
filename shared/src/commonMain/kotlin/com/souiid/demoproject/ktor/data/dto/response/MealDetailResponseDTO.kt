package com.souiid.demoproject.ktor.data.dto.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class MealDetailResponseDTO(
    val meals: List<JsonObject>? = null
)