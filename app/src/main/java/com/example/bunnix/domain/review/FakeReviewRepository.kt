package com.example.bunnix.domain.review

import com.example.bunnix.database.models.Review
import com.example.bunnix.model.domain.model.Review

class FakeReviewRepository : ReviewRepository {
    private val reviews = mutableListOf<Review>()

    override suspend fun addReview(review: Review): Review {
        if (reviews.any { it.orderId == review.orderId && it.reviewerId == review.reviewerId }) {
            throw IllegalStateException("Already reviewed")
        }
        reviews.add(review)
        return review
    }

    override suspend fun hasReviewed(orderId: String, reviewerId: String): Boolean {
        return reviews.any { it.orderId == orderId && it.reviewerId == reviewerId }
    }
}