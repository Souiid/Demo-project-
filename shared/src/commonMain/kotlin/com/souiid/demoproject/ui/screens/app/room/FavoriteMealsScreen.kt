package com.souiid.demoproject.ui.screens.app.room

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import demoproject.shared.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import demoproject.shared.generated.resources.noFavoriteMeals

@Composable
fun FavoriteMealsScreen(
    onMealClick: (String) -> Unit
) {
    val viewModel: FavoriteMealsViewModel = koinInject()
    val favoriteMeals by viewModel.favoriteMeals.collectAsState()

    TScreen {
        if (favoriteMeals.isEmpty()) {
            TText(text = stringResource(Res.string.noFavoriteMeals))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteMeals, key = { it.id }) { meal ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { onMealClick(meal.id) }
                    ) {
                        TKamelImage(
                            imageUrl = meal.imageUrl,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        TText(
                            text = meal.name,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
