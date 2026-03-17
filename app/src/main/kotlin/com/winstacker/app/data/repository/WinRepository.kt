package com.winstacker.app.data.repository

import com.winstacker.app.data.db.StatsDao
import com.winstacker.app.data.db.WinDao
import com.winstacker.app.data.model.AggregatedStats
import com.winstacker.app.data.model.Category
import com.winstacker.app.data.model.CategoryCount
import com.winstacker.app.data.model.Win
import com.winstacker.app.data.model.WinWithCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WinRepository @Inject constructor(
    private val winDao: WinDao,
    private val statsDao: StatsDao
) {
    // ── CRUD ──────────────────────────────────────────────────────────────────

    suspend fun addWin(win: Win): Long {
        val id = winDao.insertWin(win)
        updateAggregatedStats(win)
        return id
    }

    suspend fun updateWin(old: Win, new: Win) {
        winDao.updateWin(new)
        // Recalculate stats for affected months
        decrementAggregatedStats(old)
        updateAggregatedStats(new)
    }

    suspend fun deleteWin(win: Win) {
        winDao.deleteWin(win)
        decrementAggregatedStats(win)
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    fun getWinsInRange(startDay: Long, endDay: Long): Flow<List<WinWithCategory>> =
        winDao.getWinsInRange(startDay, endDay)

    fun getWinsInRangeByCategory(
        startDay: Long,
        endDay: Long,
        categoryId: Long
    ): Flow<List<WinWithCategory>> =
        winDao.getWinsInRangeByCategory(startDay, endDay, categoryId)

    fun getWinsInRangeForOther(
        startDay: Long,
        endDay: Long
    ): Flow<List<WinWithCategory>> =
        winDao.getWinsInRangeForOther(startDay, endDay, Category.PRESET_IDS)

    fun getCategoryCountsInRange(startDay: Long, endDay: Long): Flow<List<CategoryCount>> =
        winDao.getCategoryCountsInRange(startDay, endDay)

    fun getWinCountForDay(day: Long): Flow<Int> = winDao.getWinCountForDay(day)

    suspend fun getWinCountForDaySuspend(day: Long): Int = winDao.getWinCountForDaySuspend(day)

    // ── Streaks ───────────────────────────────────────────────────────────────

    suspend fun calculateCurrentStreak(): Int {
        val days = winDao.getAllDistinctDays()
        if (days.isEmpty()) return 0

        val today = LocalDate.now().toEpochDay()
        var streak = 0
        var expected = today

        for (day in days) {
            if (day == expected || (streak == 0 && day == today - 1)) {
                if (streak == 0 && day != today) {
                    // Started from yesterday
                }
                streak++
                expected = day - 1
            } else {
                break
            }
        }
        return streak
    }

    suspend fun calculateLongestStreak(): Int {
        val days = winDao.getAllDistinctDays().sorted()
        if (days.isEmpty()) return 0

        var longest = 1
        var current = 1

        for (i in 1 until days.size) {
            if (days[i] == days[i - 1] + 1) {
                current++
                if (current > longest) longest = current
            } else {
                current = 1
            }
        }
        return longest
    }

    // ── Stats ─────────────────────────────────────────────────────────────────

    suspend fun getTotalWinsInRange(startDay: Long, endDay: Long): Int =
        winDao.getTotalWinsInRange(startDay, endDay)

    suspend fun getTopCategoryInRange(startDay: Long, endDay: Long): CategoryCount? =
        winDao.getTopCategoryInRange(startDay, endDay)

    // ── Maintenance ───────────────────────────────────────────────────────────

    suspend fun pruneOldEntries() {
        val cutoff = LocalDate.now().minusYears(2).toEpochDay()
        winDao.pruneOldWins(cutoff)
    }

    // ── Aggregated stats helpers ──────────────────────────────────────────────

    private suspend fun updateAggregatedStats(win: Win) {
        val yearMonth = epochDayToYearMonth(win.dateEpochDay)
        val existing = statsDao.getTotalForMonth(yearMonth) ?: 0
        statsDao.upsert(AggregatedStats(yearMonth = "$yearMonth-null", categoryId = null, winCount = existing + 1))
        win.categoryId?.let { catId ->
            val key = "$yearMonth-$catId"
            statsDao.upsert(AggregatedStats(yearMonth = key, categoryId = catId, winCount = existing + 1))
        }
    }

    private suspend fun decrementAggregatedStats(win: Win) {
        val yearMonth = epochDayToYearMonth(win.dateEpochDay)
        val existing = statsDao.getTotalForMonth(yearMonth) ?: 1
        val newCount = maxOf(0, existing - 1)
        statsDao.upsert(AggregatedStats(yearMonth = "$yearMonth-null", categoryId = null, winCount = newCount))
    }

    private fun epochDayToYearMonth(epochDay: Long): String {
        val date = LocalDate.ofEpochDay(epochDay)
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM"))
    }
}
