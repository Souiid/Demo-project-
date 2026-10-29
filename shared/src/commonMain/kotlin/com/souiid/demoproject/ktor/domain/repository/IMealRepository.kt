package com.souiid.demoproject.ktor.domain.repository

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.model.MealCategory

interface IMealRepository {
    suspend fun searchMeals(query: String): List<Meal>
    suspend fun getCategories(): List<MealCategory>
    suspend fun getMealsByCategory(category: String): List<Meal>
}