package com.souiid.demoproject.ui.screens.app.firebase.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.souiid.demoproject.domain.model.ProfileStats
import com.souiid.demoproject.domain.model.User
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.button.TButtonType
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.follow
import demoproject.shared.generated.resources.signOut
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import demoproject.shared.generated.resources.posts
import demoproject.shared.generated.resources.followers
import demoproject.shared.generated.resources.followings
import demoproject.shared.generated.resources.unfollow

@Composable
fun FirebaseProfileScreen(
    userId: String? = null,
    goToEditProfile: () -> Unit,
    onStatClick: (String, ProfileListType) -> Unit

) {
    val viewModel: FirebaseProfileViewModel = koinInject()
    val displayedUser by viewModel.displayedUser.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val profileStats by viewModel.profileStats.collectAsStateWithLifecycle()
    val isFollowing by viewModel.isFollowing.collectAsStateWithLifecycle()
    val isUpdatingFollow by viewModel.isUpdatingFollow.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    TScreen {
        when {
            viewModel.isLoading -> {
                CircularProgressIndicator()
            }

            displayedUser != null -> {
                ProfileContent(
                    user = displayedUser!!,
                    stats = profileStats,
                    isLoadingStats = viewModel.isLoadingStats,
                    isCurrentUser = currentUser?.id != null && displayedUser?.id == currentUser?.id,
                    isFollowing = isFollowing,
                    isUpdatingFollow = isUpdatingFollow,
                    onFollowClick = viewModel::toggleFollow,
                    goToEditProfile = goToEditProfile,
                    onSignOut = viewModel::signOut,
                    onStatClick = onStatClick
                )
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
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    stats: ProfileStats,
    isLoadingStats: Boolean,
    isCurrentUser: Boolean,
    isFollowing: Boolean,
    isUpdatingFollow: Boolean,
    onFollowClick: () -> Unit,
    goToEditProfile: () -> Unit,
    onSignOut: () -> Unit,
    onStatClick: (String, ProfileListType) -> Unit
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

            TText(
                text = user.description,
                style = TTextStyle.BODY
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoadingStats) {
            CircularProgressIndicator()
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(
                    value = stats.posts.toString(),
                    label = stringResource(Res.string.posts),
                    onClick = { onStatClick(user.id, ProfileListType.POSTS) }
                )

                ProfileStat(
                    value = stats.followers.toString(),
                    label = stringResource(Res.string.followers),
                    onClick = { onStatClick(user.id, ProfileListType.FOLLOWERS) }
                )

                ProfileStat(
                    value = stats.following.toString(),
                    label = stringResource(Res.string.followings),
                    onClick = { onStatClick(user.id, ProfileListType.FOLLOWING) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isCurrentUser) {
            TButton(
                text = "Edit profile",
                onClick = goToEditProfile
            )

            Spacer(modifier = Modifier.height(12.dp))

            TButton(
                text = stringResource(Res.string.signOut),
                onClick = onSignOut
            )
        } else {
            TButton(
                text = stringResource(if (isFollowing) Res.string.unfollow else Res.string.follow),
                type = if (isFollowing) TButtonType.REFUSE else TButtonType.PRIMARY,
                isLoading = isUpdatingFollow,
                enabled = !isUpdatingFollow,
                onClick = onFollowClick
            )
        }
    }
}

@Composable
private fun ProfileStat(
    value: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TText(text = value, style = TTextStyle.TITLE)
        TText(text = label, style = TTextStyle.BODY)
    }
}