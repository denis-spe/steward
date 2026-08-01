package com.den.steward.backend.repos

import com.den.steward.backend.repoInterfaces.Storage
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class StorageService @Inject constructor(
    override val firestore: FirebaseFirestore
) : Storage {

    companion object {
        private const val DATASET_COLLECTION = "Storage"
        private const val TAG = "StorageService"
    }

    private val docRef = firestore
        .collection(DATASET_COLLECTION)
}