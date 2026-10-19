package com.souiid.demoproject.ui.components.textfield

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.*
import com.souiid.demoproject.ui.components.others.AppIcon
import com.souiid.demoproject.ui.components.text.TText

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

    shape: Shape = OutlinedTextFieldDefaults.shape
) {

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    val keyboardType = when (type) {
        TTextFieldType.TEXT -> KeyboardType.Text
        TTextFieldType.EMAIL -> KeyboardType.Email
        TTextFieldType.PASSWORD -> KeyboardType.Password
        TTextFieldType.DESCRIPTION -> KeyboardType.Text
    }

    val finalImeAction =
        if (type == TTextFieldType.DESCRIPTION)
            ImeAction.Default
        else
            imeAction

    val visualTransformation =
        if (type == TTextFieldType.PASSWORD && !passwordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None

    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,

        label = labelTitle?.let {
            { TText(it) }
        },

        placeholder = placeholder?.let {
            { TText(it) }
        },

        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,

        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = finalImeAction
        ),

        visualTransformation = visualTransformation,

        shape = shape,

        leadingIcon = leadingIcon,

        trailingIcon = {

            if (type == TTextFieldType.PASSWORD) {

                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {
                    Icon(
                        imageVector = if (passwordVisible) AppIcon.EyeOpen else AppIcon.EyeClose,
                        contentDescription = "Password visibility"
                    )
                }

            } else {

                trailingContent?.invoke()
            }
        }
    )
}