package com.example.cashpeer.core.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    AppNavGraph(
        navController = navController
    )
}