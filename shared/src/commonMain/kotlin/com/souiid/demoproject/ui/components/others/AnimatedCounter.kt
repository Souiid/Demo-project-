package com.souiid.demoproject.ui.components.others

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle

@Composable
fun AnimatedCounter(
    value: Int,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = value,
        modifier = modifier,
        transitionSpec = {
            val isIncreasing = targetState > initialState

            (slideInVertically(
                animationSpec = tween(250),
                initialOffsetY = { if (isIncreasing) -it else it }
            ) + fadeIn(animationSpec = tween(250)))
                .togetherWith(
                    slideOutVertically(
                        animationSpec = tween(250),
                        targetOffsetY = { if (isIncreasing) it else -it }
                    ) + fadeOut(animationSpec = tween(250))
                )
        },
        label = "CounterAnimation"
    ) { count ->
        TText(
            text = count.toString(),
            style = TTextStyle.TITLE
        )
    }
}
