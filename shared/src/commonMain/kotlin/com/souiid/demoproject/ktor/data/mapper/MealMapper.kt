package com.souiid.demoproject.ktor.data.mapper

import com.souiid.demoproject.ktor.data.dto.MealCategoryDTO
import com.souiid.demoproject.ktor.data.dto.MealDTO
import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.model.MealCategory


fun MealDTO.toDomain() = Meal(
    id = idMeal,
    name = strMeal,
    category = strCategory,
    area = strArea,
    instructions = strInstructions,
    imageUrl = strMealThumb
)

fun MealCategoryDTO.toDomain() = MealCategory(
    id = idCategory,
    name = strCategory,
    imageUrl = strCategoryThumb,
    description = strCategoryDescription
)