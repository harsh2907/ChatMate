package com.example.signalclone.domain.models.user

import com.google.firebase.auth.FirebaseUser


data class User(
    val userId: String="",
    val username: String="",
    val userEmail: String="",
    val userProfileImage: String? = null,
    val userBio: String = "Hey there! I'm using ChatMate🔥",
    val userBioLink: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)

fun Map<String, Any>.toUser(): User {
    return User(
        userId = this["userId"]!! as String,
        username = this["username"]!! as String,
        userEmail = this["userEmail"]!! as String,
        userProfileImage = this["userProfileImage"] as String?,
        userBio = this["userBio"]!! as String,
        userBioLink = this["userBioLink"] as String?,
        addedAt = this["addedAt"]!! as Long
    )
}


