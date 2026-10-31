package com.souiid.demoproject.ui.screens.app.room

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.souiid.demoproject.ui.components.item.ktor.MealItem
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.theme.Dimens
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.noFavoriteMeals
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun FavoriteMealsScreen(
    onMealClick: (String) -> Unit
) {
    val viewModel: FavoriteMealsViewModel = koinInject()
    val favoriteMeals by viewModel.favoriteMeals.collectAsState()

    TScreen {
        if (favoriteMeals.isEmpty()) {
            TText(
                text = stringResource(Res.string.noFavoriteMeals),
                style = TTextStyle.BODY
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.mediumSpace),
                verticalArrangement = Arrangement.spacedBy(Dimens.standardPadding)
            ) {
                items(favoriteMeals, key = { it.id }) { meal ->
                    MealItem(
                        meal = meal,
                        onClick = { onMealClick(meal.id) }
                    )
                }
            }
        }
    }
}