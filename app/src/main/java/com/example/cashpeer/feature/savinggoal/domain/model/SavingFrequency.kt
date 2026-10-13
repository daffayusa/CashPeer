package com.example.cashpeer.feature.savinggoal.domain.model

enum class SavingFrequency(
    val label: String,
    val daysPerPeriod: Int
){
    DAILY("Per hari", 1),
    WEEKLY("Per minggu", 7),
    MONTHLY("Per bulan", 30)
}