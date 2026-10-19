package com.souiid.mutiplatformtemplate.ui.components.others

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.souiid.mutiplatformtemplate.ui.theme.Dimens

@Composable
fun TScreen(
    isScrollable: Boolean = false,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    bottomBar: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {

    val focusManager = LocalFocusManager.current
    val bottomBarHeight = 50.dp

    val scrollModifier = if (isScrollable) {
            Modifier.verticalScroll(rememberScrollState())
        } else {
            Modifier
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(scrollModifier)
                .padding(horizontal = Dimens.standardPadding)
                .padding(bottom = if (bottomBar != null) bottomBarHeight else 0.dp),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {
            content()
        }

        if (bottomBar != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth().
                    padding(horizontal = Dimens.standardPadding)
            ) {
                bottomBar()
            }
        }
    }
}