package com.souiid.demoproject.ui.components.others

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.souiid.demoproject.ui.navigation.Route
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.home
import demoproject.shared.generated.resources.profile
import demoproject.shared.generated.resources.search
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

enum class BottomBarItem(
    val stringResource: StringResource,
    val route: Route,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(Res.string.home, Route.Home, AppIcon.Home, AppIcon.UnselectedHome),
    SEARCH(Res.string.search, Route.Search, AppIcon.Search, AppIcon.UnselectedSearch),
    PROFILE(Res.string.profile, Route.Profile, AppIcon.Profile, AppIcon.UnselectedProfile)
}

@Composable
fun AppBottomBar(
    selectedItem: BottomBarItem,
    onItemSelected: (BottomBarItem) -> Unit
) {
    NavigationBar {
        BottomBarItem.entries.forEach { item ->
            NavigationBarItem(
                selected = selectedItem == item,
                onClick = { onItemSelected(item) },
                icon = {
                    Icon(
                        imageVector = if (selectedItem == item) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(item.stringResource)
                    )
                }
            )
        }
    }
}