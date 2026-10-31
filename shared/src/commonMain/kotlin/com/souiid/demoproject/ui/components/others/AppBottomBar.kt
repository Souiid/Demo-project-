package com.souiid.demoproject.ui.components.others

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.navigation.Route
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.firebase
import demoproject.shared.generated.resources.room
import demoproject.shared.generated.resources.ktor
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

enum class BottomBarItem(
    val stringResource: StringResource,
    val route: Route,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    FIREBASE(Res.string.firebase, Route.FirebaseFeed(), AppIcon.Firebase, AppIcon.UnselectedFirebase),
    KTOR(Res.string.ktor, Route.Ktor, AppIcon.Ktor, AppIcon.UnselectedKtor),
    ROOM(Res.string.room, Route.Room, AppIcon.Room, AppIcon.UnselectedRoom)
}

@Composable
fun AppBottomBar(
    selectedItem: BottomBarItem,
    onItemSelected: (BottomBarItem) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        BottomBarItem.entries.forEach { item ->
            val isSelected = selectedItem == item
            val label = stringResource(item.stringResource)

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(item) },
                alwaysShowLabel = true,
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = label
                    )
                },
                label = {
                    TText(
                        text = label,
                        style = TTextStyle.LABEL,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}