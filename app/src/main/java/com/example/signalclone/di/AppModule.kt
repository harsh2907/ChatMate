package com.example.signalclone.di

import android.content.Context
import com.example.signalclone.domain.repository.FirebaseRepository
import com.example.signalclone.ui.screens.auth_screens.AuthViewModel
import com.example.signalclone.ui.screens.auth_screens.utils.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseRepository() = FirebaseRepository()

    @Provides
    @Singleton
    fun provideGoogleAuthUiClient(
        @ApplicationContext context: Context,
        firebaseRepository: FirebaseRepository,
    ):GoogleAuthUiClient{
        return GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context),
            firebaseRepository = firebaseRepository
        )
    }



}