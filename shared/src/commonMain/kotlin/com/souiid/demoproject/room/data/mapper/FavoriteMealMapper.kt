package com.souiid.demoproject.room.data.mapper

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.model.MealIngredient
import com.souiid.demoproject.room.data.local.FavoriteMealEntity
import com.souiid.demoproject.room.data.local.FavoriteMealIngredientEntity

fun Meal.toFavoriteEntity(updatedAt: Long): FavoriteMealEntity {
    return FavoriteMealEntity(
        id = id,
        name = name,
        category = category,
        area = area,
        instructions = instructions,
        imageUrl = imageUrl,
        updatedAt = updatedAt
    )
}

fun Meal.toFavoriteIngredientEntities(): List<FavoriteMealIngredientEntity> {
    return ingredients.mapIndexed { index, ingredient ->
        FavoriteMealIngredientEntity(
            mealId = id,
            position = index,
            name = ingredient.name,
            measure = ingredient.measure
        )
    }
}

fun FavoriteMealEntity.toDomain(
    ingredients: List<FavoriteMealIngredientEntity>
): Meal {
    return Meal(
        id = id,
        name = name,
        category = category,
        area = area,
        instructions = instructions,
        imageUrl = imageUrl,
        ingredients = ingredients.map {
            MealIngredient(name = it.name, measure = it.measure)
        }
    )
}
