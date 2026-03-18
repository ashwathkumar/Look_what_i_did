package com.winstacker.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.winstacker.app.ui.common.WinCard
import com.winstacker.app.viewmodel.HomeViewModel
import com.winstacker.app.viewmodel.Timeframe
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTimeline: (Timeframe) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStats: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val wins by viewModel.todayWins.collectAsStateWithLifecycle()
    val categoryCounts by viewModel.todayCategoryCounts.collectAsStateWithLifecycle()
    val winCount by viewModel.todayWinCount.collectAsStateWithLifecycle()
    val showCelebration by viewModel.showCelebration.collectAsStateWithLifecycle()

    if (showCelebration) {
        CelebrationDialog(onDismiss = viewModel::dismissCelebration)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = formattedDate(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Win count summary header
            item {
                WinCountHeader(count = winCount)
            }

            // Category summary chips
            if (categoryCounts.isNotEmpty()) {
                item {
                    CategorySummaryRow(
                        categoryCounts = categoryCounts,
                        onCategoryClick = { /* Could drill down */ }
                    )
                }
            }

            // Individual wins list
            if (wins.isEmpty()) {
                item {
                    EmptyTodayState()
                }
            } else {
                items(wins, key = { it.win.id }) { winWithCat ->
                    WinCard(
                        winWithCategory = winWithCat,
                        onClick = { /* Navigate to edit if needed from home, or keep disabled */ },
                        onDelete = { viewModel.deleteWin(winWithCat.win.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WinCountHeader(count: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = "$count ${if (count == 1) "win" else "wins"} today",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (count == 0) {
                    Text(
                        text = "Log your first win of the day!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                } else {
                    Text(
                        text = "Keep it up! 🎉",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategorySummaryRow(
    categoryCounts: List<com.winstacker.app.data.model.CategoryCount>,
    onCategoryClick: (Long?) -> Unit
) {
    Text(
        text = "By category",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    // Just display count chips (categories resolved in parent)
    // This is simplified — in real use, we'd look up category details
}

@Composable
private fun EmptyTodayState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "🏆", style = MaterialTheme.typography.displayMedium)
        Text(
            text = "No wins logged yet today",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "What have you accomplished so far?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            text = "Log a win from the + button below!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun CelebrationDialog(onDismiss: () -> Unit) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2500)
        onDismiss()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "🐶👍", style = MaterialTheme.typography.displayMedium)
                Text(
                    text = "3 wins today!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "You're crushing it! The dog approves.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                TextButton(onClick = onDismiss) {
                    Text("Thanks! 🎉")
                }
            }
        }
    }
}

private fun formattedDate(): String {
    val today = LocalDate.now()
    val dayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val month = today.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    return "$dayOfWeek, ${today.dayOfMonth} $month ${today.year}"
}
