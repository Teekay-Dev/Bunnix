package com.example.bunnix.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpCenter
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bunnix.model.Vendor
import com.example.bunnix.model.VendorViewModel

@Composable
fun VendorProfileScreen(
    navController: NavController,
    viewModel: VendorViewModel = viewModel(),
    vendor: Vendor // Pass your specific Vendor data class here
) {
    Scaffold(
        bottomBar = {BunnixBottomNavigation(navController) } //RED on VendorBottomNav
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F8F8))
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp).background(Color(0xFFF2711C))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = Color(0xFFFFE0B2)) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.padding(16.dp), tint = Color(0xFFF2711C))
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(vendor.businessName, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold) //RED on vendor.name
                    Text(vendor.email, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            // --- VENDOR TOGGLE ---
            VendorModeCard(viewModel) //red on ()

            // --- DYNAMIC CONTENT FROM YOUR DATA CLASS ---
            SettingsGroup(title = "Business Information") { //red on SettingsGroup
                SettingsItem(icon = Icons.Default.Person, label = "Owner: ${vendor.firstName} ${vendor.surName}") //red on SettingsItem
                SettingsItem(icon = Icons.Default.Phone, label = vendor.phone) //red on SettingsItem
                SettingsItem(icon = Icons.Default.Badge, label = "Role: ${vendor.role}") //red on SettingsItem
            }

            SettingsGroup(title = "Support") { ////red on SettingsGroup
                SettingsItem(icon = Icons.Default.HelpCenter, label = "Help Center")//red on SettingsItem
                SettingsItem(icon = Icons.Default.Shield, label = "Privacy & Security") //red on SettingsItem
            }

            LogoutButton(onClick = {
                // Add your actual logout logic here (e.g., Supabase SignOut)
                navController.navigate("login_screen") {
                    popUpTo(0) // Clear backstack
                }
            })
        }
    }
}

@Composable
fun VendorModeCard(viewModel: VendorViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-30).dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2711C)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Vendor Mode", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Manage your business", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
            // Real Toggle Logic
            Switch(
                checked = viewModel.isVendorModeEnabled,
                onCheckedChange = { viewModel.toggleVendorMode(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White)
            )
        }
    }
}

// Missing Helper Components
@Composable
fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text(title, fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column { content() }
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, label: String) {
    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color(0xFFF2711C), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 16.sp)
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "Logout",
            color = Color.Red,
            fontWeight = FontWeight.Bold
        )
    }
}