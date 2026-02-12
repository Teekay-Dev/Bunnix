package com.example.bunnix.domain.user


import com.example.bunnix.database.models.User


interface UserRepository {
    suspend fun createUser(user: User)
    suspend fun getUser(uid: String): User?
}
