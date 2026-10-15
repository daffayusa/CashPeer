package com.example.cashpeer.core.navigation


object AppRoute {
    const val HOME = "home"
    const val TRANSACTION = "transaction"
    const val ADD_TRANSACTION = "add_transaction"
    const val SAVING_GOALS = "saving_goals"
    const val ADD_SAVING_GOALS = "add_saving_goals"
    const val EDIT_SAVING_GOALS = "edit_saving_goal/{goalId}"

    const val STATISTICS = "statistics"
    const val PROFILE = "profile"

    fun editSavingGoal(goalId: Long): String {
        return "edit_saving_goal/$goalId"
    }
}