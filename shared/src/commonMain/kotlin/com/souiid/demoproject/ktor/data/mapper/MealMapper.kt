package com.souiid.demoproject.ktor.data.mapper

import com.souiid.demoproject.ktor.data.dto.response.MealCategoryDTO
import com.souiid.demoproject.ktor.data.dto.MealDTO
import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.model.MealCategory
import com.souiid.demoproject.ktor.domain.model.MealIngredient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive

private val json = Json {
    ignoreUnknownKeys = true
}

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

fun JsonObject.toMealDetail(): Meal {
    val dto = json.decodeFromJsonElement<MealDTO>(this)

    val ingredients = (1..20).mapNotNull { index ->
        val name = this["strIngredient$index"]?.jsonPrimitive?.contentOrNull?.trim()
        val measure = this["strMeasure$index"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()

        name?.takeIf { it.isNotEmpty() }?.let {
            MealIngredient(name = it, measure = measure)
        }
    }

    return dto.toDomain().copy(ingredients = ingredients)
}