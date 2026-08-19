// Glory be to the LORD GOD of hosts
package com.den.steward

import com.den.steward.backend.services.service.Account
import com.den.steward.backend.services.service.Storage
import com.den.steward.backend.services.AccountService
import com.den.steward.backend.services.StorageService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.LocalCacheSettings
import com.google.firebase.firestore.PersistentCacheSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StewardModule {

    // Changed to your computer's local IP to allow physical device connection
    private const val EMULATOR_IP = "192.168.10.141"

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        val auth = FirebaseAuth.getInstance()
        if (BuildConfig.DEBUG) {
            // FIXED PORT: Corrected from 9090 to 9099
            auth.useEmulator(EMULATOR_IP, 9099)
        }
        return auth
    }

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()

        // Enable persistent disk caching even in debug mode to ensure diagram
        // data survives app restarts and local synchronization delays.
        val cacheSettings: LocalCacheSettings = PersistentCacheSettings.newBuilder()
            .setSizeBytes(100 * 1024 * 1024) // 100 MB cache
            .build()

        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(cacheSettings)
            .build()

        firestore.firestoreSettings = settings
        FirebaseFirestore.setLoggingEnabled(true)

        if (BuildConfig.DEBUG) {
            // FIXED CONNECTION: Calling directly on the instance inside the correct lifecycle order
            firestore.useEmulator(EMULATOR_IP, 8080)
        }

        return firestore
    }

    @Singleton
    @Provides
    fun provideStorageService(firestore: FirebaseFirestore): Storage {
        return StorageService(firestore)
    }

    @Singleton
    @Provides
    fun provideAccountService(auth: FirebaseAuth): Account {
        return AccountService(auth)
    }
}