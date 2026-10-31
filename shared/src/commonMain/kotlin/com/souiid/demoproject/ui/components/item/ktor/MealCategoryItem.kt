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
import com.souiid.demoproject.ktor.domain.model.MealCategory
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.theme.Dimens

@Composable
fun MealCategoryItem(
    category: MealCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.width(Dimens.mealCategoryWidth).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.smallSpace)
    ) {
        TKamelImage(
            imageUrl = category.imageUrl,
            modifier = Modifier.size(Dimens.mealCategoryImageSize),
            shape = RoundedCornerShape(Dimens.mediumRadius)
        )

        TText(
            text = category.name,
            style = TTextStyle.LABEL,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (isSelected) {
            HorizontalDivider(
                modifier = Modifier.width(Dimens.mealCategoryIndicatorWidth),
                thickness = Dimens.mealCategoryIndicatorThickness,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}