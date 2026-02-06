package com.example.bunnix.model.domain.user

class UserModeManager {

    private var currentMode: UserMode = UserMode.CUSTOMER

    fun getMode(): UserMode = currentMode

    fun switchMode(): UserMode {
        currentMode = when (currentMode) {
            UserMode.CUSTOMER -> UserMode.VENDOR
            UserMode.VENDOR -> UserMode.CUSTOMER
        }
        return currentMode
    }
}
