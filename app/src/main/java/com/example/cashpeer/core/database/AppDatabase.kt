package com.example.cashpeer.core.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.example.cashpeer.feature.category.data.local.CategoryDao
import com.example.cashpeer.feature.category.data.local.CategoryEntity
import com.example.cashpeer.feature.savinggoal.data.local.SavingGoalDao
import com.example.cashpeer.feature.savinggoal.data.local.SavingGoalEntity
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
val MIGRATION_2_3 = object : Migration(2, 3) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            ALTER TABLE `saving_goals`
            ADD COLUMN `saving_frequency` TEXT NOT NULL DEFAULT 'DAILY'
            """.trimIndent()
        )

        connection.execSQL(
            """
            ALTER TABLE `saving_goals`
            ADD COLUMN `saving_amount` INTEGER NOT NULL DEFAULT 0
            """.trimIndent()
        )
    }
}

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        SavingGoalEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun savingGoalDao(): SavingGoalDao
    abstract fun categoryDao(): CategoryDao
}