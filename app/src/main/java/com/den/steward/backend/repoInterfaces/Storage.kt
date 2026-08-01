package com.den.steward.backend.repoInterfaces

import com.google.firebase.firestore.FirebaseFirestore

interface Storage {
    val firestore: FirebaseFirestore;
}