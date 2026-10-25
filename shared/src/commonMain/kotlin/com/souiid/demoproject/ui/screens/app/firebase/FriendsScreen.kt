package com.souiid.demoproject.ui.screens.app.firebase

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.domain.model.User
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.others.TKamelImage
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.follow
import demoproject.shared.generated.resources.following
import demoproject.shared.generated.resources.searchUsers
import org.jetbrains.compose.resources.stringResource

@Composable
fun FriendsScreen(
    onUserClick: (String) -> Unit
) {
    var search by remember { mutableStateOf("") }

    val users = remember {
        listOf(
            User(id = "1", firstName = "Lucas", lastName = "Martin", description = ""),
            User(id = "2", firstName = "Emma", lastName = "Bernard", description = ""),
            User(id = "3", firstName = "Hugo", lastName = "Petit", description = "")
        )
    }

    val displayedUsers = users.filter {
        "${it.firstName} ${it.lastName}".contains(search.trim(), ignoreCase = true)
    }

    TScreen {
        TTextField(
            value = search,
            onValueChange = { search = it },
            type = TTextFieldType.SEARCH,
            placeholder = stringResource(Res.string.searchUsers)
        )

        LazyColumn {
            items(displayedUsers, key = { it.id }) { user ->
                FriendItem(
                    user = user,
                    onUserClick = { onUserClick(user.id) }
                )
            }
        }
    }
}

@Composable
fun FriendItem(
    user: User,
    onUserClick: () -> Unit
) {
    var isFollowing by remember(user.id) { mutableStateOf(false) }

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

        TText(
            text = "${user.firstName} ${user.lastName}",
            style = TTextStyle.SUBTITLE,
            modifier = Modifier.weight(1f)
        )

        TButton(
            text = stringResource(if (isFollowing) Res.string.following else Res.string.follow),
            onClick = { isFollowing = !isFollowing }
        )
    }
}