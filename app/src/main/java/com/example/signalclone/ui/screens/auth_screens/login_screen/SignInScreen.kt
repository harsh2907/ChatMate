package com.example.signalclone.ui.screens.auth_screens.login_screen

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.signalclone.R
import com.example.signalclone.ui.screens.auth_screens.AuthViewModel
import com.example.signalclone.ui.screens.auth_screens.utils.SignInAction
import com.example.signalclone.ui.screens.auth_screens.utils.SignInUtils
import com.example.signalclone.ui.screens.nav_screen.Screens
import com.example.signalclone.ui.theme.ThemeColor
import com.example.signalclone.ui.utils.GoogleSignInButton
import com.example.signalclone.ui.utils.LoadingDialog
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val (email, onEmailChange) = rememberSaveable { mutableStateOf("") }
    val (password, onPasswordChange) = rememberSaveable { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current
    var passwordVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    val state by authViewModel.signInState.collectAsStateWithLifecycle()

    var errorMessage by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        authViewModel.resetSignUpState()
    }

    LaunchedEffect(key1 = email, key2 = password, block = {
        errorMessage = SignInUtils.verifySignInDetails(email, password)
    })


    LaunchedEffect(key1 = state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            navController.navigate(Screens.MainScreen.route) {
                popUpTo(0)
            }
        }
    }

    LaunchedEffect(key1 = state.signInError) {
        if (!state.signInError.isNullOrEmpty())
            Toast.makeText(context, state.signInError, Toast.LENGTH_SHORT).show()
    }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                coroutineScope.launch {
                    val signInResult = authViewModel.googleAuthUiClient.getSignInResponseFromIntent(
                        intent = result.data ?: return@launch,
                        action = SignInAction.SIGNIN
                    )
                    authViewModel.onSignInResult(signInResult)
                }
            } else {
                Toast.makeText(
                    context,
                    "An unknown error occurred. Please try again later",
                    Toast.LENGTH_SHORT
                ).show()
                isLoading = false
            }
        }
    )

    LoadingDialog(isLoading = state.isLoading)

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .align(Alignment.TopCenter)
        ) {
            Text(text = "Lets Sign you in", style = MaterialTheme.typography.headlineLarge)
            Text(text = "Welcome Back,", style = MaterialTheme.typography.labelLarge)
            Text(text = "You have been missed", style = MaterialTheme.typography.labelLarge)

            Spacer(modifier = Modifier.padding(12.dp))

            TextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text(text = "Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardActions = KeyboardActions(onNext = { keyboard?.hide() }),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    // focusedLabelColor = Color.Transparent
                )
            )
            TextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text(text = "Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardActions = KeyboardActions(onNext = { keyboard?.hide() }),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    //  focusedLabelColor = Color.Transparent
                ),
                trailingIcon = {
                    val icon: ImageVector =
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    Icon(
                        imageVector = icon,
                        contentDescription = "Toggle password visibility",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(24.dp)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = { passwordVisible = !passwordVisible })
                    )
                }
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(text = "Forgot Password ?",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        var message = SignInUtils.isEmailValid(email)
                        if (message.isNotEmpty()) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        } else {
                            coroutineScope.launch {
                                message = authViewModel.forgotPassword(email).let {
                                    if (it)
                                        "A link has been sent to your email to reset your password"
                                    else
                                        "An unknown error occurred. Please try again later"
                                }
                                Toast.makeText(
                                    context,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        }
                    })
            }
            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                onClick = {
                    errorMessage = SignInUtils.verifySignInDetails(email, password)
                    if (errorMessage.isEmpty())
                        authViewModel.signInWithEmail(email, password)
                    else
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                ),
                enabled = errorMessage.isEmpty()
            ) {
                Text(text = "Sign in", modifier = Modifier.padding(12.dp))
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp), horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "or", color = Color.Gray)
            }

            GoogleSignInButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Sign in with google",
                icon = painterResource(id = R.drawable.google_logo),
                shape = RoundedCornerShape(6.dp),
                isLoading = isLoading,
                progressIndicatorColor = ThemeColor
            ) {
                isLoading = !isLoading

                coroutineScope.launch {
                    val signInIntent = authViewModel.googleAuthUiClient.signIn()
                    launcher.launch(
                        IntentSenderRequest.Builder(
                            intentSender = signInIntent ?: return@launch
                        ).build()
                    )
                }
            }
        }

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontSize = MaterialTheme.typography.labelLarge.fontSize,
                        color = Color.Gray
                    )
                ) {
                    append("Don't have an account? ")
                }

                withStyle(
                    style = SpanStyle(
                        fontSize = MaterialTheme.typography.labelLarge.fontSize,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append(" Register Now")
                }
            },
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.BottomCenter)
                .clickable {
                    navController.navigate(Screens.SignUpScreen.route)
                }
        )
    }
}