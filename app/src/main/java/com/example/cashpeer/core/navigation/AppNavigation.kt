
package com.example.cashpeer.core.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        Log.d("NavRoute", "Current route: $currentRoute")
    }

    val mainRoutes = setOf(
        AppRoute.HOME,
        AppRoute.TRANSACTION,
        AppRoute.STATISTICS,
        AppRoute.PROFILE
    )

    val bottomBarVisible = remember { androidx.compose.runtime.mutableStateOf(true) }
    val scrollAccumulator = remember { mutableFloatStateOf(0f) }

    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: androidx.compose.ui.geometry.Offset,
                available: androidx.compose.ui.geometry.Offset,
                source: NestedScrollSource
            ): androidx.compose.ui.geometry.Offset {
                val deltaY = consumed.y

                if (deltaY != 0f) {
                    scrollAccumulator.floatValue += deltaY

                    if (scrollAccumulator.floatValue < -24f) {
                        bottomBarVisible.value = false
                        scrollAccumulator.floatValue = 0f
                    } else if (scrollAccumulator.floatValue > 24f) {
                        bottomBarVisible.value = true
                        scrollAccumulator.floatValue = 0f
                    }
                }

                return androidx.compose.ui.geometry.Offset.Zero
            }
        }
    }

    androidx.compose.runtime.LaunchedEffect(currentRoute) {
        bottomBarVisible.value = true
        scrollAccumulator.floatValue = 0f
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .nestedScroll(scrollConnection)
        ) {
            AppNavGraph(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
            AnimatedVisibility(
                visible = currentRoute in mainRoutes && bottomBarVisible.value,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp),
                enter = slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                CashPeerBottomBar(navController)
            }
        }
    }

}
