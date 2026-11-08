package com.souiid.demoproject.ktor.repository

import com.souiid.demoproject.ktor.data.remote.MealApi
import com.souiid.demoproject.ktor.data.repository.MealRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MealRepositoryTest {

    private fun createRepository(response: String): Pair<MealRepository, HttpClient> {
        val client = HttpClient(MockEngine {
            respond(
                content = response,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        return MealRepository(MealApi(client)) to client
    }

    @Test
    fun searchMealsSuccessfully() = runTest {
        val (repository, client) = createRepository(
            """{"meals":[{"idMeal":"1","strMeal":"Pizza","strCategory":"Vegetarian","strMealThumb":"https://example.com/pizza.jpg"}]}"""
        )
        try {
            val meals = repository.searchMeals("Pizza")

            assertEquals(1, meals.size)
            assertEquals("1", meals.first().id)
            assertEquals("Pizza", meals.first().name)
            assertEquals("Vegetarian", meals.first().category)
            assertEquals("https://example.com/pizza.jpg", meals.first().imageUrl)
        } finally {
            client.close()
        }
    }

    @Test
    fun searchMealsWithNoResults() = runTest {
        val (repository, client) = createRepository("""{"meals":null}""")
        try {
            val meals = repository.searchMeals("Unknown")

            assertTrue(meals.isEmpty())
        } finally {
            client.close()
        }
    }

    @Test
    fun getCategoriesSuccessfully() = runTest {
        val (repository, client) = createRepository(
            """{"categories":[{"idCategory":"1","strCategory":"Vegetarian","strCategoryThumb":"https://example.com/vegetarian.jpg","strCategoryDescription":"Vegetarian meals"}]}"""
        )
        try {
            val categories = repository.getCategories()

            assertEquals(1, categories.size)
            assertEquals("1", categories.first().id)
            assertEquals("Vegetarian", categories.first().name)
            assertEquals("Vegetarian meals", categories.first().description)
        } finally {
            client.close()
        }
    }

    @Test
    fun getCategoriesWithEmptyResponse() = runTest {
        val (repository, client) = createRepository("""{"categories":[]}""")
        try {
            val categories = repository.getCategories()

            assertTrue(categories.isEmpty())
        } finally {
            client.close()
        }
    }

    @Test
    fun getMealsByCategorySuccessfully() = runTest {
        val (repository, client) = createRepository(
            """{"meals":[{"idMeal":"2","strMeal":"Vegetable Soup","strMealThumb":"https://example.com/soup.jpg"}]}"""
        )
        try {
            val meals = repository.getMealsByCategory("Vegetarian")

            assertEquals(1, meals.size)
            assertEquals("2", meals.first().id)
            assertEquals("Vegetable Soup", meals.first().name)
        } finally {
            client.close()
        }
    }

    @Test
    fun getMealsByCategoryWithNoResults() = runTest {
        val (repository, client) = createRepository("""{"meals":null}""")
        try {
            val meals = repository.getMealsByCategory("Unknown")

            assertTrue(meals.isEmpty())
        } finally {
            client.close()
        }
    }

    @Test
    fun getMealByIdSuccessfully() = runTest {
        val (repository, client) = createRepository(
            """
            {
                "meals": [{
                    "idMeal": "3",
                    "strMeal": "Pasta",
                    "strCategory": "Vegetarian",
                    "strArea": "Italian",
                    "strInstructions": "Cook the pasta",
                    "strMealThumb": "https://example.com/pasta.jpg",
                    "strIngredient1": "Pasta",
                    "strMeasure1": "200g",
                    "strIngredient2": "Tomato",
                    "strMeasure2": "100g",
                    "strIngredient3": "",
                    "strMeasure3": ""
                }]
            }
            """.trimIndent()
        )
        try {
            val meal = repository.getMealById("3")

            assertNotNull(meal)
            assertEquals("3", meal.id)
            assertEquals("Pasta", meal.name)
            assertEquals("Italian", meal.area)
            assertEquals("Cook the pasta", meal.instructions)
            assertEquals(2, meal.ingredients.size)
            assertEquals("Pasta", meal.ingredients[0].name)
            assertEquals("200g", meal.ingredients[0].measure)
            assertEquals("Tomato", meal.ingredients[1].name)
        } finally {
            client.close()
        }
    }

    @Test
    fun getMealByIdNotFound() = runTest {
        val (repository, client) = createRepository("""{"meals":null}""")
        try {
            val meal = repository.getMealById("999")

            assertNull(meal)
        } finally {
            client.close()
        }
    }
}