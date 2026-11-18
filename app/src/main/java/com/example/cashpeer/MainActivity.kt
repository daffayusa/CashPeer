package com.example.cashpeer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cashpeer.core.navigation.AppNavigation
import com.example.cashpeer.core.ui.theme.CashPeerTheme
import com.example.cashpeer.core.ui.theme.ThemeManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ThemeManager.init(this)

        setContent {
            CashPeerTheme(darkTheme = ThemeManager.isDarkTheme()) {
                AppNavigation()
            }
        }
    }
}
