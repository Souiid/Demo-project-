package com.souiid.demoproject.ui.components.textfield

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.ui.components.others.AppIcon
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle

@Composable
fun TTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    type: TTextFieldType = TTextFieldType.TEXT,

    labelTitle: String? = null,
    placeholder: String? = null,

    imeAction: ImeAction = ImeAction.Next,

    singleLine: Boolean = type != TTextFieldType.DESCRIPTION,
    minLines: Int = if (type == TTextFieldType.DESCRIPTION) 4 else 1,
    maxLines: Int = Int.MAX_VALUE,

    leadingIcon: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,

    shape: Shape = OutlinedTextFieldDefaults.shape,

    errorMessage: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isError = !errorMessage.isNullOrBlank()

    val keyboardType = when (type) {
        TTextFieldType.TEXT -> KeyboardType.Text
        TTextFieldType.EMAIL -> KeyboardType.Email
        TTextFieldType.PASSWORD -> KeyboardType.Password
        TTextFieldType.DESCRIPTION -> KeyboardType.Text
        TTextFieldType.SEARCH -> KeyboardType.Text
    }

    val finalImeAction = when (type) {
        TTextFieldType.DESCRIPTION -> ImeAction.Default
        TTextFieldType.SEARCH -> ImeAction.Search
        else -> imeAction
    }

    val visualTransformation = if (type == TTextFieldType.PASSWORD && !passwordVisible) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,

            label = labelTitle?.let { { TText(it) } },
            placeholder = placeholder?.let { { TText(it) } },

            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,

            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = finalImeAction
            ),

            visualTransformation = visualTransformation,
            shape = shape,
            isError = isError,

            leadingIcon = if (type == TTextFieldType.SEARCH) {
                { Icon(imageVector = AppIcon.Search, contentDescription = null) }
            } else leadingIcon,

            trailingIcon = {
                when (type) {
                    TTextFieldType.PASSWORD -> {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) AppIcon.EyeOpen else AppIcon.EyeClose,
                                contentDescription = "Password visibility"
                            )
                        }
                    }
                    TTextFieldType.SEARCH if value.isNotEmpty() -> {
                        IconButton(onClick = { onValueChange("") }) {
                            Icon(imageVector = AppIcon.Close, contentDescription = null)
                        }
                    }
                    else -> trailingContent?.invoke()
                }
            }
        )

        if (isError) {
            TText(
                text = errorMessage,
                style = TTextStyle.LABEL,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}