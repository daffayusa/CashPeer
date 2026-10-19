package com.example.cashpeer.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cashpeer.feature.home.presentation.HomeScreen
import com.example.cashpeer.feature.home.presentation.HomeViewModel
import com.example.cashpeer.feature.savinggoal.presentation.SavingGoalFormScreen
import com.example.cashpeer.feature.savinggoal.presentation.SavingGoalListScreen
import com.example.cashpeer.feature.transaction.presentation.add.AddTransactionScreen
import com.example.cashpeer.feature.transaction.presentation.list.TransactionListScreen


@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.HOME,
        modifier = modifier
    ) {
        composable(AppRoute.HOME) {
            val viewModel: HomeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            HomeScreen(
                uiState = uiState,
                onAddTransaction = {
                    navController.navigate(AppRoute.ADD_TRANSACTION)
                },
                onViewTransactions = {
                    navController.navigate(AppRoute.TRANSACTION)
                },
                onViewSavingGoals = {
                    navController.navigate(AppRoute.SAVING_GOALS)
                },
                onSavingGoalClick = { goalId ->
                    navController.navigate(AppRoute.editSavingGoal(goalId))
                },
                onMonthClick = {},
                onPreviousMonth = {
                    viewModel.previousMonth()
                },
                onNextMonth = {
                    viewModel.nextMonth()
                }

            )
        }

        composable(AppRoute.TRANSACTION) {
            TransactionListScreen(
                onBack = {},
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

        composable(AppRoute.SAVING_GOALS) {
            SavingGoalListScreen(
                onAddClick = {
                    navController.navigate(AppRoute.ADD_SAVING_GOALS)
                }
            )
        }

        composable(
            route = AppRoute.EDIT_SAVING_GOALS,
            arguments = listOf(
                navArgument("goalId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getLong("goalId")

            SavingGoalFormScreen(
                goalId = goalId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoute.ADD_SAVING_GOALS) {
            SavingGoalFormScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }


    }
}