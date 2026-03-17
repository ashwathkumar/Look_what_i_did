package com.winstacker.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val isPreset: Boolean = false,
    val sortOrder: Int = Int.MAX_VALUE
) {
    companion object {
        val PRESET_CATEGORIES = listOf(
            Category(id = 1, name = "Work", emoji = "💼", colorHex = "#4A90D9", isPreset = true, sortOrder = 0),
            Category(id = 2, name = "Health & Fitness", emoji = "🏃", colorHex = "#5DAB6A", isPreset = true, sortOrder = 1),
            Category(id = 3, name = "Learning", emoji = "📚", colorHex = "#9B6FD4", isPreset = true, sortOrder = 2),
            Category(id = 4, name = "Personal", emoji = "🙌", colorHex = "#E8864A", isPreset = true, sortOrder = 3),
            Category(id = 5, name = "Home", emoji = "🏠", colorHex = "#D45E5E", isPreset = true, sortOrder = 4),
        )

        val PRESET_IDS = PRESET_CATEGORIES.map { it.id }.toSet()
    }
}
