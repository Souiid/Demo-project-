package com.souiid.demoproject.ui.utils

import androidx.compose.runtime.Composable
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.postDaysAgo
import demoproject.shared.generated.resources.postHoursAgo
import demoproject.shared.generated.resources.postJustNow
import demoproject.shared.generated.resources.postMinutesAgo
import demoproject.shared.generated.resources.postYesterday
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.Instant

object PostDateFormatter {

    @Composable
    fun format(createdAt: Long): String {
        val now = Clock.System.now()
        val date = Instant.fromEpochMilliseconds(createdAt)
        val timeZone = TimeZone.currentSystemDefault()

        val nowLocal = now.toLocalDateTime(timeZone)
        val postLocal = date.toLocalDateTime(timeZone)
        val minutes = (now.toEpochMilliseconds() - createdAt).coerceAtLeast(0L) / 60_000L

        return when (val days = nowLocal.date.toEpochDays() - postLocal.date.toEpochDays()) {
            0L if minutes < 1L -> stringResource(Res.string.postJustNow)
            0L if minutes < 60L -> stringResource(Res.string.postMinutesAgo, minutes.toInt())
            0L if minutes < 180L -> stringResource(Res.string.postHoursAgo, (minutes / 60).toInt())
            0L -> "${postLocal.hour.pad()}:${postLocal.minute.pad()}"
            1L -> stringResource(Res.string.postYesterday)
            in 2L..6L -> stringResource(Res.string.postDaysAgo, days)
            else -> "${postLocal.day.pad()}/${postLocal.monthNumber.pad()}/${postLocal.year}"
        }
    }

    private fun Int.pad(): String = toString().padStart(2, '0')
}