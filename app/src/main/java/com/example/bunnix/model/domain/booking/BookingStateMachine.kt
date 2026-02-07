package com.example.bunnix.model.domain.booking

import com.example.bunnix.model.domain.user.UserMode

class BookingStateMachine {

    fun canTransition(
        current: BookingStatus,
        next: BookingStatus,
        actorMode: UserMode
    ): Boolean {

        return when (actorMode) {

            UserMode.CUSTOMER -> {
                current == BookingStatus.REQUESTED &&
                        next == BookingStatus.PAYMENT_AWAITING_CONFIRMATION
            }

            UserMode.VENDOR -> {
                when (current) {
                    BookingStatus.PAYMENT_AWAITING_CONFIRMATION ->
                        next == BookingStatus.PAYMENT_CONFIRMED

                    BookingStatus.PAYMENT_CONFIRMED ->
                        next == BookingStatus.VENDOR_ACCEPTED

                    BookingStatus.VENDOR_ACCEPTED ->
                        next == BookingStatus.IN_PROGRESS

                    BookingStatus.IN_PROGRESS ->
                        next == BookingStatus.COMPLETED

                    else -> false
                }
            }
        }
    }
}
