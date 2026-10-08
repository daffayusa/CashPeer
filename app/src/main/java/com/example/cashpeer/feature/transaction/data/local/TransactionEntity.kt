package com.example.cashpeer.feature.transaction.data.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey


@Entity(
    tableName = "transactions",
    indices = [
        Index("user_id"),
        Index("category_id"),
        Index("saving_goal_id"),
        Index("transaction_date")
    ]
)
data class  TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long =0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo("category_id")
    val categoryId: Long?,

    @ColumnInfo("saving_goal_id")
    val savingGoalId: Long?,

    val type: String,
    val amount: Long,

    @ColumnInfo("transaction_date")
    val transactionDate: String,

    val note: String?

//    @ColumnInfo("created_at")
//    val createdAt: Long,
//
//    @ColumnInfo("updated_at")
//    val updatedAt: Long
)
