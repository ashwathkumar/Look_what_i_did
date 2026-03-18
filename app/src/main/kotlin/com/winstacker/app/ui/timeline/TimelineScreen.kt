package com.winstacker.app.ui.timeline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.winstacker.app.data.model.Category
import com.winstacker.app.data.model.CategoryCount
import com.winstacker.app.ui.common.CategoryChip
import com.winstacker.app.ui.common.WinCard
import com.winstacker.app.ui.common.parseColor
import com.winstacker.app.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    initialTimeframe: Timeframe = Timeframe.WEEK,
    initialCategoryFilter: String = "all",
    onEditWin: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStats: () -> Unit,
    viewModel: TimelineViewModel = hiltViewModel()
) {
    val timeframe by viewModel.timeframe.collectAsStateWithLifecycle()
    val categoryFilter by viewModel.categoryFilter.collectAsStateWithLifecycle()
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val categoryCounts by viewModel.categoryCounts.collectAsStateWithLifecycle()
    val wins by viewModel.wins.collectAsStateWithLifecycle()

    // Apply initial values from deep links
    LaunchedEffect(initialTimeframe, initialCategoryFilter) {
        viewModel.setTimeframe(initialTimeframe)
        when {
            initialCategoryFilter == "other" -> viewModel.setCategoryFilter(CategoryFilter.OtherCategories)
            initialCategoryFilter != "all" -> {
                val id = initialCategoryFilter.toLongOrNull()
                if (id != null) viewModel.setCategoryFilter(CategoryFilter.ByCategory(id))
            }
        }
    }

    val categoryMap = allCategories.associateBy { it.id }
    val showingCategorySummary = categoryFilter is CategoryFilter.All

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timeline") },
                actions = {
                    IconButton(onClick = onNavigateToStats) {
                        Icon(Icons.Outlined.EmojiEvents, contentDescription = "Stats")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Timeframe tabs
            TimeframeTabs(
                selected = timeframe,
                onSelect = viewModel::setTimeframe
            )

            HorizontalDivider()

            if (showingCategorySummary) {
                // Category summary view
                CategorySummaryContent(
                    categoryCounts = categoryCounts,
                    categoryMap = categoryMap,
                    onCategoryClick = { catId ->
                        if (catId == null) {
                            viewModel.setCategoryFilter(CategoryFilter.OtherCategories)
                        } else {
                            viewModel.setCategoryFilter(CategoryFilter.ByCategory(catId))
                        }
                    },
                    onShowAll = {
                        // Already showing all, but can add a "see list" action
                        viewModel.setCategoryFilter(CategoryFilter.All)
                    }
                )
            } else {
                // Drill-down wins list
                WinsListContent(
                    categoryFilter = categoryFilter,
                    categoryMap = categoryMap,
                    wins = wins,
                    onBack = { viewModel.setCategoryFilter(CategoryFilter.All) },
                    onEditWin = onEditWin,
                    onDeleteWin = { viewModel.deleteWin(it) }
                )
            }
        }
    }
}

@Composable
private fun TimeframeTabs(
    selected: Timeframe,
    onSelect: (Timeframe) -> Unit
) {
    val tabs = listOf(
        Timeframe.TODAY to "Today",
        Timeframe.WEEK to "Week",
        Timeframe.MONTH to "Month",
        Timeframe.YEAR to "Year",
        Timeframe.ALL_TIME to "All"
    )

    ScrollableTabRow(
        selectedTabIndex = tabs.indexOfFirst { it.first == selected }.coerceAtLeast(0),
        edgePadding = 16.dp
    ) {
        tabs.forEach { (tf, label) ->
            Tab(
                selected = selected == tf,
                onClick = { onSelect(tf) },
                text = { Text(label) }
            )
        }
    }
}

@Composable
private fun CategorySummaryContent(
    categoryCounts: List<CategoryCount>,
    categoryMap: Map<Long, Category>,
    onCategoryClick: (Long?) -> Unit,
    onShowAll: () -> Unit
) {
    if (categoryCounts.isEmpty()) {
        EmptyTimelineState()
        return
    }

    val totalWins = categoryCounts.sumOf { it.count }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "$totalWins ${if (totalWins == 1) "win" else "wins"}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Tap a category to see details",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Preset category rows
        items(categoryCounts) { catCount ->
            val category = catCount.categoryId?.let { categoryMap[it] }
            CategorySummaryRow(
                category = category,
                count = catCount.count,
                isOther = catCount.categoryId != null && !Category.PRESET_IDS.contains(catCount.categoryId) && category == null,
                onClick = { onCategoryClick(catCount.categoryId) }
            )
        }

        item {
            // "All" option
            OutlinedButton(
                onClick = onShowAll,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View all wins chronologically")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategorySummaryRow(
    category: Category?,
    count: Int,
    isOther: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (category != null) {
                CategoryChip(category = category, count = count)
            } else {
                // Uncategorised or other
                val label = if (isOther) "🗂️ Other" else "📎 Uncategorised"
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(
                        text = "$label ($count)",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun WinsListContent(
    categoryFilter: CategoryFilter,
    categoryMap: Map<Long, Category>,
    wins: List<com.winstacker.app.data.model.WinWithCategory>,
    onBack: () -> Unit,
    onEditWin: (Long) -> Unit,
    onDeleteWin: (Long) -> Unit
) {
    Column {
        // Filter header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← All categories")
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = "${wins.size} wins",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (wins.isEmpty()) {
            EmptyTimelineState()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(wins, key = { it.win.id }) { winWithCat ->
                    WinCard(
                        winWithCategory = winWithCat,
                        onClick = { onEditWin(winWithCat.win.id) },
                        onDelete = { onDeleteWin(winWithCat.win.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyTimelineState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "📭", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "No wins in this period",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Start logging your achievements!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
