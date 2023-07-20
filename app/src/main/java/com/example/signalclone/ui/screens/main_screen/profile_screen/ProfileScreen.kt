package com.example.signalclone.ui.screens.main_screen.profile_screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.signalclone.domain.models.user.User
import com.example.signalclone.ui.screens.auth_screens.AuthViewModel
import com.example.signalclone.ui.utils.LoadingDialog

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel

) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        authViewModel.loadCurrentUser()
    }
    AnimatedContent(targetState = currentUser) {
        when (currentUser) {
            null -> LoadingDialog(isLoading = true)
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    ProfileHeadSection(
                        user = currentUser!!,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

            }
        }
    }

}

@Composable
fun ProfileHeadSection(
    user: User,
    modifier: Modifier = Modifier
) {
    var expandImage by remember { mutableStateOf(false) }
    AnimatedVisibility(visible = expandImage) {
        Dialog(onDismissRequest = { expandImage = false }) {
            AsyncImage(
                model = user.userProfileImage,
                contentDescription = "user"
            )
        }
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.userProfileImage,
            contentDescription = "user",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .clickable {
                    expandImage = true
                },
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = user.username, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text(text = user.userEmail, style = MaterialTheme.typography.bodySmall)
        }
    }
}
