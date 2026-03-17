package com.winstacker.app.di

import android.content.Context
import androidx.room.Room
import com.winstacker.app.data.db.CategoryDao
import com.winstacker.app.data.db.StatsDao
import com.winstacker.app.data.db.WinDao
import com.winstacker.app.data.db.WinsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WinsDatabase =
        Room.databaseBuilder(
            context,
            WinsDatabase::class.java,
            "wins_tracker.db"
        ).build()

    @Provides
    fun provideWinDao(db: WinsDatabase): WinDao = db.winDao()

    @Provides
    fun provideCategoryDao(db: WinsDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideStatsDao(db: WinsDatabase): StatsDao = db.statsDao()
}
