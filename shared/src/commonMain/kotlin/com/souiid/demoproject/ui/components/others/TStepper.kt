package com.souiid.demoproject.ui.components.others

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.theme.Dimens

@Composable
fun TStepper(
    value: Int,
    maxValue: Int,
    minValue: Int,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.standardPadding)
    ) {

        TButton(
            text = "-",
            onClick = {
                if (value > minValue) {
                    onMinusClick()
                }
            }
        )

        TText(
            text = value.toString(),
        )

        TButton(
            text = "+",
            onClick = {
                if (value < maxValue) {
                    onPlusClick()
                }
            }
        )
    }
}