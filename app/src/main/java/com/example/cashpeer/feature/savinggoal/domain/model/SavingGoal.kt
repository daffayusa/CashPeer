package com.example.cashpeer.feature.savinggoal.domain.model

import java.time.LocalDate

data class SavingGoal(
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val targetAmount: Long,
    val targetDate: LocalDate?,
    val note: String? = null,
    val savingFrequency: SavingFrequency = SavingFrequency.DAILY,
    val savingAmount: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)