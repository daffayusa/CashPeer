package com.example.cashpeer.core.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.example.cashpeer.feature.category.data.local.CategoryDao
import com.example.cashpeer.feature.category.data.local.CategoryEntity
import com.example.cashpeer.feature.transaction.data.local.TransactionDao
import com.example.cashpeer.feature.transaction.data.local.TransactionEntity

val MIGRATION_1_2 = object : Migration(1, 2) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `categories` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `user_id` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `type` TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
}