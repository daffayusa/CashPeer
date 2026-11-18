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
import com.example.cashpeer.feature.category.presentation.CategoryManagementScreen
import com.example.cashpeer.feature.home.presentation.HomeScreen
import com.example.cashpeer.feature.home.presentation.HomeViewModel
import com.example.cashpeer.feature.profile.presentation.ProfileScreen
import com.example.cashpeer.feature.savinggoal.presentation.SavingGoalFormScreen
import com.example.cashpeer.feature.savinggoal.presentation.SavingGoalListScreen
import com.example.cashpeer.feature.savinggoal.presentation.detail.SavingGoalDetailScreen
import com.example.cashpeer.feature.statistics.presentation.StatisticsScreen
import com.example.cashpeer.feature.statistics.presentation.StatisticsViewModel
import com.example.cashpeer.feature.transaction.presentation.add.AddTransactionScreen
import com.example.cashpeer.feature.transaction.presentation.edit.EditTransactionScreen
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
                    navController.navigate(AppRoute.TRANSACTION){
                        launchSingleTop = true
                    }
                },
                onViewSavingGoals = {
                    navController.navigate(AppRoute.SAVING_GOALS)
                },
                onSavingGoalClick = { goalId ->
                    navController.navigate(AppRoute.savingGoalDetail(goalId))
                },
                onMonthClick = {},
                onPreviousMonth = {
                    viewModel.previousMonth()
                },
                onNextMonth = {
                    viewModel.nextMonth()
                },
                onSetBudget = viewModel::setMonthlyBudget

            )
        }

        composable(AppRoute.TRANSACTION) {
            TransactionListScreen(
                onBack = {},
                onAddTransaction = {
                    navController.navigate(AppRoute.ADD_TRANSACTION)
                },
                onEditTransaction = { id ->
                    navController.navigate(AppRoute.editTransaction(id))
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
                },
                onGoalClick = { goalId ->
                    navController.navigate(AppRoute.savingGoalDetail(goalId))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = AppRoute.SAVING_GOAL_DETAIL,
            arguments = listOf(
                navArgument("goalId") {
                    type = NavType.LongType
                }
            )
        ) {
            SavingGoalDetailScreen(
                onBack = {
                    navController.popBackStack()
                },
                onEditGoal = { goalId ->
                    navController.navigate(AppRoute.editSavingGoal(goalId))
                },
                onEditTransaction = { transactionId ->
                    navController.navigate(AppRoute.editTransaction(transactionId))
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
        composable(AppRoute.STATISTICS) {
            val viewModel: StatisticsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            StatisticsScreen(
                uiState = uiState,
                onPreviousMonth = viewModel::previousMonth,
                onNextMonth = viewModel::nextMonth,
                onCurrentMonth = viewModel::selectCurrentMonth
            )
        }

        composable(
            route = AppRoute.EDIT_TRANSACTION,
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: return@composable
            EditTransactionScreen(
                transactionId = transactionId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoute.PROFILE) {
            ProfileScreen(
                onNavigateToCategories = {
                    navController.navigate(AppRoute.CATEGORY_MANAGEMENT)
                }
            )
        }

        composable(AppRoute.CATEGORY_MANAGEMENT) {
            CategoryManagementScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

    }
}