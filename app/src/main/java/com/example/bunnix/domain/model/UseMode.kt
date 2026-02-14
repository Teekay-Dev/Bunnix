package com.example.bunnix.domain.model


/**
 * User operating mode in the app.
 * Users can switch between Customer and Vendor modes.
 *
 * Note: A user's mode is LOCAL STATE only.
 * The isVendor flag in Firestore determines if they CAN be a vendor.
 * This enum determines which mode they're CURRENTLY operating in.
 */
enum class UserMode {
    /**
     * Customer mode - Browse and purchase products/services
     */
    CUSTOMER,

    /**
     * Vendor mode - Manage products, services, and orders
     */
    VENDOR;

    companion object {
        /**
         * Get display name for UI
         */
        fun UserMode.displayName(): String {
            return when (this) {
                CUSTOMER -> "Customer"
                VENDOR -> "Vendor"
            }
        }

        /**
         * Toggle between modes
         */
        fun UserMode.toggle(): UserMode {
            return when (this) {
                CUSTOMER -> VENDOR
                VENDOR -> CUSTOMER
            }
        }
    }
}
