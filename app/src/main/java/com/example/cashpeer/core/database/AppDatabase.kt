package com.example.cashpeer.core.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.example.cashpeer.feature.transaction.data.local.TransactionDao
import com.example.cashpeer.feature.transaction.data.local.TransactionEntity
import com.example.cashpeer.feature.transaction.domain.model.Transaction

@Database(
    entities = [
        TransactionEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun transactionDao(): TransactionDao
}