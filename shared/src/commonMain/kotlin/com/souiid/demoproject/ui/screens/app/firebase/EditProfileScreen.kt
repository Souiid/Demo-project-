package com.souiid.demoproject.ui.screens.app.firebase

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
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import com.souiid.demoproject.ui.utils.appErrorMessage
import org.koin.compose.koinInject

@Composable
fun EditProfileScreen() {
    val viewModel: EditProfileViewModel = koinInject()
    val currentUser by viewModel.currentUser.collectAsState()

    TScreen(horizontalAlignment = Alignment.CenterHorizontally) {
        currentUser?.let { user ->
            EditProfilePhoto(user = user)
            EditProfileFields(viewModel)
            EditProfileSaveButton(viewModel)
        }
    }
}

@Composable
private fun EditProfilePhoto(user: User) {
    Box(
        modifier = Modifier.size(104.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        TKamelImage(
            imageUrl = user.photoUrl,
            modifier = Modifier.fillMaxSize()
        ) {
            TText(
                text = "${user.firstName.firstOrNull() ?: ""}${user.lastName.firstOrNull() ?: ""}",
                style = TTextStyle.SUBTITLE
            )
        }

        IconButton(
            onClick = {},
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Modifier la photo",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    TButton(
        text = "Modifier la photo de profil",
        type = TButtonType.TEXT,
        onClick = {}
    )
}

@Composable
private fun EditProfileFields(viewModel: EditProfileViewModel) {
    TTextField(
        labelTitle = "Prénom",
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
        labelTitle = "Nom",
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
        labelTitle = "Description",
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
        text = "Enregistrer",
        onClick = {
            viewModel.updateUser()
        },
        isLoading = viewModel.isLoading
    )
}