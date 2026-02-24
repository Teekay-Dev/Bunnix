package com.example.bunnix

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.bunnix.backend.Routes
import com.example.bunnix.data.CartData
import com.example.bunnix.database.models.Service
import com.example.bunnix.database.models.VendorProfile // IMPORT THE CORRECT MODEL
import com.example.bunnix.frontend.*
import com.example.bunnix.presentation.viewmodel.ProductViewModel
import com.example.bunnix.ui.theme.BunnixTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Modern Bunnix Color System
val OrangePrimaryModern = Color(0xFFFF6B35)
val OrangeLight = Color(0xFFFF8C61)
val OrangeSoft = Color(0xFFFFF0EB)
val TealAccent = Color(0xFF2EC4B6)
val SurfaceLight = Color(0xFFFAFAFA)
val TextPrimary = Color(0xFF1A1A2E)
val TextSecondary = Color(0xFF6B7280)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BunnixTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BunnixNavigation()
                }
            }
        }
    }
}

// FIXED: Use VendorProfile instead of separate Vendor class
// Extension function to create VendorProfile with UI-friendly fields
val vendorList = listOf(
    VendorProfile(
        vendorId = "1",
        businessName = "TechHub Store",
        category = "Tech",
        description = "Your one-stop electronics shop",
        coverPhotoUrl = "",
        rating = 4.8,
        totalReviews = 128,
        address = "2.3 km away",
        phone = "+234 123 456 7890",
        isAvailable = true
    ),
    VendorProfile(
        vendorId = "2",
        businessName = "Fashion Hub",
        category = "Fashion",
        description = "Latest trends and styles",
        coverPhotoUrl = "",
        rating = 4.5,
        totalReviews = 85,
        address = "1.5 km away",
        phone = "+234 987 654 3210",
        isAvailable = true
    ),
    VendorProfile(
        vendorId = "3",
        businessName = "Spa & Wellness",
        category = "Beauty",
        description = "Relax and rejuvenate",
        coverPhotoUrl = "",
        rating = 4.9,
        totalReviews = 200,
        address = "3.0 km away",
        phone = "+234 456 789 0123",
        isAvailable = true
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BunnixNavigation() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // Screens where BottomBar appears
    val bottomBarScreens = listOf(
        Routes.Home,
        Routes.Cart,
        Routes.Chat,
        Routes.Notifications,
        Routes.Profile
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = currentRoute in bottomBarScreens,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                ModernBottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Splash,
            modifier = Modifier.padding(padding)
        ) {
            // ===== AUTHENTICATION FLOW =====

            composable(Routes.Signup) {
                SignupScreen(
                    userPrefs = UserPreferences(LocalContext.current),
                    onLogin = {
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.Signup) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.Login) {
                LoginScreen(
                    onSignupClick = {
                        navController.navigate(Routes.Signup)
                    },
                    onLoginSuccess = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    }
                )
            }

            // ===== MAIN CUSTOMER FLOW =====
            composable(Routes.Home) {
                HomeScreen(
                    onVendorClick = { vendorId ->
                        navController.navigate("vendor_detail/$vendorId")
                    },
                    onBookServiceClick = {
                        navController.navigate(Routes.ServiceList)
                    },
                    onShopProductClick = {
                        navController.navigate(Routes.ProductList)
                    },
                    onSearchClick = { query ->
                        navController.navigate("search/${Uri.encode(query)}")
                    },

                    bottomBar = {
                        ModernBottomNavBar(
                            navController = navController,
                            currentRoute = Routes.Home
                        )
                    }

                )
            }

            // ===== VENDOR DETAIL =====
            composable(
                route = "vendor_detail/{vendorId}",
                arguments = listOf(
                    navArgument("vendorId") { type = NavType.StringType } // CHANGED to StringType
                )
            ) { backStackEntry ->
                val vendorId = backStackEntry.arguments?.getString("vendorId") ?: ""
                val selectedVendor = vendorList.find { it.vendorId == vendorId } ?: vendorList.first()

                VendorDetailScreen(
                    vendor = selectedVendor, // NOW IT MATCHES - both are VendorProfile
                    onBack = { navController.popBackStack() }
                )
            }

            // ===== SEARCH =====
            composable(
                route = "search/{query}",
                arguments = listOf(
                    navArgument("query") { type = NavType.StringType }
                )
            ) { entry ->
                val query = entry.arguments?.getString("query") ?: ""
                val productViewModel: ProductViewModel = hiltViewModel()
                val products by productViewModel.products.collectAsState()

                SearchScreen(
                    query = query,
                    products = products,
                    services = emptyList(),
                    onProductClick = { productId ->
                        navController.navigate("product_detail/$productId")
                    },
                    onServiceClick = { serviceName, price ->
                        // TODO: Navigate to service detail
                    }
                )
            }

            // ===== SERVICES & BOOKING =====
            composable(Routes.ServiceList) {
                ServiceListScreen(
                    onBack = { navController.popBackStack() },
                    onServiceClick = { service ->
                        navController.navigate("booking/${Uri.encode(service.serviceId)}")
                    }
                )
            }

            composable(
                route = "booking/{serviceId}",
                arguments = listOf(
                    navArgument("serviceId") { type = NavType.StringType }
                )
            ) { entry ->
                val serviceId = entry.arguments?.getString("serviceId") ?: ""

                val service = remember(serviceId) {
                    Service(
                        serviceId = serviceId,
                        vendorId = "v1",
                        vendorName = "Sample Vendor",
                        name = "Sample Service",
                        description = "Service description",
                        price = 5000.0,
                        duration = 60,
                        category = "General",
                        imageUrl = "",
                        availability = emptyList(),
                        totalBookings = 0,
                        rating = 4.5,
                        isActive = true
                    )
                }

                BookingScreen(
                    service = service,
                    onBack = { navController.popBackStack() },
                    onContinue = { bookingDetails ->
                        navController.navigate(
                            "checkout/service/${Uri.encode(serviceId)}/${bookingDetails.time}"
                        )
                    }
                )
            }

            // ===== PRODUCTS =====
            composable(Routes.ProductList) {
                val productViewModel: ProductViewModel = hiltViewModel()
                val products by productViewModel.products.collectAsState()

                ProductListScreen(
                    products = products,
                    onBack = { navController.popBackStack() },
                    onProductClick = { product ->
                        navController.navigate("product_detail/${product.productId}")
                    }
                )
            }

            composable(
                route = "product_detail/{productId}",
                arguments = listOf(
                    navArgument("productId") { type = NavType.StringType }
                )
            ) { entry ->
                val productId = entry.arguments?.getString("productId") ?: ""
                val productViewModel: ProductViewModel = hiltViewModel()
                val allProducts by productViewModel.products.collectAsState()
                val product = allProducts.find { it.productId == productId }

                if (product != null) {
                    ProductDetailsScreen(
                        product = product,
                        allProducts = allProducts,
                        onAddToCart = { addedProduct, quantity ->
                            CartData.addToCart(addedProduct, quantity)
                        },
                        onBuyNow = { buyProduct, quantity ->
                            navController.navigate("checkout/product/${buyProduct.productId}/$quantity")
                        },
                        onBack = { navController.popBackStack() },
                        onChatWithVendor = { vendorId ->
                            navController.navigate("chat_detail/vendor_$vendorId")
                        }
                    )
                } else {
                    ProductNotFoundScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // ===== CHECKOUT & PAYMENT =====
            composable(
                route = "checkout/{type}/{id}/{quantity}",
                arguments = listOf(
                    navArgument("type") { type = NavType.StringType },
                    navArgument("id") { type = NavType.StringType },
                    navArgument("quantity") { type = NavType.IntType }
                )
            ) { entry ->
                val type = entry.arguments?.getString("type") ?: ""
                val id = entry.arguments?.getString("id") ?: ""
                val quantity = entry.arguments?.getInt("quantity") ?: 1

                val checkoutItems: List<CheckoutItem> = when (type) {
                    "product" -> {
                        val productViewModel: ProductViewModel = hiltViewModel()
                        val products by productViewModel.products.collectAsState()
                        val product = products.find { it.productId == id }
                        product?.let {
                            listOf(
                                CheckoutItem(
                                    id = it.productId,
                                    name = it.name,
                                    imageUrl = it.imageUrls.firstOrNull() ?: "",
                                    price = it.price,
                                    quantity = quantity,
                                    variant = null
                                )
                            )
                        } ?: emptyList()
                    }
                    "service" -> {
                        listOf(
                            CheckoutItem(
                                id = id,
                                name = "Service Booking",
                                imageUrl = "",
                                price = 0.0,
                                quantity = 1,
                                variant = null
                            )
                        )
                    }
                    "cart" -> {
                        CartData.cartItems.map { product ->
                            CheckoutItem(
                                id = product.productId,
                                name = product.name,
                                imageUrl = product.imageUrls.firstOrNull() ?: "",
                                price = product.price,
                                quantity = 1,
                                variant = null
                            )
                        }
                    }
                    else -> emptyList()
                }

                val calculatedTotal = checkoutItems.sumOf { it.price * it.quantity }
                val isService = type == "service"
                val orderId = "ORD-${System.currentTimeMillis() % 100000}"

                CheckoutScreen(
                    items = checkoutItems,
                    isServiceBooking = isService,
                    onBack = { navController.popBackStack() },
                    onPaymentMethodSelect = { paymentMethod ->
                        navController.navigate("payment/$calculatedTotal/$orderId")
                    },
                    onApplyPromo = { promoCode ->
                        promoCode.uppercase() == "WELCOME10" ||
                                promoCode.uppercase() == "SAVE20" ||
                                promoCode.uppercase() == "BUNNIX50"
                    },
                    onPlaceOrder = {
                        navController.navigate(Routes.OrderSuccess)
                    },
                    total = calculatedTotal
                )
            }

            composable(
                route = "payment/{total}/{orderId}",
                arguments = listOf(
                    navArgument("total") { type = NavType.StringType },
                    navArgument("orderId") { type = NavType.StringType }
                )
            ) { entry ->
                val total = entry.arguments?.getString("total")?.toDoubleOrNull() ?: 0.0
                val orderId = entry.arguments?.getString("orderId") ?: ""

                PaymentMethodScreen(
                    total = total,
                    orderId = orderId,
                    onBack = { navController.popBackStack() },
                    onPaymentSuccess = {
                        navController.navigate(Routes.OrderSuccess) {
                            popUpTo(Routes.Home) { inclusive = false }
                        }
                    }
                )
            }

            // ===== ORDER SUCCESS =====
            composable(Routes.OrderSuccess) {
                OrderPlacedScreen(
                    orderId = "ORD-${System.currentTimeMillis() % 10000}",
                    onTrackOrder = { orderId ->
                        navController.navigate("track_order/$orderId") {
                            popUpTo(Routes.Home) { inclusive = false }
                        }
                    },
                    onContinueShopping = {
                        navController.navigate(Routes.Home) {
                            popUpTo(Routes.Home) { inclusive = true }
                        }
                    }
                )
            }

            // ===== ORDER TRACKING =====
            composable(
                route = "track_order/{orderId}",
                arguments = listOf(
                    navArgument("orderId") { type = NavType.StringType }
                )
            ) { entry ->
                val orderId = entry.arguments?.getString("orderId") ?: ""
                TrackOrderScreen(
                    orderId = orderId,
                    onBack = { navController.popBackStack() }
                )
            }

            // ===== CHAT SYSTEM =====
            composable(Routes.Chat) {
                ChatListScreen(
                    navController = navController,
                    currentUserId = "current_user_id"
                )
            }

            composable(
                route = "chat_detail/{chatId}",
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType }
                )
            ) { entry ->
                val chatId = entry.arguments?.getString("chatId") ?: ""
                ChatDetailScreen(
                    navController = navController,
                    chatId = chatId,
                    currentUserId = "current_user_id"
                )
            }

            // ===== NOTIFICATIONS =====
            composable(Routes.Notifications) {
                NotificationScreen(
                    navController = navController,
                    currentUserId = "current_user_id"
                )
            }

            // ===== PROFILE =====
            composable(Routes.Profile) {
                val context = LocalContext.current
                val prefs = UserPreferences(context)
                val scope = rememberCoroutineScope()

                var showEditDialog by remember { mutableStateOf(false) }
                var currentUserName by remember { mutableStateOf("John Doe") }
                var currentUserEmail by remember { mutableStateOf("john@example.com") }
                var currentUserPhone by remember { mutableStateOf("+234 801 234 5678") }
                var isVendorMode by remember { mutableStateOf(false) }

                val currentMode by prefs.getMode().collectAsState(initial = "CUSTOMER")
                LaunchedEffect(currentMode) {
                    isVendorMode = currentMode == "VENDOR"
                }

                Box {
                    ProfileScreen(
                        userName = currentUserName,
                        userEmail = currentUserEmail,
                        userPhone = currentUserPhone,
                        isVendor = isVendorMode,
                        vendorBusinessName = if (isVendorMode) "TechHub Store" else null,
                        onBack = { navController.popBackStack() },
                        onEditProfile = { showEditDialog = true },
                        onViewOrders = {
                            navController.navigate("order_history")
                        },
                        onViewNotifications = {
                            navController.navigate(Routes.Notifications)
                        },
                        onMenuItemClick = { route ->
                            // Handle other menu items
                        },
                        onSwitchMode = {
                            scope.launch { prefs.switchMode() }
                        },
                        onBecomeVendor = {
                            navController.navigate("vendor_onboarding")
                        },
                        onLogout = {
                            scope.launch {
                                prefs.logout()
                                navController.navigate(Routes.Login) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )

                    EditProfileDialog(
                        showDialog = showEditDialog,
                        onDismiss = { showEditDialog = false },
                        isVendor = isVendorMode,
                        currentName = currentUserName,
                        currentEmail = currentUserEmail,
                        currentPhone = currentUserPhone,
                        currentBusinessName = if (isVendorMode) "TechHub Store" else null,
                        currentBusinessAddress = if (isVendorMode) "123 Tech Street, Lagos" else null,
                        currentBusinessDescription = if (isVendorMode) "Quality electronics seller" else null,
                        onSaveProfile = { name, email, phone, bizName, bizAddr, bizDesc ->
                            currentUserName = name
                            currentUserEmail = email
                            currentUserPhone = phone
                        },
                        onChangeProfilePicture = {
                            // Handle profile picture change
                        }
                    )
                }
            }

            // ===== CART =====
            composable(Routes.Cart) {
                CartScreen(
                    onBack = { navController.popBackStack() },
                    onCheckout = {
                        navController.navigate("checkout/cart/0/1")
                    },
                    onContinueShopping = {
                        navController.navigate(Routes.Home)
                    }
                )
            }

            // ===== VENDOR ONBOARDING =====
            composable("vendor_onboarding") {
                VendorOnboardingScreen(
                    onBack = { navController.popBackStack() },
                    onComplete = {
                        navController.navigate(Routes.Profile) {
                            popUpTo(Routes.Profile) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

// ===== MODERN BOTTOM NAVIGATION =====
@Composable
fun ModernBottomNavBar(
    navController: NavController,
    currentRoute: String?
) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, Routes.Home),
        BottomNavItem("Cart", Icons.Default.ShoppingCart, Routes.Cart),
        BottomNavItem("Chat", Icons.Default.ChatBubble, Routes.Chat),
        BottomNavItem("Alerts", Icons.Default.Notifications, Routes.Notifications),
        BottomNavItem("Profile", Icons.Default.Person, Routes.Profile)
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            val iconColor by animateColorAsState(
                targetValue = if (selected) OrangePrimaryModern else TextSecondary,
                animationSpec = tween(300),
                label = "iconColor"
            )

            NavigationBarItem(
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.route == Routes.Chat || item.route == Routes.Notifications) {
                                Badge(containerColor = Color(0xFFEF4444)) {
                                    Text("2", color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = iconColor
                        )
                    }
                },
                label = {
                    Text(
                        item.label,
                        color = iconColor,
                        fontSize = 12.sp
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OrangePrimaryModern,
                    selectedTextColor = OrangePrimaryModern,
                    indicatorColor = OrangeSoft,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

// ===== PRODUCT NOT FOUND SCREEN =====
@Composable
fun ProductNotFoundScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = TextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Product Not Found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "The product you're looking for doesn't exist or has been removed.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryModern)
        ) {
            Text("Go Back")
        }
    }
}

// ===== VENDOR ONBOARDING PLACEHOLDER =====
@Composable
fun VendorOnboardingScreen(
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Become a Vendor",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Start selling your products and services on Bunnix!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onComplete,
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryModern),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Started")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Maybe Later")
        }
    }
}