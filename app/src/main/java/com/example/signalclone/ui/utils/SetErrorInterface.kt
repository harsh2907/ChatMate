package com.example.signalclone.ui.utils

import androidx.compose.runtime.Composable

interface SetErrorInterface {

    @Composable
    fun ShowError(error:Exception)
}