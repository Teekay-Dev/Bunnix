package com.example.bunnix.database.firebase.collections

import com.example.bunnix.database.config.FirebaseConfig
import com.example.bunnix.database.models.Category
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object CategoryCollection {

    private val collection = FirebaseConfig.firestore.collection(FirebaseConfig.Collections.CATEGORIES)

    // GET ALL CATEGORIES
    fun getAllCategories(): Flow<List<Category>> = callbackFlow {
        val listener = collection
            .orderBy("displayOrder")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val categories = snapshot?.toObjects(Category::class.java) ?: emptyList()
                trySend(categories)
            }
        awaitClose { listener.remove() }
    }

    // GET CATEGORIES BY TYPE
    fun getCategoriesByType(type: String): Flow<List<Category>> = callbackFlow {
        val listener = collection
            .whereEqualTo("type", type)
            .orderBy("displayOrder")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val categories = snapshot?.toObjects(Category::class.java) ?: emptyList()
                trySend(categories)
            }
        awaitClose { listener.remove() }
    }
}