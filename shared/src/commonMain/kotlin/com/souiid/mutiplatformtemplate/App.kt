package com.souiid.mutiplatformtemplate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.souiid.mutiplatformtemplate.di.initKoin
import com.souiid.mutiplatformtemplate.ui.navigation.AppNavigation
import com.souiid.mutiplatformtemplate.ui.theme.AppTheme

@Composable
fun App() {
    remember {
        initKoin()
    }

    AppTheme {
        AppNavigation()
    }
}