package com.example.signalclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.signalclone.ui.screens.auth_screens.AuthViewModel
import com.example.signalclone.ui.screens.auth_screens.utils.GoogleAuthUiClient
import com.example.signalclone.ui.screens.nav_screen.SignalNavScreen
import com.example.signalclone.ui.theme.SignalCloneTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignalCloneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel by viewModels<AuthViewModel>()

                    SignalNavScreen(
                        navController = navController,
                        authViewModel = authViewModel
                    )

                }
            }
        }
    }
}

