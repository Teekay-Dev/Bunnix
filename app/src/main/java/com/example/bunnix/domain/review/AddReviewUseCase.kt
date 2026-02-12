package com.example.bunnix.domain.review

import com.example.bunnix.data.auth.AuthManager
import com.example.bunnix.database.models.Review
import com.example.bunnix.domain.order.OrderRepository
import com.example.bunnix.domain.order.OrderStatus
import com.example.bunnix.model.data.auth.AuthManager
import com.example.bunnix.model.domain.model.Review
import java.util.UUID


class AddReviewUseCase(
    private val auth: AuthManager,
    private val reviewRepo: ReviewRepository,
    private val orderRepo: OrderRepository
) {
    suspend fun execute(orderId: String, rating: Int, comment: String): Result<Review> {
        val uid = auth.currentUserUid()

        // Fetch the order
        val order = orderRepo.getById(orderId)
            ?: return Result.failure(Exception("Order not found"))

        // Validate customer ownership
        if (order.customerId != uid) return Result.failure(Exception("Cannot review this order"))

        // Ensure order is completed
        if (order.status != OrderStatus.DELIVERED) return Result.failure(Exception("Order not completed"))

        // Check if already reviewed
        if (reviewRepo.hasReviewed(orderId, uid)) return Result.failure(Exception("Already reviewed"))

        // Create review
        val review = Review(
            id = UUID.randomUUID().toString(),
            reviewerId = uid,
            vendorId = order.vendorId,
            orderId = orderId,
            rating = rating,
            comment = comment
        )

        val saved = reviewRepo.addReview(review)
        return Result.success(saved)
    }
}
