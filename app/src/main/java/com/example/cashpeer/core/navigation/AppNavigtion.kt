package com.example.cashpeer.core.navigation


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val navBackStackEntry =
        navController.currentBackStackEntryAsState().value

    val currentRoute = navBackStackEntry?.destination?.route

    val mainRoutes = setOf(
        AppRoute.HOME,
        AppRoute.TRANSACTION,
        AppRoute.SAVING_GOALS,
        AppRoute.STATISTICS,
        AppRoute.PROFILE
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in mainRoutes) {
                CashPeerBottomBar(navController)
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}