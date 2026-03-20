package com.example.bunnix.data.repository

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.domain.repository.VendorRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VendorRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : VendorRepository {

    override suspend fun getVendorProfile(vendorId: String): AuthResult<VendorProfile> {
        return try {
            val snapshot = firestore.collection("vendorProfiles")
                .document(vendorId)
                .get()
                .await()
            val vendor = snapshot.toObject(VendorProfile::class.java)
            if (vendor != null) AuthResult.Success(vendor)
            else AuthResult.Error("Vendor not found")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to fetch vendor")
        }
    }

    override suspend fun updateVendorProfile(
        vendorId: String,
        updates: Map<String, Any>
    ): AuthResult<Unit> {
        return try {
            firestore.collection("vendorProfiles")
                .document(vendorId)
                .update(updates)
                .await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to update vendor")
        }
    }

    override suspend fun getAllVendors(): AuthResult<List<VendorProfile>> {
        return try {
            val snapshot = firestore.collection("vendorProfiles")
                .get()
                .await()
            val vendors = snapshot.toObjects(VendorProfile::class.java)
            AuthResult.Success(vendors)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to fetch vendors")
        }
    }
}