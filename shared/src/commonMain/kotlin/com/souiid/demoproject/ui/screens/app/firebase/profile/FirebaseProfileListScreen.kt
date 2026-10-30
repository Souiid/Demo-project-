
package com.souiid.demoproject.ui.screens.app.firebase.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.souiid.demoproject.firebase.domain.usecase.user.ObserveCurrentUserUseCase
import com.souiid.demoproject.ui.components.item.firebase.PostItem
import com.souiid.demoproject.ui.components.item.firebase.UserItem
import com.souiid.demoproject.ui.components.others.TDialog
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.screens.app.firebase.FollowViewModel
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.cancel
import demoproject.shared.generated.resources.delete
import demoproject.shared.generated.resources.delete_post
import demoproject.shared.generated.resources.delete_post_confirmation
import demoproject.shared.generated.resources.followers
import demoproject.shared.generated.resources.followings
import demoproject.shared.generated.resources.posts
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
    val deletingPostIds by viewModel.deletingPostIds.collectAsStateWithLifecycle()

    val currentUser by observeCurrentUserUseCase().collectAsStateWithLifecycle()
    val followingUserIds by followViewModel.followingUserIds.collectAsStateWithLifecycle()
    val updatingUserIds by followViewModel.updatingUserIds.collectAsStateWithLifecycle()
    val isFollowingLoading by followViewModel.isLoading.collectAsStateWithLifecycle()

    var postToDelete by remember { mutableStateOf<String?>(null) }

    val title = when (type) {
        ProfileListType.POSTS -> stringResource(Res.string.posts)
        ProfileListType.FOLLOWERS -> stringResource(Res.string.followers)
        ProfileListType.FOLLOWING -> stringResource(Res.string.followings)
    }

    LaunchedEffect(userId, type) {
        viewModel.loadData(userId, type)
        postToDelete = null
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
                                    isCurrentUser = post.authorId == currentUser?.id,
                                    isDeleting = post.id in deletingPostIds,
                                    onLike = { viewModel.toggleLike(post.id) },
                                    onDelete = { postToDelete = post.id },
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
                                    isFollowing = user.id in followingUserIds,
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

        postToDelete?.let { postId ->
            TDialog(
                title = stringResource(Res.string.delete_post),
                message = stringResource(Res.string.delete_post_confirmation),
                confirmText = stringResource(Res.string.delete),
                dismissText = stringResource(Res.string.cancel),
                onConfirm = {
                    postToDelete = null
                    viewModel.deletePost(postId)
                },
                onDismiss = { postToDelete = null }
            )
        }
    }
}
