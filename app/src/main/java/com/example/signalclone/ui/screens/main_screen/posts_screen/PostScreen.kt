package com.example.signalclone.ui.screens.main_screen.posts_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.navigation.NavHostController

@Composable
fun PostsScreen(
    navController: NavHostController
) {
    Box(modifier = Modifier.fillMaxSize()){
        Text(text = "Posts Screen",modifier =Modifier.align(Alignment.Center))
    }
}