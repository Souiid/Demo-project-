package com.souiid.demoproject.ui.screens.app.firebase.friends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.souiid.demoproject.ui.components.item.firebase.UserItem
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import com.souiid.demoproject.ui.screens.app.firebase.FollowViewModel
import com.souiid.demoproject.ui.theme.Dimens
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.following
import demoproject.shared.generated.resources.noResults
import demoproject.shared.generated.resources.results
import demoproject.shared.generated.resources.searchUsers
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FriendsScreen(
    onUserClick: (String) -> Unit
) {
    val viewModel: FriendsViewModel = koinViewModel()
    val followViewModel: FollowViewModel = koinInject()

    val search by viewModel.searchQuery.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    val followingUsers by viewModel.followingUsers.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isFollowingUsersLoading by viewModel.isFollowingUsersLoading.collectAsStateWithLifecycle()

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val followingUserIds by followViewModel.followingUserIds.collectAsStateWithLifecycle()
    val updatingUserIds by followViewModel.updatingUserIds.collectAsStateWithLifecycle()
    val isFollowingLoading by followViewModel.isLoading.collectAsStateWithLifecycle()

    TScreen {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimens.standardPadding)
        ) {
            TTextField(
                value = search,
                onValueChange = viewModel::onSearchQueryChange,
                type = TTextFieldType.SEARCH,
                placeholder = stringResource(Res.string.searchUsers)
            )

            TText(
                text = stringResource(if (search.isNotBlank()) Res.string.results else Res.string.following),
                style = TTextStyle.ITEM_TITLE
            )

            if (search.isNotBlank()) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (users.isEmpty()) {
                    TText(
                        text = stringResource(Res.string.noResults),
                        style = TTextStyle.DESCRIPTION,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(users, key = { it.id }) { user ->
                            UserItem(
                                user = user,
                                isFollowing = user.id in followingUserIds,
                                isUpdating = isFollowingLoading || user.id in updatingUserIds,
                                isCurrentUser = user.id == currentUser?.id,
                                onUserClick = { onUserClick(user.id) },
                                onFollowClick = { followViewModel.toggleFollow(user.id) }
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                thickness = Dimens.dividerThickness
                            )
                        }
                    }
                }
            } else {
                if (isFollowingUsersLoading) {
                    CircularProgressIndicator()
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(followingUsers, key = { it.id }) { user ->
                            UserItem(
                                user = user,
                                isFollowing = user.id in followingUserIds,
                                isUpdating = isFollowingLoading || user.id in updatingUserIds,
                                isCurrentUser = user.id == currentUser?.id,
                                onUserClick = { onUserClick(user.id) },
                                onFollowClick = { followViewModel.toggleFollow(user.id) }
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                thickness = Dimens.dividerThickness
                            )
                        }
                    }
                }
            }
        }
    }
}