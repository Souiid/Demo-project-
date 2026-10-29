package com.souiid.demoproject.ui.screens.app.ktor.detail

import androidx.compose.runtime.Composable
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText

@Composable
fun MealDetailScreen(
    mealId: String,
    onBack: () -> Unit
) {
    TScreen {
        TText(text = mealId)
    }
}