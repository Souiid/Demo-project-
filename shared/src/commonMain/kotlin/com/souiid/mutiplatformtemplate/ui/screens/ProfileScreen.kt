package com.souiid.mutiplatformtemplate.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import mutiplatformtemplate.shared.generated.resources.Res
import mutiplatformtemplate.shared.generated.resources.profile
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Res.string.profile),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}