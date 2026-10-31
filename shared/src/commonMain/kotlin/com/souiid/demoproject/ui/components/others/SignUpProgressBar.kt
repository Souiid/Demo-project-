package com.souiid.demoproject.ui.components.others

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.theme.Dimens
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.finalStep
import demoproject.shared.generated.resources.stepOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignUpProgressBar(step: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = (step / 3f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = "signUpProgress"
    )
    val shape = RoundedCornerShape(100)

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.standardPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.smallSpace),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.signUpProgressHeight)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        TText(
            text = if (step == 3) stringResource(Res.string.finalStep)
            else stringResource(Res.string.stepOf, step, 3),
            style = TTextStyle.DESCRIPTION
        )
    }
}