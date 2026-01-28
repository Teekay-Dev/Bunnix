package com.example.bunnix.frontend

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bunnix.data.model.User
import com.example.bunnix.model.VendorViewModel

@Composable
fun BusinessSettingsScreen(navController: NavController, user: User, viewModel: VendorViewModel = viewModel()) {
    var bizName by remember { mutableStateOf(user.business_name ?: "") }
    var address by remember { mutableStateOf(user.business_address ?: "") }
    var phone by remember { mutableStateOf(user.phone ?: "") }

    Scaffold(
        topBar = { /* Row with Back button and "Business Settings" Title */ }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            BunnixTextField(value = bizName, onValueChange = { bizName = it }, label = "Business Name")
            Spacer(modifier = Modifier.height(16.dp))
            BunnixTextField(value = address, onValueChange = { address = it }, label = "Shop Address")
            Spacer(modifier = Modifier.height(16.dp))
            BunnixTextField(value = phone, onValueChange = { phone = it }, label = "Contact Phone", isNumber = true)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.updateVendorProfile(user.id, bizName, address, phone) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2711C))
            ) {
                Text("Save Changes")
            }
        }
    }
}