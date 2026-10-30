package com.souiid.demoproject.ui.components.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextDecoration
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.theme.Black
import com.souiid.demoproject.ui.theme.Dimens

@Composable
fun TButton(
    modifier: Modifier = Modifier,
    text: String,
    type: TButtonType = TButtonType.PRIMARY,
    onClick: () -> Unit,
    shape: Shape = RoundedCornerShape(Dimens.mediumRadius),
    enabled: Boolean = true,
    isLoading: Boolean = false,
    isUnderlined: Boolean = false,
    isTextAlignStart: Boolean = false,
    color: Color = Black
) {
    val isEnabled = enabled && !isLoading

    when (type) {
        TButtonType.PRIMARY, TButtonType.REFUSE, TButtonType.NEUTRAL -> {
            val containerColor = when (type) {
                TButtonType.REFUSE -> MaterialTheme.colorScheme.error
                TButtonType.NEUTRAL -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.primary
            }

            val contentColor = when (type) {
                TButtonType.REFUSE -> MaterialTheme.colorScheme.onError
                TButtonType.NEUTRAL -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onPrimary
            }

            Button(
                onClick = onClick,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = contentColor,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                enabled = isEnabled,
                modifier = modifier
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Dimens.progressIndicatorSize),
                        strokeWidth = Dimens.progressStrokeSize,
                        color = contentColor
                    )
                } else {
                    TText(
                        text = text,
                        style = TTextStyle.BUTTON
                    )
                }
            }
        }

        TButtonType.TEXT -> {
            TextButton(
                onClick = onClick,
                enabled = isEnabled,
                modifier = if (isTextAlignStart) modifier.fillMaxWidth() else modifier,
                contentPadding = PaddingValues(vertical = Dimens.tinySpace)
            ) {
                Box(
                    contentAlignment = if (isTextAlignStart) Alignment.CenterStart else Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(Dimens.progressIndicatorSize),
                            strokeWidth = Dimens.progressStrokeSize,
                            color = color
                        )
                    } else {
                        TText(
                            text = text,
                            style = TTextStyle.LITTLE,
                            color = color,
                            textDecoration = if (isUnderlined) TextDecoration.Underline else null
                        )
                    }
                }
            }
        }
    }
}