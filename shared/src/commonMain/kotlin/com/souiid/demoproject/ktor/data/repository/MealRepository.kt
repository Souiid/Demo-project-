package com.souiid.demoproject.ktor.data.repository

import com.souiid.demoproject.ktor.data.mapper.toDomain
import com.souiid.demoproject.ktor.data.remote.MealApi
import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.model.MealCategory
import com.souiid.demoproject.ktor.domain.repository.IMealRepository

class MealRepository(
    private val mealApi: MealApi
) : IMealRepository {

    override suspend fun searchMeals(query: String): List<Meal> {
        return mealApi.searchMeals(query).meals?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getCategories(): List<MealCategory> {
        return mealApi.getCategories().categories.map { it.toDomain() }
    }
}