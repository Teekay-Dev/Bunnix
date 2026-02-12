package com.example.bunnix


import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Bunnix.
 *
 * @HiltAndroidApp triggers Hilt's code generation and sets up dependency injection.
 * This MUST be added to AndroidManifest.xml
 */
@HiltAndroidApp
class BunnixApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Application initialization here
    }
}
