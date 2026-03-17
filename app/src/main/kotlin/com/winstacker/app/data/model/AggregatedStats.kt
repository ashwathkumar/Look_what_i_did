package com.winstacker.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores rolled-up monthly counts so stats survive the 2-year raw-entry pruning window.
 */
@Entity(tableName = "aggregated_stats")
data class AggregatedStats(
    @PrimaryKey
    /** Format: "YYYY-MM" */
    val yearMonth: String,
    val categoryId: Long?,
    val winCount: Int
)
