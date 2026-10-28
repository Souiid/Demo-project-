package com.souiid.demoproject.ui.screens.app.firebase.editprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.domain.model.User
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.button.TButtonType
import com.souiid.demoproject.ui.components.others.PhotoPicker
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import com.souiid.demoproject.ui.utils.appErrorMessage
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.description
import demoproject.shared.generated.resources.firstName
import demoproject.shared.generated.resources.lastName
import demoproject.shared.generated.resources.modifyPhoto
import demoproject.shared.generated.resources.save
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun EditProfileScreen() {
    val viewModel: EditProfileViewModel = koinInject()
    val currentUser by viewModel.currentUser.collectAsState()

    TScreen(horizontalAlignment = Alignment.CenterHorizontally) {
        currentUser?.let { user ->
            EditProfilePhoto(
                user = user,
                photoUri = viewModel.photoUri,
                onPhotoSelected = { uri, mimeType ->
                    viewModel.photoUri = uri
                    viewModel.photoMimeType = mimeType
                }
            )
            EditProfileFields(viewModel)
            EditProfileSaveButton(viewModel)
        }
    }
}

@Composable
private fun EditProfileFields(viewModel: EditProfileViewModel) {
    TTextField(
        labelTitle = stringResource(Res.string.firstName),
        value = viewModel.firstName,
        onValueChange = {
            viewModel.clearError()
            viewModel.firstName = it
        },
        type = TTextFieldType.TEXT,
        imeAction = ImeAction.Next,
        errorMessage = (viewModel.error as? AppError.Validation)
            ?.takeIf { it.field == AppError.Field.FIRST_NAME }
            ?.let { appErrorMessage(it) }
    )

    TTextField(
        labelTitle = stringResource(Res.string.lastName),
        value = viewModel.lastName,
        onValueChange = {
            viewModel.clearError()
            viewModel.lastName = it
        },
        type = TTextFieldType.TEXT,
        imeAction = ImeAction.Next,
        errorMessage = (viewModel.error as? AppError.Validation)
            ?.takeIf { it.field == AppError.Field.LAST_NAME }
            ?.let { appErrorMessage(it) }
    )

    TTextField(
        labelTitle = stringResource(Res.string.description),
        value = viewModel.description,
        onValueChange = {
            viewModel.clearError()
            viewModel.description = it
        },
        type = TTextFieldType.TEXT,
        imeAction = ImeAction.Done
    )
}

@Composable
private fun EditProfileSaveButton(viewModel: EditProfileViewModel) {
    TButton(
        text = stringResource(Res.string.save),
        onClick = {
            viewModel.updateUser()
        },
        isLoading = viewModel.isLoading
    )
}

@Composable
private fun EditProfilePhoto(
    user: User,
    photoUri: String?,
    onPhotoSelected: (String?, String?) -> Unit
) {
    PhotoPicker(
        photoUri = photoUri,
        onPhotoSelected = onPhotoSelected
    ) { _, openPicker ->
        Box(
            modifier = Modifier.size(104.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            TKamelImage(
                imageUrl = user.photoUrl,
                photoUri = photoUri,
                modifier = Modifier.fillMaxSize()
            ) {
                TText(
                    text = "${user.firstName.firstOrNull() ?: ""}${user.lastName.firstOrNull() ?: ""}",
                    style = TTextStyle.SUBTITLE
                )
            }

            IconButton(
                onClick = openPicker,
                modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.modifyPhoto),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        TButton(
            text = stringResource(Res.string.modifyPhoto),
            type = TButtonType.TEXT,
            onClick = openPicker
        )
    }
}