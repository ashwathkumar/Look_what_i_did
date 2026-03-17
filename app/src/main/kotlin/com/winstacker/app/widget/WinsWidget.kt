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
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.material3.ColorProviders
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

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WinsWidgetEntryPoint::class.java
        )
        val winRepo = entryPoint.winRepository()
        val catRepo = entryPoint.categoryRepository()

        val today = LocalDate.now()
        val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toEpochDay()
        val weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).toEpochDay()

        val categoryCounts = winRepo.getCategoryCountsInRange(weekStart, weekEnd).first()
        val allCategories = catRepo.getAllCategories().first()
        val categoryMap = allCategories.associateBy { it.id }

        provideContent {
            WinsWidgetContent(
                categoryCounts = categoryCounts,
                categoryMap = categoryMap
            )
        }
    }
}

@Composable
private fun WinsWidgetContent(
    categoryCounts: List<com.winstacker.app.data.model.CategoryCount>,
    categoryMap: Map<Long, Category>
) {
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
                    Text(
                        text = "This Week",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlanceTheme.colors.onSurface
                        ),
                        modifier = GlanceModifier.defaultWeight()
                    )

                    // Quick-add button
                    Box(
                        modifier = GlanceModifier
                            .background(GlanceTheme.colors.primary)
                            .cornerRadius(20.dp)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
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
                            text = "+ Win",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = GlanceTheme.colors.onPrimary
                            )
                        )
                    }
                }

                Spacer(GlanceModifier.height(8.dp))

                if (categoryCounts.isEmpty()) {
                    Text(
                        text = "No wins yet this week — go get 'em! 🏆",
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
                                    deepLinkParam = catCount.categoryId.toString()
                                )
                            }
                        }

                        if (otherCount > 0) {
                            WidgetCategoryRow(
                                emoji = "🗂️",
                                name = "Other",
                                count = otherCount,
                                deepLinkParam = "other"
                            )
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
    deepLinkParam: String
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable(
                actionStartActivity<MainActivity>(
                    actionParametersOf(
                        ActionParameters.Key<String>("deeplink") to "winstacker://weekly?categoryFilter=$deepLinkParam"
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
