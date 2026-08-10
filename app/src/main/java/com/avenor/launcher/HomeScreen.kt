package com.avenor.launcher

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import java.time.ZonedDateTime
import kotlinx.coroutines.delay

@Composable
internal fun HomeScreen(
    clock: () -> ZonedDateTime = { ZonedDateTime.now() },
) {
    val context = LocalContext.current
    var now by remember { mutableStateOf(clock()) }

    LaunchedEffect(clock) {
        while (true) {
            val remainingMillis = 60_000L - (System.currentTimeMillis() % 60_000L)
            delay(remainingMillis)
            now = clock()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(
                horizontal = dimensionResource(R.dimen.home_horizontal_padding),
                vertical = dimensionResource(R.dimen.home_vertical_padding),
            ),
    ) {
        Text(
            text = HomeDateTimeFormatter.time(context, now),
            modifier = Modifier
                .heightIn(min = dimensionResource(R.dimen.home_time_min_height))
                .testTag("home_time")
                .clickable(role = Role.Button) {
                    context.launchClockDestination()
                },
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = dimensionResource(R.dimen.home_time_text_size).value.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = dimensionResource(R.dimen.home_time_line_height).value.sp,
            textAlign = TextAlign.Start,
        )
        Text(
            text = HomeDateTimeFormatter.dateAndWeekday(context, now),
            modifier = Modifier
                .heightIn(min = dimensionResource(R.dimen.home_date_height))
                .testTag("home_date")
                .clickable(role = Role.Button) {
                    val calendarUri = CalendarContract.CONTENT_URI
                        .buildUpon()
                        .appendPath("time")
                        .appendPath(now.toInstant().toEpochMilli().toString())
                        .build()
                    context.launchPlatformDestination(
                        intent = Intent(Intent.ACTION_VIEW, calendarUri),
                        failureMessage = R.string.calendar_unavailable,
                    )
                }
                .wrapContentHeight(align = Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = dimensionResource(R.dimen.home_date_text_size).value.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = dimensionResource(R.dimen.home_date_line_height).value.sp,
            textAlign = TextAlign.Start,
        )
    }
}

private fun Context.launchClockDestination() {
    val alarmIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
    val clockMainIntent = try {
        packageManager
            .resolveDefaultActivity(alarmIntent)
            ?.activityInfo
            ?.packageName
            ?.let { packageManager.getLaunchIntentForPackage(it) }
    } catch (_: SecurityException) {
        null
    }

    launchPlatformDestination(
        intent = clockMainIntent ?: alarmIntent,
        failureMessage = R.string.clock_unavailable,
    )
}

private fun PackageManager.resolveDefaultActivity(intent: Intent) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        resolveActivity(
            intent,
            PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
        )
    } else {
        @Suppress("DEPRECATION")
        resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    }

private fun Context.launchPlatformDestination(
    intent: Intent,
    @StringRes failureMessage: Int,
) {
    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show()
    } catch (_: SecurityException) {
        Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show()
    }
}
