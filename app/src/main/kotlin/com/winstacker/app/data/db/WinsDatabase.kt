package com.winstacker.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.winstacker.app.data.model.AggregatedStats
import com.winstacker.app.data.model.Category
import com.winstacker.app.data.model.Win

@Database(
    entities = [Win::class, Category::class, AggregatedStats::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WinsDatabase : RoomDatabase() {
    abstract fun winDao(): WinDao
    abstract fun categoryDao(): CategoryDao
    abstract fun statsDao(): StatsDao
}
