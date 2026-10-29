package com.souiid.demoproject.ui.components.item.firebase

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.follow
import demoproject.shared.generated.resources.unfollow
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserItem(
    user: User,
    onUserClick: () -> Unit,
    isFollowing: Boolean? = null,
    isUpdating: Boolean = false,
    onFollowClick: () -> Unit = {},
    onUnfollowClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TKamelImage(
            imageUrl = user.photoUrl,
            modifier = Modifier.size(48.dp).clickable(onClick = onUserClick)
        ) {
            TText(
                text = "${user.firstName.firstOrNull() ?: ""}${user.lastName.firstOrNull() ?: ""}",
                style = TTextStyle.BODY
            )
        }

        Column(
            modifier = Modifier.weight(1f).clickable(onClick = onUserClick)
        ) {
            TText(
                text = "${user.firstName} ${user.lastName}",
                style = TTextStyle.SUBTITLE
            )
        }

        if (isFollowing != null) {
            TButton(
                text = stringResource(if (isFollowing) Res.string.unfollow else Res.string.follow),
                enabled = !isUpdating,
                onClick = {
                    if (isFollowing) onUnfollowClick()
                    else onFollowClick()
                }
            )
        }
    }
}