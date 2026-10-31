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

    suspend fun searchMeals(query: String): MealResponseDTO {
        return client.get(MealApiConstants.Endpoints.SEARCH) {
            parameter(MealApiConstants.Parameters.SEARCH, query)
        }.body()
    }

    suspend fun getCategories(): MealCategoryResponseDTO {
        return client.get(MealApiConstants.Endpoints.CATEGORIES).body()
    }

    suspend fun getMealsByCategory(category: String): MealResponseDTO {
        return client.get(MealApiConstants.Endpoints.FILTER) {
            parameter(MealApiConstants.Parameters.CATEGORY, category)
        }.body()
    }

    suspend fun getMealById(mealId: String): MealDetailResponseDTO {
        return client.get(MealApiConstants.Endpoints.LOOKUP) {
            parameter(MealApiConstants.Parameters.ID, mealId)
        }.body()
    }
}