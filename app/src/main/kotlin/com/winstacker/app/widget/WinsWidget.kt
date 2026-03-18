package com.winstacker.app.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import android.net.Uri
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.*
import androidx.glance.material3.ColorProviders
import androidx.glance.unit.ColorProvider
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.winstacker.app.MainActivity
import com.winstacker.app.data.model.Category
import com.winstacker.app.data.repository.WinRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WinsWidgetEntryPoint {
    fun winRepository(): WinRepository
    fun categoryRepository(): com.winstacker.app.data.repository.CategoryRepository
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

        val prefs = getAppWidgetState(context, PreferencesGlanceStateDefinition, id)
        val timeframeStr = prefs[stringPreferencesKey("timeframe")] ?: "WEEK"
        val showMenu = prefs[booleanPreferencesKey("show_menu")] ?: false
        val timeframe = runCatching { com.winstacker.app.viewmodel.Timeframe.valueOf(timeframeStr) }
            .getOrDefault(com.winstacker.app.viewmodel.Timeframe.WEEK)

        val today = LocalDate.now()
        val (start, end) = computeDateRange(timeframe, today)

        val categoryCounts = winRepo.getCategoryCountsInRange(start, end).first()
        val allCategories = catRepo.getAllCategories().first()
        val categoryMap = allCategories.associateBy { it.id }

        provideContent {
            WinsWidgetContent(
                categoryCounts = categoryCounts,
                categoryMap = categoryMap,
                timeframe = timeframe,
                showMenu = showMenu
            )
        }
    }
}

@Composable
private fun WinsWidgetContent(
    categoryCounts: List<com.winstacker.app.data.model.CategoryCount>,
    categoryMap: Map<Long, Category>,
    timeframe: com.winstacker.app.viewmodel.Timeframe,
    showMenu: Boolean
) {
    val themeGreen = ColorProvider(Color(0xFF3E8752))
    val timeframeLabel = when(timeframe) {
        com.winstacker.app.viewmodel.Timeframe.TODAY -> "Today"
        com.winstacker.app.viewmodel.Timeframe.WEEK -> "This week"
        com.winstacker.app.viewmodel.Timeframe.MONTH -> "This month"
        com.winstacker.app.viewmodel.Timeframe.YEAR -> "This year"
        com.winstacker.app.viewmodel.Timeframe.ALL_TIME -> "All"
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
                // Header row
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Row(
                        modifier = GlanceModifier.defaultWeight().clickable(actionRunCallback<ToggleMenuAction>()),
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
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeGreen
                            )
                        )
                    }

                    // Quick-add button
                    Box(
                        modifier = GlanceModifier
                            .background(themeGreen)
                            .cornerRadius(20.dp)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .clickable(
                                actionStartActivity<MainActivity>(
                                    actionParametersOf(
                                        ActionParameters.Key<String>("deeplink") to "winstacker://add"
                                    )
                                )
                            ),
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

                if (showMenu) {
                    Column(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(GlanceTheme.colors.surfaceVariant)
                            .cornerRadius(8.dp)
                    ) {
                        listOf(
                            com.winstacker.app.viewmodel.Timeframe.TODAY to "Today",
                            com.winstacker.app.viewmodel.Timeframe.WEEK to "This week",
                            com.winstacker.app.viewmodel.Timeframe.YEAR to "This year",
                            com.winstacker.app.viewmodel.Timeframe.ALL_TIME to "All time"
                        ).forEach { (tf, label) ->
                            Text(
                                text = label,
                                modifier = GlanceModifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable(
                                        actionRunCallback<ChangeTimeframeAction>(
                                            actionParametersOf(ActionParameters.Key<String>("timeframe") to tf.name)
                                        )
                                    ),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = GlanceTheme.colors.onSurfaceVariant
                                )
                            )
                        }
                    }
                } else {
                    Spacer(GlanceModifier.height(8.dp))

                    if (categoryCounts.isEmpty()) {
                        Text(
                            text = "No wins yet ${timeframeLabel.lowercase()} — go get 'em! \uD83C\uDFC6",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = GlanceTheme.colors.onSurfaceVariant
                            )
                        )
                    } else {
                    // Group preset vs other
                    val presetCounts = categoryCounts.filter {
                        it.categoryId != null && Category.PRESET_IDS.contains(it.categoryId)
                    }
                    val otherCount = categoryCounts
                        .filter { it.categoryId == null || !Category.PRESET_IDS.contains(it.categoryId) }
                        .sumOf { it.count }

                    Column(
                        modifier = GlanceModifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Vertical.Top
                    ) {
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
                }
            }
        }
    }
}

@Composable
private fun WidgetCategoryRow(
    emoji: String,
    name: String,
    count: Int,
    deepLinkParam: String,
    timeframe: String
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable(
                actionStartActivity<MainActivity>(
                    actionParametersOf(
                        ActionParameters.Key<String>("deeplink") to "winstacker://timeline?timeframe=$timeframe&categoryFilter=$deepLinkParam"
                    )
                )
            ),
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

class ChangeTimeframeAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val newTimeframe = parameters[ActionParameters.Key<String>("timeframe")] ?: "WEEK"
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[stringPreferencesKey("timeframe")] = newTimeframe
                this[booleanPreferencesKey("show_menu")] = false
            }
        }
        WinsWidget().update(context, glanceId)
    }
}

class ToggleMenuAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                val current = this[booleanPreferencesKey("show_menu")] ?: false
                this[booleanPreferencesKey("show_menu")] = !current
            }
        }
        WinsWidget().update(context, glanceId)
    }
}

private fun computeDateRange(tf: com.winstacker.app.viewmodel.Timeframe, today: LocalDate): Pair<Long, Long> {
    return when (tf) {
        com.winstacker.app.viewmodel.Timeframe.TODAY -> {
            val d = today.toEpochDay()
            d to d
        }
        com.winstacker.app.viewmodel.Timeframe.WEEK -> {
            val mon = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val sun = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            mon.toEpochDay() to sun.toEpochDay()
        }
        com.winstacker.app.viewmodel.Timeframe.MONTH -> {
            val start = today.withDayOfMonth(1)
            val end = today.with(TemporalAdjusters.lastDayOfMonth())
            start.toEpochDay() to end.toEpochDay()
        }
        com.winstacker.app.viewmodel.Timeframe.YEAR -> {
            val start = today.withDayOfYear(1)
            val end = today.with(TemporalAdjusters.lastDayOfYear())
            start.toEpochDay() to end.toEpochDay()
        }
        com.winstacker.app.viewmodel.Timeframe.ALL_TIME -> {
            0L to Long.MAX_VALUE / 2
        }
    }
}
