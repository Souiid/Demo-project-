package com.souiid.demoproject.ui.components.item.firebase

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.souiid.demoproject.firebase.domain.model.Post
import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.ui.components.others.AppIcon
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.theme.Dimens
import com.souiid.demoproject.ui.utils.PostDateFormatter

@Composable
fun PostItem(
    post: Post,
    author: User?,
    isLiked: Boolean = false,
    isCurrentUser: Boolean = false,
    isDeleting: Boolean = false,
    onDelete: () -> Unit = {},
    onLike: () -> Unit = {},
    onAuthorClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.mediumSpace),
        verticalAlignment = Alignment.Top
    ) {
        TKamelImage(
            imageUrl = author?.photoUrl.orEmpty(),
            modifier = Modifier.size(Dimens.avatarSize).clickable { onAuthorClick(post.authorId) }
        ) {
            TText(
                text = author?.firstName?.firstOrNull()?.toString() ?: "?",
                style = TTextStyle.BODY
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Dimens.smallSpace)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f).clickable { onAuthorClick(post.authorId) },
                    verticalArrangement = Arrangement.spacedBy(Dimens.tinySpace)
                ) {
                    TText(
                        text = author?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown user",
                        style = TTextStyle.ITEM_TITLE,
                        maxLines = 1
                    )

                    TText(
                        text = PostDateFormatter.format(post.createdAt),
                        style = TTextStyle.DESCRIPTION
                    )
                }

                if (isCurrentUser) {
                    if (isDeleting) {
                        CircularProgressIndicator(modifier = Modifier.size(Dimens.progressIndicatorSize))
                    } else {
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = AppIcon.Delete,
                                contentDescription = "Delete post",
                                modifier = Modifier.size(Dimens.standardIconSize),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            TText(
                text = post.content,
                style = TTextStyle.BODY,
                maxLines = Int.MAX_VALUE
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.smallSpace)
            ) {
                Icon(
                    imageVector = if (isLiked) AppIcon.Favorite else AppIcon.FavoriteBorder,
                    contentDescription = if (isLiked) "Unlike" else "Like",
                    modifier = Modifier
                        .size(Dimens.standardIconSize)
                        .clickable(enabled = !isDeleting) { onLike() },
                    tint = if (isLiked) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )

                TText(
                    text = post.likesCount.toString(),
                    style = TTextStyle.DESCRIPTION
                )
            }
        }
    }
}
