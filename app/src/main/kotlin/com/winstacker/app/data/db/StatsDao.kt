package com.winstacker.app.data.db

import androidx.room.*
import com.winstacker.app.data.model.AggregatedStats
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stats: AggregatedStats)

    @Query("SELECT * FROM aggregated_stats WHERE yearMonth LIKE :yearPrefix || '%'")
    suspend fun getStatsForYear(yearPrefix: String): List<AggregatedStats>

    /** Returns the stored winCount for the monthly total row (key = "YYYY-MM", categoryId IS NULL). */
    @Query("SELECT winCount FROM aggregated_stats WHERE yearMonth = :yearMonth AND categoryId IS NULL LIMIT 1")
    suspend fun getTotalForMonth(yearMonth: String): Int?

    /** Returns the stored winCount for any aggregated_stats row by its primary key. */
    @Query("SELECT winCount FROM aggregated_stats WHERE yearMonth = :key LIMIT 1")
    suspend fun getCountForKey(key: String): Int?

    @Query("SELECT * FROM aggregated_stats ORDER BY yearMonth ASC")
    fun getAllStats(): Flow<List<AggregatedStats>>
}
