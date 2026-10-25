package com.souiid.demoproject.ui.screens.app.firebase.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.ui.components.CreatePostSheet
import com.souiid.demoproject.ui.components.PostItem
import com.souiid.demoproject.ui.components.others.AppIcon
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.createPost
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun FirebaseFeedScreen() {
    val viewModel: FirebaseFeedViewModel = koinInject()
    var showCreatePostSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
        viewModel.loadPosts()
    }

    LaunchedEffect(viewModel.isPostCreated) {
        if (viewModel.isPostCreated) {
            showCreatePostSheet = false
            viewModel.onPostCreatedHandled()
        }
    }

    TScreen {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                viewModel.isLoadingPosts -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                viewModel.posts.isEmpty() -> {
                    TText(
                        text = "No posts yet",
                        style = TTextStyle.BODY,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.posts, key = { it.id }) { post ->
                            PostItem(
                                post = post,
                                author = viewModel.authors[post.authorId]
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showCreatePostSheet = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = AppIcon.Add,
                    contentDescription = stringResource(Res.string.createPost)
                )
            }
        }
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