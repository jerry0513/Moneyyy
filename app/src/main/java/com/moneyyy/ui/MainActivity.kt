package com.moneyyy.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.moneyyy.ui.navigation.MoneyyyNavigation
import com.moneyyy.ui.theme.MoneyyyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            MoneyyyTheme {
                MoneyyyNavigation()
            }
        }
    }
}