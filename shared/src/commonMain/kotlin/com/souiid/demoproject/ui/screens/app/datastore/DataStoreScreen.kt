package com.souiid.demoproject.ui.screens.app.datastore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.ktor
import org.jetbrains.compose.resources.stringResource

@Composable
fun DataStoreScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.ktor),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}