package com.winstacker.app.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.layout.*
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.winstacker.app.MainActivity
import com.winstacker.app.data.model.Category
import com.winstacker.app.data.model.CategoryCount
import com.winstacker.app.data.repository.CategoryRepository
import com.winstacker.app.data.repository.WinRepository
import com.winstacker.app.viewmodel.Timeframe
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import androidx.compose.ui.graphics.Color

private val KEY_TIMEFRAME = stringPreferencesKey("timeframe")
private val KEY_SHOW_MENU = booleanPreferencesKey("show_menu")

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WinsWidgetEntryPoint {
    fun winRepository(): WinRepository
    fun categoryRepository(): CategoryRepository
}

class WinsWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WinsWidgetEntryPoint::class.java
        )
        val winRepo = entryPoint.winRepository()
        val catRepo = entryPoint.categoryRepository()

        val allCategories = catRepo.getAllCategories().first()
        val categoryMap = allCategories.associateBy { it.id }

        provideContent {
            // Read Glance state reactively — re-renders automatically when state changes
            val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
            val timeframeStr = prefs[KEY_TIMEFRAME] ?: Timeframe.WEEK.name
            val showMenu = prefs[KEY_SHOW_MENU] ?: false
            val timeframe = runCatching { Timeframe.valueOf(timeframeStr) }
                .getOrDefault(Timeframe.WEEK)

            // DB data fetched once per provideGlance; updated via WinsWidget().update()
            val today = LocalDate.now()
            val (start, end) = computeDateRange(timeframe, today)
            val categoryCounts = winRepo.getCategoryCountsInRange(start, end).first()

            WinsWidgetContent(
                categoryCounts = categoryCounts,
                categoryMap = categoryMap,
                timeframe = timeframe,
                showMenu = showMenu
            )
        }
    }
}

@GlanceComposable
@androidx.compose.runtime.Composable
private fun WinsWidgetContent(
    categoryCounts: List<CategoryCount>,
    categoryMap: Map<Long, Category>,
    timeframe: Timeframe,
    showMenu: Boolean
) {
    val themeGreen = ColorProvider(Color(0xFF3E8752))
    val dividerColor = ColorProvider(Color(0x1F000000))

    val timeframeLabel = when (timeframe) {
        Timeframe.TODAY -> "Today"
        Timeframe.WEEK -> "This week"
        Timeframe.MONTH -> "This month"
        Timeframe.YEAR -> "This year"
        Timeframe.ALL_TIME -> "All time"
    }

    GlanceTheme {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(12.dp)
                .cornerRadius(16.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Vertical.Top
            ) {
                // ── Header row ────────────────────────────────────────────
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    // Timeframe label — tapping toggles the dropdown
                    Row(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .clickable(actionRunCallback<ToggleMenuAction>()),
                        verticalAlignment = Alignment.Vertical.CenterVertically
                    ) {
                        Text(
                            text = timeframeLabel,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeGreen
                            )
                        )
                        Spacer(GlanceModifier.width(4.dp))
                        Text(
                            text = if (showMenu) "▴" else "▾",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeGreen
                            )
                        )
                    }

                    // Quick-add button — opens Add Win form directly
                    val addIntent = Intent(LocalContext.current, MainActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse("winstacker://add")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    Box(
                        modifier = GlanceModifier
                            .background(themeGreen)
                            .cornerRadius(20.dp)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .clickable(actionStartActivity(addIntent)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorProvider(Color.White)
                            )
                        )
                    }
                }

                // ── Separator ─────────────────────────────────────────────
                Spacer(GlanceModifier.height(8.dp))
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(dividerColor)
                )
                Spacer(GlanceModifier.height(8.dp))

                // ── Body: dropdown menu or category list ───────────────────
                if (showMenu) {
                    TimeframeMenu()
                } else {
                    CategoryList(
                        categoryCounts = categoryCounts,
                        categoryMap = categoryMap,
                        timeframe = timeframe,
                        emptyLabel = "No wins yet ${timeframeLabel.lowercase()} — go get 'em! 🏆"
                    )
                }
            }
        }
    }
}

