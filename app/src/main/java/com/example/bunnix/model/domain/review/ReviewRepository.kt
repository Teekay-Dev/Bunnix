package com.example.bunnix.model.domain.review

import com.example.bunnix.model.domain.model.Review

interface ReviewRepository {
    suspend fun addReview(review: Review): Review
    suspend fun hasReviewed(orderId: String, reviewerId: String): Boolean
}