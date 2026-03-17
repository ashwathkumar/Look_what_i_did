package com.winstacker.app.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.winstacker.app.data.model.WinWithCategory
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WinCard(
    winWithCategory: WinWithCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val win = winWithCategory.win
    val category = winWithCategory.category

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category colour indicator
            if (category != null) {
                Surface(
                    modifier = Modifier
                        .width(4.dp)
                        .height(40.dp),
                    shape = MaterialTheme.shapes.small,
                    color = parseColor(category.colorHex)
                ) {}
                Spacer(Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = win.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (win.notes.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = win.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
                if (category != null) {
                    Spacer(Modifier.height(6.dp))
                    CategoryChip(category = category)
                }
            }

            Spacer(Modifier.width(8.dp))
            Text(
                text = formatEpochDay(win.dateEpochDay),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatEpochDay(epochDay: Long): String {
    val date = LocalDate.ofEpochDay(epochDay)
    val today = LocalDate.now()
    return when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> "${date.dayOfMonth} ${date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }}"
    }
}
