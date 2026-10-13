package com.example.cashpeer.feature.savinggoal.domain.model

import java.time.LocalDate

fun estimateCompletionDate(
    targetAmount: Long,
    savedAmount: Long,
    savingAmount: Long,
    frequency: SavingFrequency,
    startDate: LocalDate = LocalDate.now()
): LocalDate? {
    val remaining = (targetAmount - savedAmount).coerceAtLeast(0L)

    if (remaining == 0L || savingAmount <= 0L) return null

    val periods = (remaining - 1L) / savingAmount + 1L

    return when (frequency) {
        SavingFrequency.DAILY -> startDate.plusDays(periods)
        SavingFrequency.WEEKLY -> startDate.plusWeeks(periods)
        SavingFrequency.MONTHLY -> startDate.plusMonths(periods)
    }
}