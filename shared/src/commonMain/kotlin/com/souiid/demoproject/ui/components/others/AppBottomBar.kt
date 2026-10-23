package com.souiid.demoproject.ui.components.others

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.souiid.demoproject.ui.navigation.Route
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.firebase
import demoproject.shared.generated.resources.datastore
import demoproject.shared.generated.resources.ktor
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

enum class BottomBarItem(
    val stringResource: StringResource,
    val route: Route,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    FIREBASE(Res.string.firebase, Route.FirebaseFeed, AppIcon.Firebase, AppIcon.UnselectedFirebase),
    KTOR(Res.string.ktor, Route.Ktor, AppIcon.Ktor, AppIcon.UnselectedKtor),
    DATASTORE(Res.string.datastore, Route.DataStore, AppIcon.DataStore, AppIcon.UnselectedDataStore)
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