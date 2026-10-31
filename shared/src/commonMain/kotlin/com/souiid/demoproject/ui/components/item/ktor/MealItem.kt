package com.souiid.demoproject.ui.components.item.ktor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.souiid.demoproject.ktor.domain.model.Meal
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.theme.Dimens

@Composable
fun MealItem(
    meal: Meal,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(Dimens.smallSpace)
    ) {
        meal.imageUrl?.let { imageUrl ->
            TKamelImage(
                imageUrl = imageUrl,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                shape = RoundedCornerShape(Dimens.mediumRadius)
            )
        }

        TText(
            text = meal.name,
            style = TTextStyle.ITEM_TITLE,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}