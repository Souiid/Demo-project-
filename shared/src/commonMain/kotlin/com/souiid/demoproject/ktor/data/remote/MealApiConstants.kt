package com.souiid.demoproject.ktor.data.remote

object MealApiConstants {

    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1"

    object Endpoints {
        const val SEARCH = "$BASE_URL/search.php"
        const val CATEGORIES = "$BASE_URL/categories.php"
        const val FILTER = "$BASE_URL/filter.php"
        const val LOOKUP = "$BASE_URL/lookup.php"
    }

    object Parameters {
        const val SEARCH = "s"
        const val CATEGORY = "c"
        const val ID = "i"
    }

    object Fields {
        const val INGREDIENT = "strIngredient"
        const val MEASURE = "strMeasure"
    }

    const val MAX_INGREDIENTS = 20
}