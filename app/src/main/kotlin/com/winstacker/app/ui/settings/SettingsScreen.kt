package com.winstacker.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.winstacker.app.data.repository.AppAccent
import com.winstacker.app.data.repository.AppTheme
import com.winstacker.app.ui.common.SlimTopBar
import com.winstacker.app.ui.theme.AccentPalette
import com.winstacker.app.ui.theme.accentPalettes
import com.winstacker.app.ui.theme.dynamicAccentPalette
import com.winstacker.app.ui.theme.supportsDynamicColor
import com.winstacker.app.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToCategories: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentTheme by viewModel.appTheme.collectAsStateWithLifecycle()
    val currentAccent by viewModel.appAccent.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            SlimTopBar(
                title = "Settings",
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SectionLabel(text = "Appearance", topPadding = 4.dp)
            }

            item {
                ThemeSection(
                    currentTheme = currentTheme,
                    onThemeSelected = viewModel::setTheme
                )
            }

            item {
                SectionLabel(text = "Colours")
            }

            item {
                AccentSection(
                    currentAccent = currentAccent,
                    onAccentSelected = viewModel::setAccent
                )
            }

            item {
                SectionLabel(text = "Data")
            }

            item {
                SettingsRow(
                    icon = Icons.Default.Category,
                    title = "Manage Categories",
                    subtitle = "Add, rename, or delete custom categories",
                    onClick = onNavigateToCategories
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Raw win entries are stored for 2 years. Aggregated stats (monthly counts) are kept indefinitely.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, topPadding: androidx.compose.ui.unit.Dp = 12.dp) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = topPadding, bottom = 4.dp)
    )
}

@Composable
private fun ThemeSection(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            ThemeOption(
                icon = Icons.Default.SettingsBrightness,
                label = "Follow system",
                selected = currentTheme == AppTheme.SYSTEM,
                onClick = { onThemeSelected(AppTheme.SYSTEM) }
            )
            ThemeOption(
                icon = Icons.Default.LightMode,
                label = "Light",
                selected = currentTheme == AppTheme.LIGHT,
                onClick = { onThemeSelected(AppTheme.LIGHT) }
            )
            ThemeOption(
                icon = Icons.Default.DarkMode,
                label = "Dark",
                selected = currentTheme == AppTheme.DARK,
                onClick = { onThemeSelected(AppTheme.DARK) }
            )
        }
    }
}

@Composable
private fun ThemeOption(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        RadioButton(selected = selected, onClick = onClick)
    }
}

@Composable
private fun AccentSection(
    currentAccent: AppAccent,
    onAccentSelected: (AppAccent) -> Unit
) {
    val palettes = remember {
        if (supportsDynamicColor) accentPalettes + dynamicAccentPalette else accentPalettes
    }
    val selectedPalette = palettes.firstOrNull { it.accent == currentAccent }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                palettes.forEach { palette ->
                    AccentSwatch(
                        palette = palette,
                        selected = palette.accent == currentAccent,
                        onClick = { onAccentSelected(palette.accent) }
                    )
                }
            }
            Text(
                text = selectedPalette?.label ?: "Green",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun AccentSwatch(
    palette: AccentPalette,
    selected: Boolean,
    onClick: () -> Unit
) {
    // Keep the whole 48dp square tappable while the circle stays 40dp.
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClickLabel = palette.label, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(palette.swatch)
                .then(
                    if (selected) {
                        Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            val markColor = if (palette.swatch.luminance() > 0.5f) Color.Black else Color.White
            when {
                selected -> Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = markColor,
                    modifier = Modifier.size(20.dp)
                )
                palette.accent == AppAccent.DYNAMIC -> Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = markColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
