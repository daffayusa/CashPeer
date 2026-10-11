package com.example.cashpeer.feature.savinggoal.data.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "saving_goals",
    indices = [
        Index("user_id"),
        Index("target_date")
    ]
)
data class SavingGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    val name: String,

    @ColumnInfo(name = "target_amount")
    val targetAmount: Long,

    @ColumnInfo(name = "target_date")
    val targetDate: String?,

    val note: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)