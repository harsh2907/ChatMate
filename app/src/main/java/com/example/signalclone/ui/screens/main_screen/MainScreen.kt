package com.example.signalclone.ui.screens.main_screen


import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.signalclone.ui.screens.auth_screens.AuthViewModel
import com.example.signalclone.ui.screens.nav_screen.MainNavScreen
import com.example.signalclone.ui.screens.nav_screen.Screens
import com.example.signalclone.ui.theme.QuickSand


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val bottomNavController = rememberNavController()
    var displayOption by remember { mutableStateOf(false) }
    var showLogoutDialog = remember { mutableStateOf(false) }
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = currentUser ){
        if(currentUser == null){
            navController.navigate(Screens.SignInScreen.route) {
                popUpTo(0)
            }
        }
    }

    AnimatedVisibility(visible = showLogoutDialog.value) {
        ShowDialog(
            state = showLogoutDialog,
            title = "Logout",
            message = "Do you want to log out this account?"
        ) {
            authViewModel.signOut()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            MainScreenBottomNav(navController = bottomNavController)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "ChatMate🔥", fontFamily = QuickSand, fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = { displayOption = !displayOption }) {
                        Icon(Icons.Default.MoreVert, "")
                    }

                    // Creating a dropdown menu
                    DropdownMenu(
                        expanded = displayOption,
                        onDismissRequest = { displayOption = false }
                    ) {

//                        DropdownMenuItem(text = {
//                            Text(text = "Settings")
//                        }, onClick = {
//                            Toast.makeText(
//                                context,
//                                "Settings",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        })

                        DropdownMenuItem(text = {
                            Text(text = "Logout")
                        }, onClick = {
                            showLogoutDialog.value = true
                        })
                    }
                }
            )
        }
    ) { paddingValues ->
        MainNavScreen(
            navController = bottomNavController,
            modifier = Modifier.padding(paddingValues),
            authViewModel = authViewModel
        )
    }
}

@Composable
fun ShowDialog(
    state:MutableState<Boolean>,
    title: String,
    message: String,
    onActionDone: () -> Unit
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onActionDone) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = { state.value = false }) {
                Text(text = "Cancel")
            }
        },
        onDismissRequest = { state.value = false }
    )
}
