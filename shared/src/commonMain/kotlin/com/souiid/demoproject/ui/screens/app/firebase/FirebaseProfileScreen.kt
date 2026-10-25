package com.souiid.demoproject.ui.screens.app.firebase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.souiid.demoproject.domain.model.User
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import org.koin.compose.koinInject

@Composable
fun FirebaseProfileScreen(
    userId: String? = null,
    goToEditProfile: () -> Unit
) {
    val viewModel: FirebaseProfileViewModel = koinInject()
    val displayedUser by viewModel.displayedUser.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    TScreen {
        when {
            viewModel.isLoading -> {
                CircularProgressIndicator()
            }

            viewModel.error != null -> {
                TText(
                    text = "Unable to load profile",
                    style = TTextStyle.BODY
                )

                TButton(
                    text = "Retry",
                    onClick = { viewModel.loadUser(userId) }
                )
            }

            displayedUser != null -> {
                ProfileContent(
                    user = displayedUser!!,
                    isCurrentUser = displayedUser?.id == currentUser?.id,
                    goToEditProfile = goToEditProfile
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    isCurrentUser: Boolean,
    goToEditProfile: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TKamelImage(
            imageUrl = user.photoUrl,
            modifier = Modifier.size(96.dp)
        ) {
            TText(
                text = "${user.firstName.firstOrNull() ?: ""}${user.lastName.firstOrNull() ?: ""}",
                style = TTextStyle.TITLE
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TText(
            text = "${user.firstName} ${user.lastName}",
            style = TTextStyle.TITLE
        )

        if (user.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            TText(text = user.description, style = TTextStyle.BODY)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileStat("0", "Posts")
            ProfileStat("0", "Followers")
            ProfileStat("0", "Following")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isCurrentUser) {
            TButton(text = "Edit profile", onClick = {
                goToEditProfile()
            })
        } else {
            TButton(text = "Follow", onClick = {})
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TText(text = value, style = TTextStyle.TITLE)
        TText(text = label, style = TTextStyle.BODY)
    }
}