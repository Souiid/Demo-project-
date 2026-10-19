package com.souiid.mutiplatformtemplate.ui.components.datetime

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
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
import com.souiid.mutiplatformtemplate.ui.components.text.TText
import com.souiid.mutiplatformtemplate.ui.components.text.TTextStyle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import mutiplatformtemplate.shared.generated.resources.Res
import mutiplatformtemplate.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.time.Clock

private const val VISIBLE_ITEMS = 5
private val ITEM_HEIGHT = 52.dp

@Composable
fun TWheelDatePicker(
    selectedDate: LocalDate?,
    minDate: LocalDate,
    maxDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    require(maxDate >= minDate)

    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val dates = remember(minDate, maxDate) {
        generateDates(minDate = minDate, maxDate = maxDate)
    }

    if (dates.isEmpty()) return

    val initialDate = when {
        selectedDate != null && selectedDate in minDate..maxDate -> selectedDate
        today in minDate..maxDate -> today
        else -> minDate
    }

    val initialIndex = dates.indexOf(initialDate).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val centeredIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2

            layoutInfo.visibleItemsInfo
                .minByOrNull { item ->
                    val itemCenter = item.offset + item.size / 2
                    abs(itemCenter - viewportCenter)
                }
                ?.index
                ?.coerceIn(0, dates.lastIndex)
                ?: initialIndex
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { isScrolling ->
                if (!isScrolling) {
                    dates.getOrNull(centeredIndex)?.let { date ->
                        if (date != selectedDate) {
                            onDateSelected(date)
                        }
                    }
                }
            }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ITEM_HEIGHT * VISIBLE_ITEMS)
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                vertical = ITEM_HEIGHT * (VISIBLE_ITEMS / 2)
            )
        ) {
            itemsIndexed(
                items = dates,
                key = { _, date -> date.toString() }
            ) { index, date ->
                val isSelected = index == centeredIndex

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ITEM_HEIGHT),
                    contentAlignment = Alignment.Center
                ) {
                    TText(
                        text = date.toWheelDateLabel(today),
                        style = if (isSelected) TTextStyle.SUBTITLE else TTextStyle.BODY,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(ITEM_HEIGHT)
        ) {
            HorizontalDivider(
                modifier = Modifier.align(Alignment.TopCenter),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            HorizontalDivider(
                modifier = Modifier.align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

private fun generateDates(
    minDate: LocalDate,
    maxDate: LocalDate
): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    var current = minDate

    while (current <= maxDate) {
        dates += current
        current = current.plus(1, DateTimeUnit.DAY)
    }

    return dates
}

@Composable
private fun LocalDate.toWheelDateLabel(today: LocalDate): String {
    val tomorrow = today.plus(1, DateTimeUnit.DAY)

    return when (this) {
        today -> stringResource(Res.string.today)
        tomorrow -> stringResource(Res.string.tomorrow)
        else -> stringResource(
            Res.string.date_format,
            dayOfWeek.toLocalizedName(),
            day,
            month.toLocalizedName()
        )
    }
}

@Composable
private fun DayOfWeek.toLocalizedName(): String {
    return when (this) {
        DayOfWeek.MONDAY -> stringResource(Res.string.monday)
        DayOfWeek.TUESDAY -> stringResource(Res.string.tuesday)
        DayOfWeek.WEDNESDAY -> stringResource(Res.string.wednesday)
        DayOfWeek.THURSDAY -> stringResource(Res.string.thursday)
        DayOfWeek.FRIDAY -> stringResource(Res.string.friday)
        DayOfWeek.SATURDAY -> stringResource(Res.string.saturday)
        DayOfWeek.SUNDAY -> stringResource(Res.string.sunday)
    }
}

@Composable
private fun Month.toLocalizedName(): String {
    return when (this) {
        Month.JANUARY -> stringResource(Res.string.january)
        Month.FEBRUARY -> stringResource(Res.string.february)
        Month.MARCH -> stringResource(Res.string.march)
        Month.APRIL -> stringResource(Res.string.april)
        Month.MAY -> stringResource(Res.string.may)
        Month.JUNE -> stringResource(Res.string.june)
        Month.JULY -> stringResource(Res.string.july)
        Month.AUGUST -> stringResource(Res.string.august)
        Month.SEPTEMBER -> stringResource(Res.string.september)
        Month.OCTOBER -> stringResource(Res.string.october)
        Month.NOVEMBER -> stringResource(Res.string.november)
        Month.DECEMBER -> stringResource(Res.string.december)
    }
}