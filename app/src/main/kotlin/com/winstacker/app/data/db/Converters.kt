package com.winstacker.app.data.db

import androidx.room.TypeConverter
import com.winstacker.app.data.model.WinSource

class Converters {
    @TypeConverter
    fun fromWinSource(value: WinSource): String = value.name

    @TypeConverter
    fun toWinSource(value: String): WinSource = WinSource.valueOf(value)
}
