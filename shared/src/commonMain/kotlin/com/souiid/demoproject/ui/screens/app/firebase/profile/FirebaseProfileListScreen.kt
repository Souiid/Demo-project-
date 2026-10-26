package com.souiid.demoproject.ui.screens.app.firebase.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.posts
import demoproject.shared.generated.resources.followers
import demoproject.shared.generated.resources.followings
import org.jetbrains.compose.resources.stringResource

@Composable
fun FirebaseProfileListScreen(
    userId: String,
    type: ProfileListType,
    onUserClick: (String) -> Unit
) {
    val title = when (type) {
        ProfileListType.POSTS -> stringResource(Res.string.posts)
        ProfileListType.FOLLOWERS -> stringResource(Res.string.followers)
        ProfileListType.FOLLOWING -> stringResource(Res.string.followings)
    }

    TScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TText(
                text = title,
                style = TTextStyle.TITLE
            )
        }
    }
}