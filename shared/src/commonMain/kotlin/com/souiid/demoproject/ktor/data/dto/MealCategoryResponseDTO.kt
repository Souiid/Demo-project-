package com.souiid.demoproject.ktor.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MealCategoryResponseDTO(
    val categories: List<MealCategoryDTO> = emptyList()
)