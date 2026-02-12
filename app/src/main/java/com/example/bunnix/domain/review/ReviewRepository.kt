package com.example.bunnix.domain.review

import com.example.bunnix.database.models.Review

interface ReviewRepository {
    suspend fun addReview(review: Review): Review
    suspend fun hasReviewed(orderId: String, reviewerId: String): Boolean
}