package com.example.bunnix.domain.switchCase

import com.example.bunnix.data.auth.AuthManager
import com.example.bunnix.domain.user.UserMode
import com.example.bunnix.domain.user.UserModeManager
import com.example.bunnix.domain.vendor.VendorProfileRepository
import com.example.bunnix.domain.vendor.VendorRepository

class SwitchModeUseCase(
    private val auth: AuthManager,
    private val userModeManager: UserModeManager,
    private val vendorRepo: VendorProfileRepository,
    private val vendorRepository: VendorRepository
) {
    suspend fun execute(): Result<UserMode> {
        val uid = auth.currentUserId()
        return when (userModeManager.getMode()) {
            UserMode.CUSTOMER -> {
                if (!vendorRepo.exists(uid))
                    Result.failure(IllegalStateException("Create vendor profile first"))
                else Result.success(userModeManager.switchMode())
            }
            UserMode.VENDOR -> Result.success(userModeManager.switchMode())
        }
    }

    suspend fun switchToVendor(userId: String): UserMode {
        val vendor = vendorRepository.getVendorByUserId(userId)
            ?: throw IllegalStateException("You need to have a Business Account")

        return UserMode.VENDOR
    }

    fun switchToCustomer(): UserMode = UserMode.CUSTOMER

//    fun onVendorSignupClicked() {
//        _navigation.emit(NavigateToVendorOnboarding)
//    }

}
