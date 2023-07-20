package com.example.signalclone.domain.repository

import android.net.Uri
import coil.network.HttpException
import com.example.signalclone.domain.models.user.User
import com.example.signalclone.domain.models.user.toUser
import com.example.signalclone.ui.utils.SignInResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException


class FirebaseRepository {

    private val firestore = Firebase.firestore
    private val storageRef = Firebase.storage.reference
    private val userRef = firestore.collection("users")
    private val auth = Firebase.auth

    suspend fun uploadProfileImage(userId: String, uri: Uri) {
        try {
            storageRef.child(userId).putFile(uri).await()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
        }
    }

    suspend fun getImage(userId:String ): Uri = storageRef.child(userId).downloadUrl.await()


    suspend fun addUser(user: User): Flow<SignInResult> = flow {
        emit(SignInResult.Loading)
        try {
            userRef.document(user.userId).set(user).await()
            emit(SignInResult.Success)
        } catch (e: CancellationException) {
            emit(SignInResult.Failed(e.message ?: "oops, an unknown error occurred"))
            e.printStackTrace()
            println("Add user Error: " + e.message)
        }
    }

    suspend fun getUser(userId: String): User? {
        return userRef.document(userId).get().await().data?.toUser()
    }

    private suspend fun getAllUsers(): List<User?> {
        return userRef.get().await().documents.map { doc ->
            doc.toObject<User>()
        }
    }

    suspend fun signUpWithEmail(
        username: String,
        email: String,
        password: String
    ): Flow<SignInResult> = flow {
        emit(SignInResult.Loading)
        try {
            val userList = getAllUsers()
            if (isEmailAlreadyExists(userList, email)) {
                emit(SignInResult.Failed("Email is already in use."))
                return@flow
            }

            if (isUsernameAlreadyExists(userList, username)) {
                emit(SignInResult.Failed("Username is already in use."))
                return@flow
            }
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
            if (user != null)
                emit(SignInResult.Success)
        } catch (e: HttpException) {
            emit(SignInResult.Failed(message = "Oops, something went wrong"))
        } catch (e: IOException) {
            emit(SignInResult.Failed(message = "Couldn't reach server check your internet connection"))
        } catch (e: Exception) {
            emit(SignInResult.Failed(message = e.message ?: "oops, an unknown error occurred"))
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Flow<SignInResult> = flow {
        emit(SignInResult.Loading)
        try {
            val user = auth.signInWithEmailAndPassword(email, password).await().user
            if (user != null) emit(SignInResult.Success)
            else
                emit(SignInResult.Failed("No user exist with this email. Please Sign up to make a new account"))

        } catch (e: HttpException) {
            emit(SignInResult.Failed(message = "Oops, something went wrong"))
        } catch (e: IOException) {
            emit(SignInResult.Failed(message = "Couldn't reach server check your internet connection"))
        } catch (e: Exception) {
            emit(SignInResult.Failed(message = e.message ?: "oops, an unknown error occurred"))
        }
    }

    private fun isEmailAlreadyExists(userList: List<User?>, email: String): Boolean {
        //If email already exists return true
        return userList.count { it?.userEmail == email } != 0
    }



     suspend fun isUsernameAlreadyExists(userList: List<User?>?=null, username: String): Boolean {
        //If username already exists return true
        //val list = userList ?:
             return getAllUsers().count { it?.username == username } != 0
    }

}

