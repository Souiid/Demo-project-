package com.souiid.demoproject.ui.components.others

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.theme.Dimens

data class SegmentItem(
    val title: String,
    val icon: ImageVector? = null
)

@Composable
fun TSegmentedControl(
    modifier: Modifier = Modifier,
    options: List<SegmentItem>,
    selectedIndex: Int = 0,
    selectedIndices: Set<Int> = emptySet(),
    multiSelection: Boolean = false,
    onSelectedIndexChange: (Int) -> Unit = {},
    onSelectedIndicesChange: (Set<Int>) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.mediumRadius))
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            .padding(Dimens.tinyPadding),
        horizontalArrangement = Arrangement.spacedBy(Dimens.tinySpace)
    ) {
        options.forEachIndexed { index, option ->

            val selected = if (multiSelection) {
                index in selectedIndices
            } else {
                index == selectedIndex
            }

            val backgroundColor by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                } else {
                    Color.Transparent
                },
                label = "SegmentBackground"
            )

            val borderColor by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                } else {
                    Color.Transparent
                },
                label = "SegmentBorder"
            )

            val contentColor by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                label = "SegmentContent"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(Dimens.mediumRadius))
                    .background(backgroundColor)
                    .border(
                        width = if (selected) 1.dp else 0.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(Dimens.mediumRadius)
                    )
                    .clickable {
                        if (multiSelection) {
                            val updatedIndices = selectedIndices.toMutableSet()

                            if (index in updatedIndices) {
                                updatedIndices.remove(index)
                            } else {
                                updatedIndices.add(index)
                            }

                            onSelectedIndicesChange(updatedIndices)
                        } else {
                            onSelectedIndexChange(index)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.smallSpace)
                ) {
                    option.icon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(Dimens.standardIconSize),
                            tint = contentColor
                        )
                    }

                    TText(
                        text = option.title,
                        color = contentColor,
                        style = TTextStyle.BODY
                    )
                }
            }
        }
    }
}