// Glory be to the LORD GOD of hosts
package com.den.steward

import com.den.steward.backend.services.AccountService
import com.den.steward.backend.services.StorageService
import com.den.steward.backend.services.service.Account
import com.den.steward.backend.services.service.Storage
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

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        val auth = FirebaseAuth.getInstance()
        val emulatorHost = BuildConfig.EMULATOR_HOST
        val emulatorPort = BuildConfig.FIREAUTH_PORT

        if (BuildConfig.USE_EMULATOR) {
            auth.useEmulator(emulatorHost, emulatorPort)
        }
        return auth
    }

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        val emulatorHost = BuildConfig.EMULATOR_HOST
        val emulatorPort = BuildConfig.FIRESTORE_PORT

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

        if (BuildConfig.USE_EMULATOR) {
            firestore.useEmulator(emulatorHost, emulatorPort)
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