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

    @Query("SELECT SUM(winCount) FROM aggregated_stats WHERE yearMonth = :yearMonth AND categoryId IS NULL")
    suspend fun getTotalForMonth(yearMonth: String): Int?

    @Query("SELECT * FROM aggregated_stats ORDER BY yearMonth ASC")
    fun getAllStats(): Flow<List<AggregatedStats>>
}
