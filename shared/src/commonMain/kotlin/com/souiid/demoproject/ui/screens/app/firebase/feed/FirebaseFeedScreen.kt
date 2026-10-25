package com.souiid.demoproject.ui.screens.app.firebase.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.souiid.demoproject.ui.components.CreatePostSheet
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.createPost
import demoproject.shared.generated.resources.firebase
import demoproject.shared.generated.resources.signOut
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun FirebaseFeedScreen(
    goToProfile: () -> Unit,
    goToFriends: () -> Unit
) {
    val viewModel: FirebaseFeedViewModel = koinInject()
    var showCreatePostSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }

    LaunchedEffect(viewModel.isPostCreated) {
        if (viewModel.isPostCreated) {
            showCreatePostSheet = false
            viewModel.onPostCreatedHandled()
        }
    }

    TScreen(verticalArrangement = Arrangement.Center) {
        TText(
            text = stringResource(Res.string.firebase),
            style = TTextStyle.TITLE
        )

        TButton(
            text = "Go to profile",
            onClick = goToProfile
        )

        TButton(
            text = "Go to friends",
            onClick = goToFriends
        )

        TButton(
            text = stringResource(Res.string.createPost),
            onClick = { showCreatePostSheet = true }
        )

        TButton(
            text = stringResource(Res.string.signOut),
            onClick = viewModel::signOut
        )
    }

    if (showCreatePostSheet) {
        CreatePostSheet(
            content = viewModel.content,
            isPublishing = viewModel.isPublishing,
            onContentChange = viewModel::onContentChange,
            onPublish = viewModel::createPost,
            onDismiss = { if (!viewModel.isPublishing) showCreatePostSheet = false }
        )
    }
}