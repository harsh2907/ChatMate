package com.example.signalclone.ui.screens.auth_screens.signup_screen

import android.app.Activity.RESULT_OK
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.signalclone.R
import com.example.signalclone.ui.screens.auth_screens.AuthViewModel
import com.example.signalclone.ui.screens.auth_screens.SignInEvent
import com.example.signalclone.ui.screens.auth_screens.utils.SignInUtils
import com.example.signalclone.ui.screens.nav_screen.Screens
import com.example.signalclone.ui.theme.ThemeColor
import com.example.signalclone.ui.utils.GoogleSignInButton
import com.example.signalclone.ui.utils.LoadingDialog
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    onEvent: (SignInEvent) -> Unit
) {
    val (username, onUsernameChange) = rememberSaveable { mutableStateOf("") }
    val (bioLink, onBioLinkChange) = rememberSaveable { mutableStateOf("") }
    val (email, onEmailChange) = rememberSaveable { mutableStateOf("") }
    val (password, onPasswordChange) = rememberSaveable { mutableStateOf("") }
    val (imageUri, setImageUri) = rememberSaveable { mutableStateOf<Uri?>(null) }
//  val (bio, onBioChange) = rememberSaveable { mutableStateOf("") }

    val state by viewModel.signUpState.collectAsStateWithLifecycle()

    val keyboard = LocalSoftwareKeyboardController.current
    var passwordVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(key1 = true){
        viewModel.resetSignInState()
    }

    LaunchedEffect(key1 = email, key2 = username, key3 = password, block = {
        errorMessage = SignInUtils.verifySignUpDetails(email, username, password)
    })

    LaunchedEffect(key1 = state.isSignUpSuccessful) {
        if (state.isSignUpSuccessful) {
            navController.navigate(Screens.MainScreen.route) {
                popUpTo(0)
            }
        }
    }

    LaunchedEffect(key1 = state.error) {
        if (state.error.isNotEmpty()) {
            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
            isLoading = false
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                coroutineScope.launch {
                    onEvent(
                        SignInEvent.OnSigningInWithGoogle(
                            intent = result.data ?: return@launch,
                            username = username
                        )
                    )
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

    LoadingDialog(isLoading = state.isSigningUp)

    Column(
        Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {

        val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = setImageUri
        )

        Text(text = "Lets Register Account!", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (imageUri == null) {
                Box(modifier = Modifier
                    .size(80.dp)
                    .background(ThemeColor.copy(alpha = .3f), CircleShape)
                    .clip(CircleShape)
                    .align(Alignment.Center)
                    .clickable {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = ThemeColor.copy(alpha = .7f),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(60.dp)

                    )
                }
            }
            else{
                AsyncImage(
                    model = imageUri, contentDescription = "profile picture",
                    placeholder = rememberVectorPainter(image = Icons.Default.Person),
                    modifier = Modifier
                        .size(80.dp)
                        .background(ThemeColor.copy(alpha = .3f), CircleShape)
                        .clip(CircleShape)
                        .align(Alignment.Center)
                        .clickable {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }

        TextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text(text = "Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardActions = KeyboardActions(onDone = { keyboard?.hide() }),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            supportingText = {
//                if (errorMessage.toLowerCase(Locale.current).contains("username") ) {
//                    Text(
//                        modifier = Modifier.fillMaxWidth(),
//                        text = errorMessage,
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
            }
        )

        TextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(text = "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardActions = KeyboardActions(onDone = { keyboard?.hide() }),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ), supportingText = {
//                if (errorMessage.toLowerCase(Locale.current).contains("email")) {
//                    Text(
//                        modifier = Modifier.fillMaxWidth(),
//                        text = errorMessage,
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
            }
        )

        TextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(text = "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardActions = KeyboardActions(onDone = { keyboard?.hide() }),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
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
            }, supportingText = {
//                if (errorMessage.toLowerCase(Locale.current).contains("password")) {
//                    Text(
//                        modifier = Modifier.fillMaxWidth(),
//                        text = errorMessage,
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
            }
        )


        TextField(
            value = bioLink,
            onValueChange = onBioLinkChange,
            label = { Text(text = "Bio link (Optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            keyboardActions = KeyboardActions(onDone = { keyboard?.hide() }),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                // focusedLabelColor = Color.Transparent
            )
        )

        Button(
            onClick = {
                onEvent(
                    SignInEvent.SignUp(
                        username,
                        email,
                        password,
                        imageUri,
                        bioLink.ifEmpty { null })
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
            ),
            enabled = errorMessage.isEmpty()
        ) {
            Text(text = "Sign in", modifier = Modifier.padding(6.dp))
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "or", color = Color.Gray)
        }

        GoogleSignInButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Sign up using google",
            icon = painterResource(id = R.drawable.google_logo),
            shape = RoundedCornerShape(6.dp),
            isLoading = isLoading,
            progressIndicatorColor = ThemeColor,
        ) {

            isLoading = !isLoading

            coroutineScope.launch {
                val verificationError = viewModel.verifyUsername(username)
                if (verificationError.isNotEmpty()) {
                    Toast.makeText(context, verificationError, Toast.LENGTH_LONG).show()
                    isLoading = !isLoading

                    return@launch
                }

                val signInIntent = viewModel.googleAuthUiClient.signIn()
                launcher.launch(
                    IntentSenderRequest.Builder(
                        intentSender = signInIntent ?: return@launch
                    ).build()
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = MaterialTheme.typography.labelLarge.fontSize,
                            color = Color.Gray
                        )
                    ) {
                        append("Already have an account? ")
                    }

                    withStyle(
                        style = SpanStyle(
                            fontSize = MaterialTheme.typography.labelLarge.fontSize,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(" Login Now")
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .clickable {
                        navController.navigate(Screens.SignInScreen.route)
                    }
            )
        }
    }
}