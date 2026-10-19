package com.souiid.mutiplatformtemplate.ui.components.others

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.souiid.mutiplatformtemplate.ui.theme.Dimens

@Composable
fun TCard(
    modifier: Modifier = Modifier.fillMaxWidth(),
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        ),
        shape = RoundedCornerShape(Dimens.mediumRadius)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.standardPadding)
        ) {
            content()
        }
    }
}
