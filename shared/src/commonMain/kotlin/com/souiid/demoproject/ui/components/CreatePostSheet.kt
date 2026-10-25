package com.souiid.demoproject.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.others.TBottomSheet
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.components.textfield.TTextField
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.createPost
import demoproject.shared.generated.resources.publish
import demoproject.shared.generated.resources.writePost
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreatePostSheet(
    content: String,
    isPublishing: Boolean,
    onContentChange: (String) -> Unit,
    onPublish: () -> Unit,
    onDismiss: () -> Unit
) {
    TBottomSheet(onDismissRequest = onDismiss) {
        TText(
            text = stringResource(Res.string.createPost),
            style = TTextStyle.TITLE
        )

        Spacer(Modifier.height(16.dp))

        TTextField(
            value = content,
            onValueChange = onContentChange,
            placeholder = stringResource(Res.string.writePost)
        )

        Spacer(Modifier.height(16.dp))

        if (isPublishing) {
            CircularProgressIndicator()
        }

        TButton(
            text = stringResource(Res.string.publish),
            enabled = content.isNotBlank() && content.length <= 1000 && !isPublishing,
            onClick = onPublish
        )

        Spacer(Modifier.height(16.dp))
    }
}