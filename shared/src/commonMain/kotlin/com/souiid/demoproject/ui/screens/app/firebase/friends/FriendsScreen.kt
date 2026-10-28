package com.souiid.demoproject.ui.screens.app.firebase.friends

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.souiid.demoproject.ui.components.UserItem
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.searchUsers
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import demoproject.shared.generated.resources.followings
import demoproject.shared.generated.resources.results
import demoproject.shared.generated.resources.noResults

@Composable
fun FriendsScreen(
    onUserClick: (String) -> Unit
) {
    val viewModel: FriendsViewModel = koinInject()
    val search by viewModel.searchQuery.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    val followingUsers by viewModel.followingUsers.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isFollowingUsersLoading by viewModel.isFollowingUsersLoading.collectAsStateWithLifecycle()
    val isFollowingLoading by viewModel.isFollowingLoading.collectAsStateWithLifecycle()
    val followingUserIds by viewModel.followingUserIds.collectAsStateWithLifecycle()
    val updatingUserIds by viewModel.updatingUserIds.collectAsStateWithLifecycle()

    TScreen {
        TTextField(
            value = search,
            onValueChange = viewModel::onSearchQueryChange,
            type = TTextFieldType.SEARCH,
            placeholder = stringResource(Res.string.searchUsers)
        )

        if (search.isNotBlank()) {
            TText(
                text = stringResource(Res.string.results),
                style = TTextStyle.SUBTITLE
            )

            if (isLoading) {
                CircularProgressIndicator()
            } else if (users.isEmpty()) {
                TText(
                    text = stringResource(Res.string.noResults),
                    style = TTextStyle.BODY
                )
            } else {
                LazyColumn {
                    items(users, key = { it.id }) { user ->
                        UserItem(
                            user = user,
                            isFollowing = user.id in followingUserIds,
                            isUpdating = isFollowingLoading || user.id in updatingUserIds,
                            onUserClick = { onUserClick(user.id) },
                            onFollowClick = { viewModel.followUser(user.id) },
                            onUnfollowClick = { viewModel.unfollowUser(user.id) }
                        )
                    }
                }
            }
        } else {
            TText(
                text = stringResource(Res.string.followings),
                style = TTextStyle.SUBTITLE
            )

            if (isFollowingUsersLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(followingUsers, key = { it.id }) { user ->
                        UserItem(
                            user = user,
                            isFollowing = user.id in followingUserIds,
                            isUpdating = user.id in updatingUserIds,
                            onUserClick = { onUserClick(user.id) },
                            onFollowClick = { viewModel.followUser(user.id) },
                            onUnfollowClick = { viewModel.unfollowUser(user.id) }
                        )
                    }
                }
            }
        }
    }
}