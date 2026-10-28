package com.souiid.demoproject.ktor.domain.model

data class Meal(
    val id: String,
    val name: String,
    val category: String?,
    val area: String?,
    val instructions: String?,
    val imageUrl: String?
)