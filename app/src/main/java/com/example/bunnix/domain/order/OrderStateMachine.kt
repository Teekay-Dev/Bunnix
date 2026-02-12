package com.example.bunnix.domain.order

import com.example.bunnix.domain.user.UserMode

class OrderStateMachine {

    fun canTransition(
        current: OrderStatus,
        next: OrderStatus,
        actorMode: UserMode
    ): Boolean {

        return when (actorMode) {

            UserMode.CUSTOMER -> {
                current == OrderStatus.PLACED &&
                        next == OrderStatus.PAYMENT_AWAITING_CONFIRMATION
            }

            UserMode.VENDOR -> {
                when (current) {
                    OrderStatus.PAYMENT_AWAITING_CONFIRMATION ->
                        next == OrderStatus.PAYMENT_CONFIRMED

                    OrderStatus.PAYMENT_CONFIRMED ->
                        next == OrderStatus.PROCESSING

                    OrderStatus.PROCESSING ->
                        next == OrderStatus.SHIPPED

                    OrderStatus.SHIPPED ->
                        next == OrderStatus.DELIVERED

                    else -> false
                }
            }
        }
    }
}
