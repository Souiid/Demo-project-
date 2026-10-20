package com.souiid.demoproject

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.souiid.demoproject.di.initKoin
import com.souiid.demoproject.ui.navigation.AppNavigation
import com.souiid.demoproject.ui.navigation.AuthNavigation
import com.souiid.demoproject.ui.theme.AppTheme

@Composable
fun App() {
    remember {
        initKoin()
    }

    AppTheme {
        AuthNavigation()
    }
}