@GlanceComposable
@androidx.compose.runtime.Composable
private fun TimeframeMenu() {
    val options = listOf(
        Timeframe.TODAY to "Today",
        Timeframe.WEEK to "This week",
        Timeframe.MONTH to "This month",
        Timeframe.YEAR to "This year",
        Timeframe.ALL_TIME to "All time"
    )
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(GlanceTheme.colors.surfaceVariant)
            .cornerRadius(8.dp)
    ) {
        options.forEach { (tf, label) ->
            Text(
                text = label,
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clickable(
                        actionRunCallback<ChangeTimeframeAction>(
                            actionParametersOf(
                                ActionParameters.Key<String>("timeframe") to tf.name
                            )
                        )
                    ),
                style = TextStyle(
                    fontSize = 13.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
        }
    }
}

@GlanceComposable
@androidx.compose.runtime.Composable
private fun CategoryList(
    categoryCounts: List<CategoryCount>,
    categoryMap: Map<Long, Category>,
    timeframe: Timeframe,
    emptyLabel: String
) {
    if (categoryCounts.isEmpty()) {
        Text(
            text = emptyLabel,
            style = TextStyle(
                fontSize = 12.sp,
                color = GlanceTheme.colors.onSurfaceVariant
            )
        )
        return
    }

    val presetCounts = categoryCounts.filter {
        it.categoryId != null && Category.PRESET_IDS.contains(it.categoryId)
    }
    val otherCount = categoryCounts
        .filter { it.categoryId == null || !Category.PRESET_IDS.contains(it.categoryId) }
        .sumOf { it.count }

    Column(modifier = GlanceModifier.fillMaxWidth()) {
        presetCounts.forEach { catCount ->
            val category = catCount.categoryId?.let { categoryMap[it] }
            if (category != null) {
                WidgetCategoryRow(
                    emoji = category.emoji,
                    name = category.name,
                    count = catCount.count,
                    deepLinkParam = catCount.categoryId.toString(),
                    timeframe = timeframe.name
                )
            }
        }
        if (otherCount > 0) {
            WidgetCategoryRow(
                emoji = "🗂️",
                name = "Other",
                count = otherCount,
                deepLinkParam = "other",
                timeframe = timeframe.name
            )
        }
    }
}

@GlanceComposable
@androidx.compose.runtime.Composable
private fun WidgetCategoryRow(
    emoji: String,
    name: String,
    count: Int,
    deepLinkParam: String,
    timeframe: String
) {
    val intent = Intent(LocalContext.current, MainActivity::class.java).apply {
        action = Intent.ACTION_VIEW
        data = Uri.parse("winstacker://timeline?timeframe=$timeframe&categoryFilter=$deepLinkParam")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable(actionStartActivity(intent)),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Text(
            text = "$emoji $name",
            style = TextStyle(
                fontSize = 12.sp,
                color = GlanceTheme.colors.onSurface
            ),
            modifier = GlanceModifier.defaultWeight()
        )
        Text(
            text = "$count",
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.primary
            )
        )
    }
}

// ── Action Callbacks ──────────────────────────────────────────────────────────

class ToggleMenuAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                val current = this[KEY_SHOW_MENU] ?: false
                this[KEY_SHOW_MENU] = !current
            }
        }
        // State change triggers automatic re-render via currentState; also call update
        // to ensure the widget refreshes even if composition was not active.
        WinsWidget().update(context, glanceId)
    }
}

class ChangeTimeframeAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val newTimeframe = parameters[ActionParameters.Key<String>("timeframe")] ?: Timeframe.WEEK.name
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[KEY_TIMEFRAME] = newTimeframe
                this[KEY_SHOW_MENU] = false
            }
        }
        // Must call update to re-fetch DB data for the new timeframe
        WinsWidget().update(context, glanceId)
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun computeDateRange(tf: Timeframe, today: LocalDate): Pair<Long, Long> = when (tf) {
    Timeframe.TODAY -> today.toEpochDay().let { it to it }
    Timeframe.WEEK -> {
        val mon = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sun = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        mon.toEpochDay() to sun.toEpochDay()
    }
    Timeframe.MONTH -> {
        val start = today.withDayOfMonth(1)
        val end = today.with(TemporalAdjusters.lastDayOfMonth())
        start.toEpochDay() to end.toEpochDay()
    }
    Timeframe.YEAR -> {
        val start = today.withDayOfYear(1)
        val end = today.with(TemporalAdjusters.lastDayOfYear())
        start.toEpochDay() to end.toEpochDay()
    }
    Timeframe.ALL_TIME -> 0L to Long.MAX_VALUE / 2
}
