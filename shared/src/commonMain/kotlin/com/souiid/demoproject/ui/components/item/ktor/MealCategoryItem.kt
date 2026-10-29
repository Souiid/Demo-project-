package com.souiid.demoproject.ui.components.item.ktor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.ktor.domain.model.MealCategory
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.text.TText

@Composable
fun MealCategoryItem(
    category: MealCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.width(76.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TKamelImage(
            imageUrl = category.imageUrl,
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp))
        )

        TText(
            text = category.name,
            modifier = Modifier.alpha(if (isSelected) 1f else 0.6f)
        )

        if (isSelected) {
            HorizontalDivider(
                modifier = Modifier.width(32.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}