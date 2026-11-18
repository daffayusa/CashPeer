package com.example.cashpeer.core.navigation


object AppRoute {
    const val HOME = "home"
    const val TRANSACTION = "transaction"
    const val ADD_TRANSACTION = "add_transaction"
    const val SAVING_GOALS = "saving_goals"
    const val ADD_SAVING_GOALS = "add_saving_goals"
    const val EDIT_SAVING_GOALS = "edit_saving_goal/{goalId}"
    const val SAVING_GOAL_DETAIL = "saving_goal_detail/{goalId}"

    const val STATISTICS = "statistics"
    const val PROFILE = "profile"
    const val CATEGORY_MANAGEMENT = "category_management"

    fun editSavingGoal(goalId: Long): String {
        return "edit_saving_goal/$goalId"
    }

    fun savingGoalDetail(goalId: Long): String {
        return "saving_goal_detail/$goalId"
    }

    const val EDIT_TRANSACTION = "edit_transaction/{transactionId}"
    fun editTransaction(id: Long) = "edit_transaction/$id"
}