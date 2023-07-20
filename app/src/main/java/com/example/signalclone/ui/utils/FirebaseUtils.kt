package com.example.signalclone.ui.utils

sealed interface SignInResult{
    object Loading:SignInResult
    object Success:SignInResult
    data class Failed(val message:String):SignInResult
}