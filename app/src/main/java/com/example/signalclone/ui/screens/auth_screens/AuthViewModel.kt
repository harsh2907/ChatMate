package com.example.signalclone.ui.screens.auth_screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signalclone.domain.models.user.User
import com.example.signalclone.domain.repository.FirebaseRepository
import com.example.signalclone.ui.screens.auth_screens.utils.GoogleAuthUiClient
import com.example.signalclone.ui.screens.auth_screens.utils.SignInAction
import com.example.signalclone.ui.screens.auth_screens.utils.SignInResponse
import com.example.signalclone.ui.screens.auth_screens.utils.SignInState
import com.example.signalclone.ui.screens.auth_screens.utils.SignInUtils
import com.example.signalclone.ui.screens.auth_screens.utils.SignupState
import com.example.signalclone.ui.utils.SignInResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val googleAuthUiClient: GoogleAuthUiClient,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val auth = Firebase.auth

    private val _signInState = MutableStateFlow(SignInState())
    val signInState = _signInState.asStateFlow()

    val currentUser: MutableStateFlow<User?> = MutableStateFlow(null)

    private val _signupState: MutableStateFlow<SignupState> = MutableStateFlow(SignupState())
    val signUpState = _signupState.asStateFlow()


    init {
        val userId = auth.currentUser?.uid
        if (userId != null)
            viewModelScope.launch {
                currentUser.value = firebaseRepository.getUser(userId)
            }
    }


    fun onEvent(uiEvent: SignInEvent) {
        when (uiEvent) {
            is SignInEvent.SignUp -> {
                signUpWithEmail(
                    uiEvent.email,
                    uiEvent.password,
                    uiEvent.username,
                    uiEvent.bioLink,
                    uiEvent.imageUri
                )
            }

            is SignInEvent.OnSigningInWithGoogle -> {
                signUpWithGoogle(uiEvent.intent, uiEvent.username)
            }
        }
    }

    private fun signUpWithGoogle(intent: Intent, username: String) {
        viewModelScope.launch {
            _signupState.update { it.copy(isSigningUp = true) }
            val res = googleAuthUiClient.getSignInResponseFromIntent(
                intent = intent, action = SignInAction.SIGNUP, username = username
            ) {
                addUser(it)
            }

            if (!res.error.isNullOrEmpty()) {
                _signupState.update {
                    it.copy(
                        isSigningUp = false,
                        isSignUpSuccessful = false,
                        error = res.error
                    )
                }
            }

        }
    }

    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            firebaseRepository.signInWithEmail(email, pass).collectLatest { res ->
                when (res) {
                    is SignInResult.Failed -> _signInState.update {
                        it.copy(
                            isSignInSuccessful = false,
                            signInError = res.message,
                            isLoading = false
                        )
                    }

                    SignInResult.Loading -> _signInState.update {
                        it.copy(
                            isSignInSuccessful = false,
                            signInError = "",
                            isLoading = true
                        )
                    }

                    SignInResult.Success -> {
                        currentUser.value = auth.currentUser?.uid?.let { id ->
                            firebaseRepository.getUser(id)
                        }
                        _signInState.update {
                            it.copy(
                                isSignInSuccessful = true,
                                signInError = "",
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun signUpWithEmail(
        email: String,
        pass: String,
        username: String,
        bioLink: String?,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            firebaseRepository.signUpWithEmail(
                email = email, password = pass, username = username
            )
                .collectLatest { res ->
                    when (res) {
                        is SignInResult.Failed -> _signupState.update {
                            it.copy(
                                isSigningUp = false,
                                error = res.message
                            )
                        }

                        SignInResult.Loading -> _signupState.update {
                            it.copy(
                                isSigningUp = true,
                                error = ""
                            )
                        }

                        SignInResult.Success -> {
                            val uid = auth.currentUser?.uid!!
                            val res = imageUri?.let {
                                firebaseRepository.uploadProfileImage(uid, it)
                            }

                            val image = if (res != null) firebaseRepository.getImage(uid) else null
                            Log.e("Image uri", image.toString())
                            val user = User(
                                userId = uid,
                                username = username,
                                userEmail = email,
                                userBioLink = bioLink,
                                userProfileImage = image?.toString()
                            )
                            currentUser.value = user
                            addUser(user)
                        }
                    }
                }
        }
    }


    private fun addUser(user: User) {
        viewModelScope.launch {
            firebaseRepository.addUser(user).collectLatest { res ->
                when (res) {
                    is SignInResult.Failed -> _signupState.update {
                        it.copy(
                            isSigningUp = false,
                            error = res.message
                        )
                    }

                    SignInResult.Loading -> _signupState.update {
                        it.copy(
                            isSigningUp = true,
                            error = ""
                        )
                    }

                    SignInResult.Success -> _signupState.update {

                        it.copy(
                            isSignUpSuccessful = true,
                            isSigningUp = false,
                            error = ""
                        )
                    }
                }
            }
        }
    }

    fun onSignInResult(result: SignInResponse) {
        _signInState.update {
            currentUser.value = result.data
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.error
            )
        }
    }

    suspend fun forgotPassword(email: String): Boolean = try {
        auth.sendPasswordResetEmail(email).await()
        true
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        e.printStackTrace()
        false
    }

    fun signOut() {
        auth.signOut()
        currentUser.value = null
        resetSignUpState()
        resetSignInState()
    }

    fun resetSignUpState() {
        _signupState.update { SignupState() }
    }

    fun resetSignInState() {
        _signInState.update { SignInState() }
    }

    suspend fun verifyUsername(username: String): String {
        val isUsernameValid = SignInUtils.isUsernameValid(username)
        if (isUsernameValid.isNotEmpty()) return isUsernameValid

        val isUsernameExist =
            firebaseRepository.isUsernameAlreadyExists(username = username, userList = null)

        if (isUsernameExist) return "User already exists with provided email"

        return ""
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            val user = firebaseRepository.getUser(uid)
            currentUser.update { user }
        }
    }


}

sealed interface SignInEvent {
    data class SignUp(
        val username: String,
        val email: String,
        val password: String,
        val imageUri: Uri?,
        val bioLink: String?
    ) : SignInEvent

    data class OnSigningInWithGoogle(
        val intent: Intent,
        val username: String
    ) : SignInEvent

}
