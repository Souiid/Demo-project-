package com.souiid.demoproject.ui.navigation.authgate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.souiid.demoproject.domain.model.AuthState
import com.souiid.demoproject.ui.navigation.AppNavigation
import com.souiid.demoproject.ui.navigation.AuthNavigation
import org.koin.compose.koinInject

@Composable
fun AuthGateScreen() {
    val viewModel: AuthGateViewModel = koinInject()
    val authState by viewModel.authState.collectAsState()

    when (authState) {
        AuthState.LOADING -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        AuthState.UNAUTHENTICATED -> AuthNavigation()
        AuthState.AUTHENTICATED -> AppNavigation()
    }
}