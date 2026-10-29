package com.souiid.demoproject.ui.screens.app.ktor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.ui.components.item.ktor.MealCategoryItem
import com.souiid.demoproject.ui.components.item.ktor.MealItem
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.results
import demoproject.shared.generated.resources.searchMeal
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun HomeMealScreen() {
    val viewModel: HomeMealViewModel = koinInject()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val meals by viewModel.meals.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    TScreen {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TTextField(
                value = searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                type = TTextFieldType.SEARCH,
                labelTitle = stringResource(Res.string.searchMeal)
            )

            if (searchQuery.isBlank()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories, key = { it.id }) { category ->
                        MealCategoryItem(
                            category = category,
                            isSelected = selectedCategory?.id == category.id,
                            onClick = { viewModel.selectCategory(category) }
                        )
                    }
                }
            }

            if (searchQuery.isNotBlank()) {
                TText(text = stringResource(Res.string.results))
            } else {
                selectedCategory?.let {
                    TText(text = it.name)
                }
            }

            if (isLoading) {
                CircularProgressIndicator()
            }

            error?.let {
                TText(text = it)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(meals, key = { it.id }) { meal ->
                    MealItem(meal)
                }
            }
        }
    }
}
