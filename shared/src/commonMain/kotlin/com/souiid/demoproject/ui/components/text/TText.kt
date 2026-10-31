package com.souiid.demoproject.ui.components.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun TText(
    text: String,
    style: TTextStyle = TTextStyle.BODY,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.Start,
    textDecoration: TextDecoration? = null,
    modifier: Modifier = Modifier,
    maxLines: Int = 3,
    overflow: TextOverflow = TextOverflow.Visible
) {
    val typography = MaterialTheme.typography
    val colors = MaterialTheme.colorScheme

    val textStyle = when (style) {
        TTextStyle.TITLE -> typography.headlineLarge
        TTextStyle.SUBTITLE -> typography.titleLarge
        TTextStyle.ITEM_TITLE -> typography.titleMedium
        TTextStyle.BODY -> typography.bodyLarge
        TTextStyle.DESCRIPTION -> typography.bodyMedium
        TTextStyle.BUTTON -> typography.labelLarge
        TTextStyle.LABEL -> typography.labelSmall
        TTextStyle.LITTLE -> typography.labelSmall
        TTextStyle.ERROR -> typography.bodyMedium
        TTextStyle.PRICE -> typography.titleLarge
        TTextStyle.BADGE -> typography.labelSmall
    }

    val resolvedColor = when {
        color != Color.Unspecified -> color
        style == TTextStyle.ERROR -> colors.error
        style == TTextStyle.DESCRIPTION -> colors.onSurfaceVariant
        style == TTextStyle.LITTLE -> colors.onSurfaceVariant
        style == TTextStyle.BADGE -> colors.primary
        style == TTextStyle.PRICE -> colors.primary
        else -> colors.onSurface
    }

    Text(
        text = text,
        style = textStyle,
        color = resolvedColor,
        textAlign = textAlign,
        textDecoration = textDecoration,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}