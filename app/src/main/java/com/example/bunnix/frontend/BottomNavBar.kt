package com.example.bunnix.frontend

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

import com.example.bunnix.backend.Routes

@Composable
fun BottomNavBar(navController: NavController) {

    NavigationBar {

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(Routes.Home)
            },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(Routes.Cart)
            },
            icon = { Icon(Icons.Default.ShoppingCart, null) },
            label = { Text("Cart") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(Routes.Chat)
            },
            icon = { Icon(Icons.Default.Chat, null) },
            label = { Text("Chats") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(Routes.Notifications)
            },
            icon = { Icon(Icons.Default.Notifications, null) },
            label = { Text("Alerts") }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(Routes.Profile)
            },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Profile") }
        )
    }
}
