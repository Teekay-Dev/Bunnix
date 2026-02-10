package com.example.bunnix.frontend

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*


@Composable
fun TrackOrderButton(destinationAddress: String) {
    val context = LocalContext.current

    Button(onClick = {
        // Build the URI for directions
        // saddr: source (optional, defaults to current location)
        // daddr: destination address or lat,lng
        val gmmIntentUri = Uri.parse("google.navigation:q=${Uri.encode(destinationAddress)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            context.startActivity(mapIntent)
        } catch (e: ActivityNotFoundException) {
            // Fallback if Google Maps isn't installed
            val playStoreIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps")
            )
            context.startActivity(playStoreIntent)
        }
    }) {
        Text("Track My Order")
    }
}