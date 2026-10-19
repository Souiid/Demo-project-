package com.souiid.mutiplatformtemplate.ui.components.others

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.souiid.mutiplatformtemplate.ui.components.button.TButton
import com.souiid.mutiplatformtemplate.ui.components.button.TButtonType
import com.souiid.mutiplatformtemplate.ui.components.text.TText
import com.souiid.mutiplatformtemplate.ui.components.textfield.TTextField
import com.souiid.mutiplatformtemplate.ui.components.textfield.TTextFieldType
import com.souiid.mutiplatformtemplate.ui.theme.Black
import com.souiid.mutiplatformtemplate.ui.theme.Dimens

@Composable
fun TDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissText: String? = null,
    textFieldValue: String = "",
    onTextFieldValueChange: (String) -> Unit = {},
    textFieldLabel: String? = null
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            TText(text = title,)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                TText(
                    text = message,
                    color = Black,
                )

                if (textFieldLabel != null) {

                    Spacer(
                        modifier = Modifier.height(Dimens.mediumSpace)
                    )

                    TTextField(
                        value = textFieldValue,
                        onValueChange = onTextFieldValueChange,
                        type = TTextFieldType.TEXT,
                        labelTitle = textFieldLabel
                    )
                }
            }
        },
        confirmButton = {
            TButton(
                text = confirmText,
                onClick = onConfirm
            )
        },
        dismissButton = {
            dismissText?.let {
                TButton(
                    text = it,
                    type = TButtonType.REFUSE,
                    onClick = onDismiss
                )
            }
        }
    )
}