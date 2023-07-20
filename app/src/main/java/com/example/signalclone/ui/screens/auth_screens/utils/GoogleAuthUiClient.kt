package com.example.signalclone.ui.screens.auth_screens.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.compose.runtime.collectAsState
import com.example.signalclone.R
import com.example.signalclone.domain.models.user.User
import com.example.signalclone.domain.repository.FirebaseRepository
import com.example.signalclone.ui.screens.auth_screens.AuthViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import javax.inject.Inject

class GoogleAuthUiClient @Inject constructor(
    private val context: Context,
    private val oneTapClient: SignInClient,
    private val firebaseRepository: FirebaseRepository ,
) {
    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInIntent()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    private fun buildSignInIntent() = BeginSignInRequest.Builder()
        .setGoogleIdTokenRequestOptions(
            GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .build()
        ).setAutoSelectEnabled(true)
        .build()

    suspend fun getSignInResponseFromIntent(
        intent: Intent,
        action:SignInAction,
        username:String? = null,
        onSignUpSuccessful:( (User)->Unit )? = null
    ):SignInResponse{
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken,null)
        return try {
            val firebaseUser = auth.signInWithCredential(googleCredentials).await().user
                ?: return SignInResponse(
                    data = null,
                    error = "An unknown error occurred while signing in with google. " +
                            "Please try again later"
                )
                
            val user = firebaseRepository.getUser(firebaseUser.uid)
            when(action){
                SignInAction.SIGNIN -> {
                    //In case of signing in
                    if(user == null){
                        return SignInResponse(
                            data = null,
                            error = "No account found linked with this email. " +
                                    "To add a new account please sign up"
                        )
                    }
                   return SignInResponse(
                        data = user,
                         error = null
                    )
                }
                SignInAction.SIGNUP -> {
                    if(user != null){
                        return SignInResponse(
                            data = null,
                            error = "Account already linked with this email. Please login"
                        )
                    }
                    if(firebaseUser.email.isNullOrEmpty()){
                        return SignInResponse(
                            data = null,
                            error = "No email is linked with this email. Please try a different account"
                        )
                    }
                    Log.e("Signup", "sign up successful 1", )
                    onSignUpSuccessful?.let {
                        Log.e("Signup", "sign up successful 2", )

                        it(
                             firebaseUser.run {
                                User(
                                    userId = uid,
                                    username = username!!,
                                    userEmail = email!!,
                                    userProfileImage = photoUrl?.toString()
                                )
                            }
                        )
                    }
                    return SignInResponse(
                        data = null,
                        error = null
                    )
                }
            }
        }catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResponse(
                data = null,
                error = e.message
            )
        }
    }

    suspend fun signOut(){
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        }catch (e:Exception){
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

//    fun getSignedInUser():UserData? = auth.currentUser?.run {
//        UserData(
//            userId = uid,
//            username = displayName.toString(),
//            profilePictureUrl = photoUrl?.toString(),
//            email = email.toString()
//        )
//    }
}

