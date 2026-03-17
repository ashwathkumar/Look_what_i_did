package com.winstacker.app.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class WinWithCategory(
    @Embedded val win: Win,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category?
)
