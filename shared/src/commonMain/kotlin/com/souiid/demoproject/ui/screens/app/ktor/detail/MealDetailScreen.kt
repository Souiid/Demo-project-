package com.souiid.demoproject.ui.screens.app.ktor.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ui.components.others.TExpandableSections
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.theme.Dimens
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.addToFavorites
import demoproject.shared.generated.resources.ingredients
import demoproject.shared.generated.resources.instructions
import demoproject.shared.generated.resources.removeFromFavorites
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

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
                    modifier = Modifier.fillMaxWidth().height(Dimens.mealLoadingHeight),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                TText(text = error.orEmpty(), style = TTextStyle.ERROR)
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
        verticalArrangement = Arrangement.spacedBy(Dimens.standardPadding)
    ) {
        TText(
            text = meal.name,
            style = TTextStyle.SUBTITLE
        )

        Box(
            modifier = Modifier.fillMaxWidth().aspectRatio(1.5f)
        ) {
            TKamelImage(
                imageUrl = meal.imageUrl,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(Dimens.largeRadius)
            )

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.align(Alignment.TopEnd)
                    .padding(Dimens.mediumSpace)
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
            horizontalArrangement = Arrangement.spacedBy(Dimens.smallSpace)
        ) {
            meal.category?.let { MealInfoBadge(it) }
            meal.area?.let { MealInfoBadge(it) }
        }

        Spacer(modifier = Modifier.height(Dimens.smallSpace))

        TExpandableSections(
            sectionTitles = listOf(
                stringResource(Res.string.ingredients),
                stringResource(Res.string.instructions)
            )
        ) { index ->
            when (index) {
                0 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.mediumSpace)) {
                        meal.ingredients.forEach { ingredient ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(Dimens.standardPadding),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    TText(
                                        text = ingredient.name,
                                        style = TTextStyle.BODY
                                    )
                                }

                                TText(
                                    text = ingredient.measure,
                                    style = TTextStyle.DESCRIPTION
                                )
                            }
                        }
                    }
                }

                1 -> {
                    meal.instructions?.takeIf { it.isNotBlank() }?.let {
                        TText(
                            text = it,
                            style = TTextStyle.BODY,
                            maxLines = Int.MAX_VALUE
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MealInfoBadge(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(Dimens.mediumRadius)
            )
            .padding(
                horizontal = Dimens.mediumSpace,
                vertical = Dimens.smallSpace
            )
    ) {
        TText(
            text = text,
            style = TTextStyle.DESCRIPTION
        )
    }
}
