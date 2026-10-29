package com.souiid.demoproject.ktor.data.remote

import com.souiid.demoproject.ktor.data.dto.response.MealCategoryResponseDTO
import com.souiid.demoproject.ktor.data.dto.response.MealDetailResponseDTO
import com.souiid.demoproject.ktor.data.dto.response.MealResponseDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class MealApi(
    private val client: HttpClient
) {
    private val baseUrl = "https://www.themealdb.com/api/json/v1/1"

    suspend fun searchMeals(query: String): MealResponseDTO {
        return client.get("$baseUrl/search.php") {
            parameter("s", query)
        }.body()
    }

    suspend fun getCategories(): MealCategoryResponseDTO {
        return client.get("$baseUrl/categories.php").body()
    }

    suspend fun getMealsByCategory(category: String): MealResponseDTO {
        return client.get("$baseUrl/filter.php") {
            parameter("c", category)
        }.body()
    }

    suspend fun getMealById(mealId: String): MealDetailResponseDTO {
        return client.get("$baseUrl/lookup.php") {
            parameter("i", mealId)
        }.body()
    }
}