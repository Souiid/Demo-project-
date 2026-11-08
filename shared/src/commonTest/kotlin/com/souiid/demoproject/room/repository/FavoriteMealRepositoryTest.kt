package com.souiid.demoproject.room.repository

import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ktor.domain.model.MealIngredient
import com.souiid.demoproject.room.data.local.FavoriteMealDao
import com.souiid.demoproject.room.data.local.FavoriteMealEntity
import com.souiid.demoproject.room.data.local.FavoriteMealIngredientEntity
import com.souiid.demoproject.room.data.local.FavoriteMealWithIngredients
import com.souiid.demoproject.room.data.repository.FavoriteMealRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FavoriteMealRepositoryTest {

    private val dao = mock<FavoriteMealDao>()
    private val repository = FavoriteMealRepository(dao)

    private class FakeFavoriteMealDao : FavoriteMealDao {

        var savedMeal: FavoriteMealEntity? = null
        var savedIngredients = emptyList<FavoriteMealIngredientEntity>()

        override fun observeFavorites(): Flow<List<FavoriteMealWithIngredients>> = flowOf(emptyList())

        override fun observeIsFavorite(mealId: String): Flow<Boolean> = flowOf(false)

        override suspend fun getFavoriteById(mealId: String): FavoriteMealEntity? = null

        override suspend fun getIngredients(mealId: String): List<FavoriteMealIngredientEntity> = emptyList()

        override suspend fun insertFavorite(meal: FavoriteMealEntity) {
            savedMeal = meal
        }

        override suspend fun insertIngredients(ingredients: List<FavoriteMealIngredientEntity>) {
            savedIngredients = ingredients
        }

        override suspend fun deleteIngredients(mealId: String) {
            savedIngredients = emptyList()
        }

        override suspend fun deleteFavorite(mealId: String) = Unit
    }

    private val meal = Meal(
        id = "1",
        name = "Pasta",
        category = "Vegetarian",
        area = "Italian",
        instructions = "Cook the pasta",
        imageUrl = "https://example.com/pasta.jpg",
        ingredients = listOf(
            MealIngredient("Pasta", "200g"),
            MealIngredient("Tomato", "100g")
        )
    )

    private val entity = FavoriteMealEntity(
        id = meal.id,
        name = meal.name,
        category = meal.category,
        area = meal.area,
        instructions = meal.instructions,
        imageUrl = meal.imageUrl,
        updatedAt = 1000L
    )

    private val ingredientEntities = listOf(
        FavoriteMealIngredientEntity("1", 0, "Pasta", "200g"),
        FavoriteMealIngredientEntity("1", 1, "Tomato", "100g")
    )

    @Test
    fun observeFavoritesSuccessfully() = runTest {
        val favorites = listOf(FavoriteMealWithIngredients(entity, ingredientEntities))
        every { dao.observeFavorites() } returns MutableStateFlow(favorites)

        val result = repository.observeFavorites().first()

        assertEquals(listOf(meal), result)
    }

    @Test
    fun observeFavoritesWithEmptyList() = runTest {
        every { dao.observeFavorites() } returns MutableStateFlow(emptyList())

        val result = repository.observeFavorites().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun observeIsFavoriteReturnsTrue() = runTest {
        every { dao.observeIsFavorite("1") } returns MutableStateFlow(true)

        val result = repository.observeIsFavorite("1").first()

        assertTrue(result)
    }

    @Test
    fun observeIsFavoriteReturnsFalse() = runTest {
        every { dao.observeIsFavorite("1") } returns MutableStateFlow(false)

        val result = repository.observeIsFavorite("1").first()

        assertFalse(result)
    }


    @Test
    fun addFavoriteSuccessfully() = runTest {
        val fakeDao = FakeFavoriteMealDao()
        val repository = FavoriteMealRepository(fakeDao)

        repository.addFavorite(meal)

        val savedMeal = assertNotNull(fakeDao.savedMeal)
        assertEquals(meal.id, savedMeal.id)
        assertEquals(meal.name, savedMeal.name)
        assertEquals(meal.category, savedMeal.category)
        assertEquals(meal.area, savedMeal.area)
        assertEquals(meal.instructions, savedMeal.instructions)
        assertEquals(meal.imageUrl, savedMeal.imageUrl)
        assertTrue(savedMeal.updatedAt > 0L)

        assertEquals(ingredientEntities, fakeDao.savedIngredients)
    }



    @Test
    fun removeFavoriteSuccessfully() = runTest {
        everySuspend { dao.deleteFavorite("1") } returns Unit

        repository.removeFavorite("1")

        verifySuspend { dao.deleteFavorite("1") }
    }

    @Test
    fun getFavoriteByIdSuccessfully() = runTest {
        everySuspend { dao.getFavoriteById("1") } returns entity
        everySuspend { dao.getIngredients("1") } returns ingredientEntities

        val result = repository.getFavoriteById("1")

        assertEquals(meal, result)
        verifySuspend { dao.getFavoriteById("1") }
        verifySuspend { dao.getIngredients("1") }
    }

    @Test
    fun getFavoriteByIdNotFound() = runTest {
        everySuspend { dao.getFavoriteById("999") } returns null

        val result = repository.getFavoriteById("999")

        assertNull(result)
        verifySuspend { dao.getFavoriteById("999") }
    }
}