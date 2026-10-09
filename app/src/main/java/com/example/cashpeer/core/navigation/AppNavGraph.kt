package com.example.cashpeer.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cashpeer.feature.transaction.presentation.add.AddTransactionScreen
import com.example.cashpeer.feature.transaction.presentation.list.TransactionListScreen

@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.ADD_TRANSACTION
    ){
        composable(AppRoute.TRANSACTION){
            TransactionListScreen(
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
    }
}