package com.example.bunnix.database.firebase.collections

import com.example.bunnix.database.config.FirebaseConfig
import com.example.bunnix.database.models.Service
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object ServiceCollection {

    private val collection = FirebaseConfig.firestore.collection(FirebaseConfig.Collections.SERVICES)

    // GET ALL SERVICES
    fun getAllServices(): Flow<List<Service>> = callbackFlow {
        val listener = collection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val services = snapshot?.toObjects(Service::class.java) ?: emptyList()
                trySend(services)
            }
        awaitClose { listener.remove() }
    }

    // GET SERVICES BY VENDOR
    fun getServicesByVendor(vendorId: String): Flow<List<Service>> = callbackFlow {
        val listener = collection
            .whereEqualTo("vendorId", vendorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val services = snapshot?.toObjects(Service::class.java) ?: emptyList()
                trySend(services)
            }
        awaitClose { listener.remove() }
    }

    // ADD SERVICE
    suspend fun addService(service: Service): Result<String> {
        return try {
            val docRef = collection.add(service).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE SERVICE
    suspend fun updateService(serviceId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            collection.document(serviceId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE SERVICE
    suspend fun deleteService(serviceId: String): Result<Unit> {
        return try {
            collection.document(serviceId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}