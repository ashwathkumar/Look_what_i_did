package com.winstacker.app.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.winstacker.app.MainActivity
import com.winstacker.app.data.repository.WinRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WinsWidgetEntryPoint {
    fun winRepository(): WinRepository
}

class WinsWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val today = LocalDate.now()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        val weekDays = (0..6).map { monday.plusDays(it.toLong()) }

        // Count total wins per day epoch. Falls back to all-zero on any error.
        val countByDay: Map<Long, Int> = runCatching {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                WinsWidgetEntryPoint::class.java
            )
            val wins = entryPoint.winRepository()
                .getWinsInRange(monday.toEpochDay(), sunday.toEpochDay())
                .first()
            wins.groupingBy { it.win.dateEpochDay }.eachCount()
        }.getOrElse { emptyMap() }

        val dayCounts = weekDays.map { countByDay[it.toEpochDay()] ?: 0 }
        val todayIndex = weekDays.indexOfFirst { it == today }.takeIf { it >= 0 }

        provideContent {
            WinsWidgetContent(
                dayCounts = dayCounts,
                todayIndex = todayIndex
            )
        }
    }
}

// ── Day labels Mon→Sun ────────────────────────────────────────────────────────

private val DAY_LETTERS = listOf("M", "T", "W", "T", "F", "S", "S")

// ── Widget UI ─────────────────────────────────────────────────────────────────

@GlanceComposable
@androidx.compose.runtime.Composable
private fun WinsWidgetContent(
    dayCounts: List<Int>,
    todayIndex: Int?
) {
    val green = ColorProvider(Color(0xFF3E8752))
    val openWeek = actionRunCallback<OpenWeeklyTimelineAction>()

    GlanceTheme {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .cornerRadius(16.dp)
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .clickable(openWeek),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                // ── Day-letter header ─────────────────────────────────────
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    // Blank spacer aligned with "🏆 Wins" label below
                    Spacer(GlanceModifier.width(76.dp))

                    DAY_LETTERS.forEachIndexed { i, letter ->
                        val isToday = i == todayIndex
                        Text(
                            text = letter,
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isToday) green else GlanceTheme.colors.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            ),
                            modifier = GlanceModifier.defaultWeight()
                        )
                    }
                }

                Spacer(GlanceModifier.height(6.dp))

                // ── Wins count row ────────────────────────────────────────
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    // Category label
                    Text(
                        text = "🏆 Wins",
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlanceTheme.colors.onSurface
                        ),
                        modifier = GlanceModifier.width(76.dp)
                    )

                    dayCounts.forEachIndexed { i, count ->
                        val isToday = i == todayIndex
                        Text(
                            text = if (count == 0) "–" else count.toString(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = if (count > 0) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    count > 0 && isToday -> green
                                    count > 0 -> GlanceTheme.colors.onSurface
                                    else -> GlanceTheme.colors.onSurfaceVariant
                                },
                                textAlign = TextAlign.Center
                            ),
                            modifier = GlanceModifier.defaultWeight()
                        )
                    }
                }
            }
        }
    }
}

// ── Action: open This Week timeline ──────────────────────────────────────────

class OpenWeeklyTimelineAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: androidx.glance.action.ActionParameters
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("winstacker://timeline?timeframe=WEEK&categoryFilter=all")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(intent)
    }
}
