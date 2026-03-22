package com.example.bunnix.vendorUI.screens.vendor.products

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.bunnix.ui.theme.*
import com.example.bunnix.viewmodel.ProductsViewModel
import kotlinx.coroutines.delay
import androidx.compose.animation.scaleIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    var productName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    val categories = listOf(
        "Electronics", "Fashion", "Home & Garden", "Beauty & Health",
        "Sports & Outdoors", "Toys & Games", "Books", "Food & Beverages", "Other"
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(successMessage) {
        if (successMessage?.isNotEmpty() == true) {
            delay(1500)
            viewModel.clearMessages()
            navController.navigate("vendor/inventory") {
                popUpTo("vendor/product/add") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Add New Product",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = OrangePrimaryModern,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = LightGrayBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ===== HEADER INFO CARD =====
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = OrangePrimaryModern.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(OrangePrimaryModern.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = OrangePrimaryModern,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Add Product to Inventory",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            Text(
                                "Fill in all required fields marked with *",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // ===== IMAGE UPLOAD SECTION =====
                Text(
                    "Product Image *",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedImageUri != null) Color.Transparent else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Product Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Edit Badge
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(16.dp),
                                color = OrangePrimaryModern,
                                shape = RoundedCornerShape(12.dp),
                                shadowElevation = 4.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        "Change",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    OrangePrimaryModern.copy(alpha = 0.1f),
                                                    OrangePrimaryModern.copy(alpha = 0.2f)
                                                )
                                            ),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Upload Image",
                                        tint = OrangePrimaryModern,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Tap to upload product image",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "JPG, PNG • 1:1 ratio • Max 5MB",
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // ===== PRODUCT DETAILS SECTION =====
                Text(
                    "Product Information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Product Name
                        OutlinedTextField(
                            value = productName,
                            onValueChange = { productName = it },
                            label = { Text("Product Name *") },
                            placeholder = { Text("e.g., iPhone 15 Pro Max") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangePrimaryModern,
                                focusedLabelColor = OrangePrimaryModern
                            ),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    tint = OrangePrimaryModern
                                )
                            },
                            singleLine = true
                        )

                        // Description
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description *") },
                            placeholder = { Text("Describe your product in detail...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            maxLines = 6,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangePrimaryModern,
                                focusedLabelColor = OrangePrimaryModern
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // ===== PRICING & STOCK =====
                Text(
                    "Pricing & Stock",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Price
                            OutlinedTextField(
                                value = price,
                                onValueChange = { price = it },
                                label = { Text("Price *") },
                                placeholder = { Text("0.00") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OrangePrimaryModern,
                                    focusedLabelColor = OrangePrimaryModern
                                ),
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = {
                                    Text(
                                        "₦",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OrangePrimaryModern,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                },
                                singleLine = true
                            )

                            // Quantity
                            OutlinedTextField(
                                value = quantity,
                                onValueChange = { quantity = it },
                                label = { Text("Stock *") },
                                placeholder = { Text("0") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OrangePrimaryModern,
                                    focusedLabelColor = OrangePrimaryModern
                                ),
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Inventory,
                                        contentDescription = null,
                                        tint = OrangePrimaryModern
                                    )
                                },
                                singleLine = true
                            )
                        }

                        // Category Dropdown
                        ExposedDropdownMenuBox(
                            expanded = showCategoryDropdown,
                            onExpandedChange = { showCategoryDropdown = it }
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category *") },
                                placeholder = { Text("Select category") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (showCategoryDropdown) Icons.Default.KeyboardArrowUp
                                        else Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Dropdown"
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null,
                                        tint = OrangePrimaryModern
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = OrangePrimaryModern,
                                    focusedLabelColor = OrangePrimaryModern
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = showCategoryDropdown,
                                onDismissRequest = { showCategoryDropdown = false }
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            category = cat
                                            showCategoryDropdown = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Label,
                                                contentDescription = null,
                                                tint = OrangePrimaryModern
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // ===== ADD BUTTON =====
                Button(
                    onClick = {
                        if (selectedImageUri != null) {
                            viewModel.addProduct(
                                name = productName,
                                description = description,
                                price = price.toDoubleOrNull() ?: 0.0,
                                quantity = quantity.toIntOrNull() ?: 0,
                                category = category,
                                imageUri = selectedImageUri!!
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading && productName.isNotBlank() &&
                            description.isNotBlank() && price.isNotBlank() &&
                            quantity.isNotBlank() && category.isNotBlank() &&
                            selectedImageUri != null,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimaryModern,
                        disabledContainerColor = TextSecondary.copy(alpha = 0.3f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Add Product to Inventory",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }

            // ===== UPLOAD PROGRESS OVERLAY =====
            AnimatedVisibility(
                visible = uploadProgress > 0f && uploadProgress < 1f,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .padding(32.dp)
                            .width(300.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = uploadProgress,
                                    modifier = Modifier.size(80.dp),
                                    color = OrangePrimaryModern,
                                    strokeWidth = 6.dp,
                                    trackColor = OrangePrimaryModern.copy(alpha = 0.1f)
                                )
                                Text(
                                    text = "${(uploadProgress * 100).toInt()}%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = OrangePrimaryModern
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Uploading Product",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Please wait while we add your product...",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // ===== SUCCESS ANIMATION =====
            AnimatedVisibility(
                visible = successMessage?.isNotEmpty() == true,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .padding(32.dp)
                            .width(300.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Success checkmark with animation
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(SuccessGreen.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(64.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                "Product Added!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "Taking you to inventory...",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}


// ===== PREVIEWS =====

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true, name = "Add Product - Empty State")
@Composable
fun AddProductScreenEmptyPreview() {
    BunnixTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Add New Product",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = OrangePrimaryModern,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            containerColor = LightGrayBg
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = OrangePrimaryModern.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(OrangePrimaryModern.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = OrangePrimaryModern,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Add Product to Inventory",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            Text(
                                "Fill in all required fields marked with *",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Image Upload Section
                Text(
                    "Product Image *",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                OrangePrimaryModern.copy(alpha = 0.1f),
                                                OrangePrimaryModern.copy(alpha = 0.2f)
                                            )
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Upload Image",
                                    tint = OrangePrimaryModern,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Tap to upload product image",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "JPG, PNG • 1:1 ratio • Max 5MB",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                // Product Information
                Text(
                    "Product Information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Product Name *") },
                            placeholder = { Text("e.g., iPhone 15 Pro Max") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    tint = OrangePrimaryModern
                                )
                            }
                        )

                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Description *") },
                            placeholder = { Text("Describe your product in detail...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Add Product - Filled State", heightDp = 800)
@Composable
fun AddProductScreenFilledPreview() {
    BunnixTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGrayBg)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Filled form fields
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = "Wireless Headphones Pro",
                        onValueChange = {},
                        label = { Text("Product Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = OrangePrimaryModern)
                        }
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = "45000",
                            onValueChange = {},
                            label = { Text("Price *") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Text("₦", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OrangePrimaryModern)
                            }
                        )
                        OutlinedTextField(
                            value = "15",
                            onValueChange = {},
                            label = { Text("Stock *") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Inventory, contentDescription = null, tint = OrangePrimaryModern)
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryModern)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Add Product to Inventory", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}