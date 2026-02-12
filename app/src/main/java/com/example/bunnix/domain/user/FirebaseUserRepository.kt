package com.example.bunnix.domain.user


import com.example.bunnix.data.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun register(email: String, password: String): String {
        val result = firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()

        return result.user?.uid
            ?: throw Exception("Registration failed")
    }

    override suspend fun login(email: String, password: String): String {
        val result = firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()

        return result.user?.uid
            ?: throw Exception("Login failed")
    }

    override fun currentUserUid(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}
