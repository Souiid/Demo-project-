package com.souiid.mutiplatformtemplate.ui.components.button

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
import com.souiid.mutiplatformtemplate.ui.components.text.TText
import com.souiid.mutiplatformtemplate.ui.components.text.TTextStyle
import com.souiid.mutiplatformtemplate.ui.theme.Black
import com.souiid.mutiplatformtemplate.ui.theme.Dimens

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


    when (type) {

        TButtonType.PRIMARY -> {
            Button(
                onClick = onClick,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                enabled = enabled && !isLoading,
                modifier = modifier
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(Dimens.progressIndicatorSize),
                        strokeWidth = Dimens.progressStrokeSize,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                } else {

                    TText(
                        text = text,
                        style = TTextStyle.BUTTON,
                    )
                }
            }
        }

        TButtonType.REFUSE -> {
            Button(
                onClick = onClick,
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                enabled = enabled && !isLoading,
                modifier = modifier
            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(Dimens.progressIndicatorSize),
                        strokeWidth = Dimens.progressStrokeSize,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                } else {

                    TText(
                        text = text,
                        style = TTextStyle.BUTTON,
                    )
                }
            }
        }

        TButtonType.TEXT -> {
            TextButton(
                onClick = onClick,
                modifier = if (isTextAlignStart) {
                    modifier.fillMaxWidth()
                } else {
                    modifier
                },
                contentPadding = PaddingValues(vertical = Dimens.tinySpace)
            ) {
                Box(
                    contentAlignment = if (isTextAlignStart) Alignment.CenterStart else Alignment.Center
                ) {
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