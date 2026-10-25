package com.souiid.demoproject.ui.components.others

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun TKamelImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    photoUri: String? = null,
    shape: Shape = CircleShape,
    contentScale: ContentScale = ContentScale.Crop,
    boxContent: @Composable BoxScope.() -> Unit = {}
) {
    val source = when {
        photoUri == null -> imageUrl
        photoUri.startsWith("file://") || photoUri.startsWith("content://") -> photoUri
        else -> "file://$photoUri"
    }

    Box(
        modifier = modifier.clip(shape).background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (!source.isNullOrBlank()) {
            KamelImage(
                resource = { asyncPainterResource(source) },
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale
            )
        } else {
            boxContent()
        }
    }
}