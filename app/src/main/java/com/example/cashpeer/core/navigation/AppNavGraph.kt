package com.example.cashpeer.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cashpeer.feature.savinggoal.presentation.SavingGoalFormScreen
import com.example.cashpeer.feature.savinggoal.presentation.SavingGoalListScreen
import com.example.cashpeer.feature.transaction.presentation.add.AddTransactionScreen
import com.example.cashpeer.feature.transaction.presentation.list.TransactionListScreen

@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.SAVING_GOALS
    ){
        composable(AppRoute.TRANSACTION){
            TransactionListScreen(
                onBack = {
                    navController.popBackStack()
                },
                onAddTransaction = {
                    navController.navigate(AppRoute.ADD_TRANSACTION)
                }
            )
        }
        composable(AppRoute.ADD_TRANSACTION) {
            AddTransactionScreen(
                onBack = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }
        composable(AppRoute.SAVING_GOALS){
            SavingGoalListScreen(
                onAddClick = {
                    navController.navigate(AppRoute.ADD_SAVING_GOALS)
                }
            )
        }
        composable(
            AppRoute.EDIT_SAVING_GOALS,
            arguments = listOf(
                navArgument("goalId"){
                    type = NavType.LongType
                }
            )
        ){backStackEntry ->
            val goalId = backStackEntry.arguments?.getLong("goalId")
            SavingGoalFormScreen(
                goalId = goalId,
                onBack = {navController.popBackStack()}
            )
        }
        composable(AppRoute.ADD_SAVING_GOALS){
            SavingGoalFormScreen(
                onBack = {navController.popBackStack()}
            )
        }

    }
}