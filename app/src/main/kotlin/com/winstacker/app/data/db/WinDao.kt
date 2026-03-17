package com.winstacker.app.data.db

import androidx.room.*
import com.winstacker.app.data.model.CategoryCount
import com.winstacker.app.data.model.Win
import com.winstacker.app.data.model.WinWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface WinDao {

    // ── Insert / Update / Delete ──────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWin(win: Win): Long

    @Update
    suspend fun updateWin(win: Win)

    @Delete
    suspend fun deleteWin(win: Win)

    // ── Queries by date range ─────────────────────────────────────────────────

    @Transaction
    @Query("""
        SELECT * FROM wins
        WHERE dateEpochDay BETWEEN :startDay AND :endDay
        ORDER BY dateEpochDay DESC, createdAtMs DESC
    """)
    fun getWinsInRange(startDay: Long, endDay: Long): Flow<List<WinWithCategory>>

    @Transaction
    @Query("""
        SELECT * FROM wins
        WHERE dateEpochDay BETWEEN :startDay AND :endDay
          AND categoryId = :categoryId
        ORDER BY dateEpochDay DESC, createdAtMs DESC
    """)
    fun getWinsInRangeByCategory(
        startDay: Long,
        endDay: Long,
        categoryId: Long
    ): Flow<List<WinWithCategory>>

    /** Wins for custom (non-preset) categories — ids NOT in the preset set */
    @Transaction
    @Query("""
        SELECT * FROM wins
        WHERE dateEpochDay BETWEEN :startDay AND :endDay
          AND (categoryId IS NULL OR categoryId NOT IN (:presetIds))
        ORDER BY dateEpochDay DESC, createdAtMs DESC
    """)
    fun getWinsInRangeForOther(
        startDay: Long,
        endDay: Long,
        presetIds: Set<Long>
    ): Flow<List<WinWithCategory>>

    // ── Category summary (counts) ─────────────────────────────────────────────

    @Query("""
        SELECT categoryId, COUNT(*) as count
        FROM wins
        WHERE dateEpochDay BETWEEN :startDay AND :endDay
        GROUP BY categoryId
    """)
    fun getCategoryCountsInRange(startDay: Long, endDay: Long): Flow<List<CategoryCount>>

    // ── Single day ────────────────────────────────────────────────────────────

    @Query("SELECT COUNT(*) FROM wins WHERE dateEpochDay = :day")
    fun getWinCountForDay(day: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM wins WHERE dateEpochDay = :day")
    suspend fun getWinCountForDaySuspend(day: Long): Int

    // ── Streak helpers ────────────────────────────────────────────────────────

    /** Returns distinct days that have at least one win, ordered descending */
    @Query("SELECT DISTINCT dateEpochDay FROM wins ORDER BY dateEpochDay DESC")
    suspend fun getAllDistinctDays(): List<Long>

    // ── Stats ─────────────────────────────────────────────────────────────────

    @Query("SELECT COUNT(*) FROM wins WHERE dateEpochDay BETWEEN :startDay AND :endDay")
    suspend fun getTotalWinsInRange(startDay: Long, endDay: Long): Int

    @Query("""
        SELECT categoryId, COUNT(*) as count
        FROM wins
        WHERE dateEpochDay BETWEEN :startDay AND :endDay
        GROUP BY categoryId
        ORDER BY count DESC
        LIMIT 1
    """)
    suspend fun getTopCategoryInRange(startDay: Long, endDay: Long): CategoryCount?

    /** Returns day-of-week (1=Mon … 7=Sun) and its win count */
    @Query("""
        SELECT (dateEpochDay % 7 + 3) % 7 + 1 AS categoryId,
               COUNT(*) AS count
        FROM wins
        WHERE dateEpochDay BETWEEN :startDay AND :endDay
        GROUP BY categoryId
        ORDER BY count DESC
        LIMIT 1
    """)
    suspend fun getMostActiveDayOfWeekInRange(startDay: Long, endDay: Long): CategoryCount?

    // ── Pruning ───────────────────────────────────────────────────────────────

    @Query("DELETE FROM wins WHERE dateEpochDay < :cutoffDay")
    suspend fun pruneOldWins(cutoffDay: Long)
}
