package com.souiid.demoproject.ui.components.item.ktor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.text.TText

@Composable
fun MealItem(
    meal: Meal,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        meal.imageUrl?.let { imageUrl ->
            TKamelImage(
                imageUrl = imageUrl,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }

        TText(text = meal.name)
    }
}