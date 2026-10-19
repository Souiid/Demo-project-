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
    overflow: TextOverflow = TextOverflow.Visible,
) {

    val textStyle = when(style) {

        TTextStyle.TITLE ->
            MaterialTheme.typography.displayLarge

        TTextStyle.SUBTITLE ->
            MaterialTheme.typography.titleLarge

        TTextStyle.BODY ->
            MaterialTheme.typography.bodyLarge

        TTextStyle.DESCRIPTION ->
            MaterialTheme.typography.bodyMedium

        TTextStyle.BUTTON ->
            MaterialTheme.typography.labelLarge

        TTextStyle.LABEL ->
            MaterialTheme.typography.labelSmall

        TTextStyle.LITTLE ->
            MaterialTheme.typography.labelSmall

        TTextStyle.ERROR ->
            MaterialTheme.typography.bodyMedium

        TTextStyle.PRICE ->
            MaterialTheme.typography.titleLarge

        TTextStyle.BADGE ->
            MaterialTheme.typography.labelSmall
    }

    val resolvedColor = when {
        color != Color.Unspecified -> color
        style == TTextStyle.ERROR -> MaterialTheme.colorScheme.error
        style == TTextStyle.DESCRIPTION -> MaterialTheme.colorScheme.onSurfaceVariant
        style == TTextStyle.LITTLE -> MaterialTheme.colorScheme.onSurfaceVariant
        style == TTextStyle.BADGE -> MaterialTheme.colorScheme.primary
        style == TTextStyle.PRICE -> MaterialTheme.colorScheme.primary
        style == TTextStyle.BUTTON -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
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