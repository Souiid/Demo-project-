package com.souiid.demoproject.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.domain.model.Post
import com.souiid.demoproject.domain.model.User
import com.souiid.demoproject.ui.components.others.AppIcon
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.utils.PostDateFormatter

@Composable
fun PostItem(
    post: Post,
    author: User?,
    onLike: () -> Unit = {},
    onAuthorClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.clickable(onClick = { onAuthorClick(post.authorId) }),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TKamelImage(
                imageUrl = author?.photoUrl.orEmpty(),
                modifier = Modifier.size(44.dp)
            ) {
                TText(
                    text = author?.firstName?.firstOrNull()?.toString() ?: "?",
                    style = TTextStyle.BODY
                )
            }

            Column {
                TText(
                    text = author?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown user",
                    style = TTextStyle.SUBTITLE
                )

                TText(
                    text = PostDateFormatter.format(post.createdAt),
                    style = TTextStyle.DESCRIPTION
                )
            }
        }

        TText(
            text = post.content,
            style = TTextStyle.BODY
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onLike, enabled = false) {
                Icon(
                    imageVector = AppIcon.FavoriteBorder,
                    contentDescription = "Like"
                )
            }

            TText(
                text = post.likesCount.toString(),
                style = TTextStyle.BODY
            )
        }
    }
}