package com.example.cashpeer.feature.savinggoal.data.Mapper

import com.example.cashpeer.feature.savinggoal.data.local.SavingGoalEntity
import com.example.cashpeer.feature.savinggoal.domain.model.SavingFrequency
import com.example.cashpeer.feature.savinggoal.domain.model.SavingGoal
import java.time.LocalDate


fun SavingGoalEntity.toDomain(): SavingGoal {
    return SavingGoal(
        id = id,
        userId = userId,
        name = name,
        targetAmount = targetAmount,
        targetDate = targetDate?.let(LocalDate::parse),
        note = note,
        createdAt = createdAt,
        savingFrequency = SavingFrequency.valueOf(savingFrequency),
        savingAmount = savingAmount
    )
}

fun SavingGoal.toEntity(): SavingGoalEntity {
    return SavingGoalEntity(
        id = id,
        userId = userId,
        name = name,
        targetAmount = targetAmount,
        targetDate = targetDate?.toString(),
        note = note,
        createdAt = createdAt,
        savingFrequency = savingFrequency.name,
        savingAmount = savingAmount
    )
}