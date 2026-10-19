package com.souiid.demoproject.ui.components.datetime

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.theme.Dimens
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.choose_a_date
import demoproject.shared.generated.resources.continu
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

@Composable
fun TDateDialog(
    selectedDate: LocalDate?,
    minDate: LocalDate,
    maxDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.largeRadius),
            color = MaterialTheme.colorScheme.surface
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.largeSpace)
            ) {

                TText(
                    text = stringResource(Res.string.choose_a_date),
                    style = TTextStyle.SUBTITLE
                )

                Spacer(
                    modifier = Modifier.height(Dimens.mediumSpace)
                )

                TWheelDatePicker(
                    selectedDate = selectedDate,
                    minDate = minDate,
                    maxDate = maxDate,
                    onDateSelected = onDateSelected
                )

                Spacer(
                    modifier = Modifier.height(Dimens.largeSpace)
                )

                TButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.continu),
                    enabled = selectedDate != null,
                    onClick = onConfirm
                )
            }
        }
    }
}