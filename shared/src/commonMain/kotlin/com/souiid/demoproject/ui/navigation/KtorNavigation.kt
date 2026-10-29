package com.souiid.demoproject.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.souiid.demoproject.ui.screens.app.ktor.home.HomeMealScreen
import com.souiid.demoproject.ui.screens.app.ktor.detail.MealDetailScreen

fun NavGraphBuilder.ktorNavigation(navController: NavController) {
    navigation<Route.Ktor>(startDestination = Route.HomeMeal) {
        composable<Route.HomeMeal> {
            HomeMealScreen(
                onMealClick = { mealId ->
                    navController.navigate(Route.MealDetail(mealId))
                }
            )
        }

        composable<Route.MealDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.MealDetail>()
            MealDetailScreen(
                mealId = route.mealId
            )
        }
    }
}