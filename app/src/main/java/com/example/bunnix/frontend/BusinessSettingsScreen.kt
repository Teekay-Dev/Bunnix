<<<<<<< HEAD

=======
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
import com.example.bunnix.model.VendorViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.bunnix.model.Vendor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessSettingsScreen(
    navController: NavController,
    vendor: Vendor,
    viewModel: VendorViewModel = viewModel()
) {
    var bizName by remember { mutableStateOf(vendor.businessName ?: "") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Settings", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            BunnixTextField(value = bizName, onValueChange = { bizName = it }, label = "Business Name")
            Spacer(modifier = Modifier.height(16.dp))

            BunnixTextField(value = address, onValueChange = { address = it }, label = "Shop Address")
            Spacer(modifier = Modifier.height(16.dp))

            BunnixTextField(value = phone, onValueChange = { phone = it }, label = "Contact Phone", isNumber = true)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.updateVendorProfile(vendor.id, bizName, address, phone)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2711C)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
>>>>>>> 3e8a2de235349208f7d0ce387a237c0a485cf30a
