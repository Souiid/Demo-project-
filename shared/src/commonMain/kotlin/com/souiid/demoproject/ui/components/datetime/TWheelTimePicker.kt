package com.souiid.demoproject.ui.components.datetime

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.LocalTime
import kotlin.math.abs

private const val TIME_VISIBLE_ITEMS = 5

private val TIME_ITEM_HEIGHT = 52.dp

@Composable
fun TWheelTimePicker(
    modifier: Modifier = Modifier,
    selectedTime: LocalTime?,
    minTime: LocalTime = LocalTime(0, 0),
    maxTime: LocalTime = LocalTime(23, 50),
    minuteStep: Int = 10,
    onTimeSelected: (LocalTime) -> Unit,
) {

    val hours = remember(
        minTime,
        maxTime
    ) {
        (minTime.hour..maxTime.hour).toList()
    }

    val minutes = remember(minuteStep) {
        (0 until 60 step minuteStep).toList()
    }

    val initialTime = remember(
        selectedTime,
        minTime,
        maxTime,
        minuteStep
    ) {
        normalizeTime(
            time = selectedTime ?: minTime,
            minTime = minTime,
            maxTime = maxTime,
            minuteStep = minuteStep
        )
    }

    val initialHourIndex = hours
        .indexOf(initialTime.hour)
        .coerceAtLeast(0)

    val initialMinuteIndex = minutes
        .indexOf(initialTime.minute)
        .coerceAtLeast(0)

    val hourListState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialHourIndex
    )

    val minuteListState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialMinuteIndex
    )

    val hourFlingBehavior = rememberSnapFlingBehavior(
        lazyListState = hourListState
    )

    val minuteFlingBehavior = rememberSnapFlingBehavior(
        lazyListState = minuteListState
    )

    val centeredHourIndex by remember {
        derivedStateOf {
            findCenteredItemIndex(
                listState = hourListState,
                fallbackIndex = initialHourIndex,
                lastIndex = hours.lastIndex
            )
        }
    }

    val centeredMinuteIndex by remember {
        derivedStateOf {
            findCenteredItemIndex(
                listState = minuteListState,
                fallbackIndex = initialMinuteIndex,
                lastIndex = minutes.lastIndex
            )
        }
    }

    LaunchedEffect(
        hourListState,
        minuteListState,
        minTime,
        maxTime,
        minuteStep
    ) {

        snapshotFlow {
            hourListState.isScrollInProgress to
                    minuteListState.isScrollInProgress
        }
            .distinctUntilChanged()
            .collect { (hourScrolling, minuteScrolling) ->

                if (
                    !hourScrolling &&
                    !minuteScrolling
                ) {

                    val hour = hours
                        .getOrNull(centeredHourIndex)
                        ?: return@collect

                    val minute = minutes
                        .getOrNull(centeredMinuteIndex)
                        ?: return@collect

                    val time = normalizeTime(
                        time = LocalTime(
                            hour = hour,
                            minute = minute
                        ),
                        minTime = minTime,
                        maxTime = maxTime,
                        minuteStep = minuteStep
                    )

                    if (time != selectedTime) {
                        onTimeSelected(time)
                    }
                }
            }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(
                TIME_ITEM_HEIGHT *
                        TIME_VISIBLE_ITEMS
            ),
        contentAlignment = Alignment.Center
    ) {


        Box(
            modifier = Modifier
                .width(170.dp)
                .height(
                    TIME_ITEM_HEIGHT *
                            TIME_VISIBLE_ITEMS
                )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        TIME_ITEM_HEIGHT *
                                TIME_VISIBLE_ITEMS
                    )
            ) {

                TimeWheel(
                    modifier = Modifier.weight(1f),
                    items = hours,
                    centeredIndex = centeredHourIndex,
                    listState = hourListState,
                    flingBehavior = hourFlingBehavior
                )

                TimeWheel(
                    modifier = Modifier.weight(1f),
                    items = minutes,
                    centeredIndex = centeredMinuteIndex,
                    listState = minuteListState,
                    flingBehavior = minuteFlingBehavior
                )
            }


            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(TIME_ITEM_HEIGHT)
            ) {

                HorizontalDivider(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.45f
                    )
                )

                HorizontalDivider(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.45f
                    )
                )
            }
        }
    }
}

@Composable
private fun TimeWheel(
    items: List<Int>,
    centeredIndex: Int,
    listState: LazyListState,
    flingBehavior: FlingBehavior,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        modifier = modifier,
        contentPadding = PaddingValues(
            vertical =
                TIME_ITEM_HEIGHT *
                        (TIME_VISIBLE_ITEMS / 2)
        )
    ) {

        itemsIndexed(
            items = items
        ) { index, value ->

            val isSelected =
                index == centeredIndex

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TIME_ITEM_HEIGHT),
                contentAlignment = Alignment.Center
            ) {

                TText(
                    text = value
                        .toString()
                        .padStart(
                            length = 2,
                            padChar = '0'
                        ),
                    style = if (isSelected) {
                        TTextStyle.SUBTITLE
                    } else {
                        TTextStyle.BODY
                    },
                    color = if (isSelected) {
                        MaterialTheme
                            .colorScheme
                            .onSurface
                    } else {
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                    },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun findCenteredItemIndex(
    listState: LazyListState,
    fallbackIndex: Int,
    lastIndex: Int
): Int {

    val layoutInfo =
        listState.layoutInfo

    val viewportCenter =
        (
                layoutInfo.viewportStartOffset +
                        layoutInfo.viewportEndOffset
                ) / 2

    return layoutInfo
        .visibleItemsInfo
        .minByOrNull { item ->

            val itemCenter =
                item.offset +
                        item.size / 2

            abs(
                itemCenter -
                        viewportCenter
            )
        }
        ?.index
        ?.coerceIn(
            0,
            lastIndex
        )
        ?: fallbackIndex
}

private fun normalizeTime(
    time: LocalTime,
    minTime: LocalTime,
    maxTime: LocalTime,
    minuteStep: Int
): LocalTime {

    var hour = time.hour

    var minute =
        (time.minute / minuteStep) *
                minuteStep

    if (minute >= 60) {
        hour++
        minute = 0
    }

    var normalized = LocalTime(
        hour = hour.coerceIn(
            minimumValue = 0,
            maximumValue = 23
        ),
        minute = minute
    )

    if (normalized < minTime) {

        val minMinute = roundMinuteUp(
            minute = minTime.minute,
            step = minuteStep
        )

        normalized =
            if (minMinute >= 60) {

                LocalTime(
                    hour = (minTime.hour + 1)
                        .coerceAtMost(23),
                    minute = 0
                )

            } else {

                LocalTime(
                    hour = minTime.hour,
                    minute = minMinute
                )
            }
    }

    if (normalized > maxTime) {

        val maxMinute =
            (maxTime.minute / minuteStep) *
                    minuteStep

        normalized = LocalTime(
            hour = maxTime.hour,
            minute = maxMinute
        )
    }

    return normalized
}

private fun roundMinuteUp(
    minute: Int,
    step: Int
): Int {

    if (minute % step == 0) {
        return minute
    }

    return (
            minute / step + 1
            ) * step
}