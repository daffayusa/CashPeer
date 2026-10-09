package com.example.cashpeer.feature.category.data.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long =0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    val name: String,

    val type: String
)