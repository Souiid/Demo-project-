package com.souiid.demoproject.ui.screens.app.ktor.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ui.components.others.TExpandableSections
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import demoproject.shared.generated.resources.removeFromFavorites

@Composable
fun MealDetailScreen(mealId: String) {
    val viewModel: MealDetailViewModel = koinInject()
    val meal by viewModel.meal.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(mealId) {
        viewModel.loadMeal(mealId)
    }

    TScreen(isScrollable = true) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                TText(text = error.orEmpty())
            }

            meal != null -> {
                MealDetailContent(
                    meal = meal!!,
                    isFavorite = isFavorite,
                    onFavoriteClick = viewModel::toggleFavorite
                )
            }
        }
    }
}

@Composable
private fun MealDetailContent(
    meal: Meal,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        TText(text = meal.name)

        Box(
            modifier = Modifier.fillMaxWidth().aspectRatio(1.5f)
        ) {
            TKamelImage(
                imageUrl = meal.imageUrl,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp)
            )

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(
                        if (isFavorite) Res.string.removeFromFavorites else Res.string.addToFavorites
                    ),
                    tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            meal.category?.let { TText(text = it) }
            meal.area?.let { TText(text = it) }
        }

        TExpandableSections(
            sectionTitles = listOf(
                stringResource(Res.string.ingredients),
                stringResource(Res.string.instructions)
            )
        ) { index ->
            when (index) {
                0 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        meal.ingredients.forEach { ingredient ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TText(text = ingredient.name)
                                TText(text = ingredient.measure)
                            }
                        }
                    }
                }

                1 -> {
                    meal.instructions?.takeIf { it.isNotBlank() }?.let {
                        TText(
                            text = it,
                            maxLines = Int.MAX_VALUE
                        )
                    }
                }
            }
        }
    }
}
