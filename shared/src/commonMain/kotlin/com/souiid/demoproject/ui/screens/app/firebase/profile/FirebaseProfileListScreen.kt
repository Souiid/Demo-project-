
package com.souiid.demoproject.ui.screens.app.firebase.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.souiid.demoproject.ui.components.item.firebase.PostItem
import com.souiid.demoproject.ui.components.item.firebase.UserItem
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.screens.app.firebase.FollowViewModel
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.posts
import demoproject.shared.generated.resources.followers
import demoproject.shared.generated.resources.followings
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun FirebaseProfileListScreen(
    userId: String,
    type: ProfileListType,
    onUserClick: (String) -> Unit
) {
    val viewModel: FirebaseProfileListViewModel = koinInject()
    val followViewModel: FollowViewModel = koinInject()
    val observeCurrentUserUseCase: ObserveCurrentUserUseCase = koinInject()

    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val author by viewModel.author.collectAsStateWithLifecycle()
    val likedPostIds by viewModel.likedPostIds.collectAsStateWithLifecycle()

    val currentUser by observeCurrentUserUseCase().collectAsStateWithLifecycle()
    val followingUserIds by followViewModel.followingUserIds.collectAsStateWithLifecycle()
    val updatingUserIds by followViewModel.updatingUserIds.collectAsStateWithLifecycle()
    val isFollowingLoading by followViewModel.isLoading.collectAsStateWithLifecycle()

    val title = when (type) {
        ProfileListType.POSTS -> stringResource(Res.string.posts)
        ProfileListType.FOLLOWERS -> stringResource(Res.string.followers)
        ProfileListType.FOLLOWING -> stringResource(Res.string.followings)
    }

    LaunchedEffect(userId, type) {
        viewModel.loadData(userId, type)
    }

    TScreen {
        Column(modifier = Modifier.fillMaxSize()) {
            TText(text = title, style = TTextStyle.TITLE)

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                when (type) {
                    ProfileListType.POSTS -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(posts, key = { it.id }) { post ->
                                PostItem(
                                    post = post,
                                    author = author,
                                    isLiked = post.id in likedPostIds,
                                    onLike = { viewModel.toggleLike(post.id) },
                                    onAuthorClick = onUserClick
                                )
                            }
                        }
                    }

                    ProfileListType.FOLLOWERS, ProfileListType.FOLLOWING -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(users, key = { it.id }) { user ->
                                UserItem(
                                    user = user,
                                    isFollowing = (if (user.id == currentUser?.id) null else user.id in followingUserIds) == true,
                                    isUpdating = isFollowingLoading || user.id in updatingUserIds,
                                    isCurrentUser = user.id == currentUser?.id,
                                    onUserClick = { onUserClick(user.id) },
                                    onFollowClick = { followViewModel.toggleFollow(user.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
