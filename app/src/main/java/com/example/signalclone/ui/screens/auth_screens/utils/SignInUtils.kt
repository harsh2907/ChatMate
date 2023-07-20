package com.example.signalclone.ui.screens.auth_screens.utils

import android.net.Uri
import com.example.signalclone.domain.models.user.User

data class SignInResponse(
    val data: User? = null,
    val error: String? = null
)

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val isLoading: Boolean = false
)

data class SignupState(
    val imageUri: Uri? = null,
    val isImageLoading: Boolean = false,
    val isSigningUp: Boolean = false,
    val error: String = "",
    val isSignUpSuccessful: Boolean = false
)

enum class SignInAction {
    SIGNIN, SIGNUP
}

object SignInUtils {

    private val passRegex = Regex("^(?=.{8,32}\$)(?!-).*\$")

    fun verifySignUpDetails(email: String, username: String, password: String): String {
        val usernameInvalidMessage = isUsernameValid(username)
        return when {
            email.isBlank() -> "Email can't be empty"
            //To check valid email pattern
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() -> "Please provide a valid email address"

            (password.length < 8 || password.length > 30) && password.isNotBlank() -> {
                "The password must be at least 8 characters long and no more than 30 characters long."
            }
            !password.matches(passRegex)-> "Invalid password. Please enter a valid password"


             usernameInvalidMessage.isNotEmpty() -> usernameInvalidMessage

            else -> ""
        }
    }

    fun verifySignInDetails(email: String, password: String): String {
        return when {
            email.isBlank() -> "Email can't be empty"
            //To check valid email pattern
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches() -> "Please provide a valid email address"

            (password.length < 8 || password.length > 30) && password.isNotBlank() -> {
                "The password must be at least 8 characters long and no more than 30 characters long."
            }
            !password.matches(passRegex)-> "Invalid password. Please enter a valid password"

            else -> ""
        }
    }

    fun isUsernameValid(username: String): String {
        val regex = Regex("[a-zA-Z0-9_.]+")
        return when {

            username.isEmpty() || username.isBlank()-> "Username can't be empty. Please provide a username."

            (username.length < 3 || username.length > 30) -> {
                 "The username must be at least 3 characters long and no more than 30 characters long."
            }

            !regex.matches(username) -> {
                 "The username must only contain alphanumeric characters and underscores."
            }


            // The username must not start or end with an underscore.
            username.startsWith("_") || username.endsWith("_") -> {
                 "The username must not start or end with an underscore."
            }

            else -> ""
        }
    }

    fun isEmailValid(email:String):String = when{
        email.isBlank() -> "Email can't be empty"
        //To check valid email pattern
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches() -> "Please provide a valid email address"

        else -> ""
    }

}
