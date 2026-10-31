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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.souiid.demoproject.firebase.domain.model.ProfileStats
import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.button.TButtonType
import com.souiid.demoproject.ui.components.others.AnimatedCounter
import com.souiid.demoproject.ui.components.others.AppIcon
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.screens.app.firebase.FollowViewModel
import com.souiid.demoproject.ui.theme.Dimens
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.editProfile
import demoproject.shared.generated.resources.follow
import demoproject.shared.generated.resources.followers
import demoproject.shared.generated.resources.following
import demoproject.shared.generated.resources.posts
import demoproject.shared.generated.resources.profileLoadError
import demoproject.shared.generated.resources.retry
import demoproject.shared.generated.resources.signOut
import demoproject.shared.generated.resources.unfollow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FirebaseProfileScreen(
    userId: String? = null,
    goToEditProfile: () -> Unit,
    onStatClick: (String, ProfileListType) -> Unit
) {
    val viewModel: FirebaseProfileViewModel = koinViewModel()
    val followViewModel: FollowViewModel = koinInject()

    val displayedUser by viewModel.displayedUser.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val profileStats by viewModel.profileStats.collectAsStateWithLifecycle()
    val followingUserIds by followViewModel.followingUserIds.collectAsStateWithLifecycle()
    val updatingUserIds by followViewModel.updatingUserIds.collectAsStateWithLifecycle()
    val isFollowingLoading by followViewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    TScreen {
        when {
            viewModel.isLoading -> {
                CircularProgressIndicator()
            }

            displayedUser != null -> {
                displayedUser?.let { user->
                    key(user.id) {
                        ProfileContent(
                            user = user,
                            stats = profileStats,
                            hasLoadedStats = viewModel.hasLoadedStats,
                            isCurrentUser = currentUser?.id == user.id,
                            isFollowing = user.id in followingUserIds,
                            isUpdatingFollow = isFollowingLoading || user.id in updatingUserIds,
                            onFollowClick = { followViewModel.toggleFollow(user.id) },
                            goToEditProfile = goToEditProfile,
                            onSignOut = viewModel::signOut,
                            onStatClick = onStatClick
                        )
                    }
                }
            }

            viewModel.error != null -> {
                TText(
                    text = stringResource(Res.string.profileLoadError),
                    style = TTextStyle.BODY
                )

                TButton(
                    text = stringResource(Res.string.retry),
                    onClick = { viewModel.loadUser(userId, forceRefresh = true) }
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: User,
    stats: ProfileStats,
    hasLoadedStats: Boolean,
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
        verticalArrangement = Arrangement.spacedBy(Dimens.extraLargeSpace)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.standardPadding)
        ) {
            TKamelImage(
                imageUrl = user.photoUrl,
                modifier = Modifier.size(Dimens.profileAvatarSize)
            ) {
                TText(
                    text = "${user.firstName.firstOrNull() ?: ""}${user.lastName.firstOrNull() ?: ""}",
                    style = TTextStyle.SUBTITLE
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.tinySpace)
            ) {
                TText(
                    text = "${user.firstName} ${user.lastName}",
                    style = TTextStyle.SUBTITLE
                )

                if (user.description.isNotBlank()) {
                    TText(
                        text = user.description,
                        style = TTextStyle.DESCRIPTION,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (hasLoadedStats) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(
                    modifier = Modifier.weight(1f),
                    value = stats.posts,
                    label = stringResource(Res.string.posts),
                    onClick = { onStatClick(user.id, ProfileListType.POSTS) }
                )

                ProfileStat(
                    modifier = Modifier.weight(1f),
                    value = stats.followers,
                    label = stringResource(Res.string.followers),
                    onClick = { onStatClick(user.id, ProfileListType.FOLLOWERS) }
                )

                ProfileStat(
                    modifier = Modifier.weight(1f),
                    value = stats.following,
                    label = stringResource(Res.string.following),
                    onClick = { onStatClick(user.id, ProfileListType.FOLLOWING) }
                )
            }
        } else {
            CircularProgressIndicator()
        }

        if (isCurrentUser) {
            TButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.editProfile),
                onClick = goToEditProfile
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = Dimens.dividerThickness
            )

            TButton(
                text = stringResource(Res.string.signOut),
                type = TButtonType.ICON_TEXT,
                icon = AppIcon.SignOut,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onSignOut
            )
        } else {
            TButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(if (isFollowing) Res.string.unfollow else Res.string.follow),
                type = if (isFollowing) TButtonType.NEUTRAL else TButtonType.PRIMARY,
                isLoading = isUpdatingFollow,
                enabled = !isUpdatingFollow,
                onClick = onFollowClick
            )
        }
    }
}

@Composable
private fun ProfileStat(
    modifier: Modifier = Modifier,
    value: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(Dimens.profileStatMinHeight)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedCounter(value = value)

        Spacer(modifier = Modifier.height(Dimens.tinySpace))

        TText(
            text = label,
            style = TTextStyle.LABEL,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
