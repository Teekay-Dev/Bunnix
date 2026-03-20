package com.example.bunnix

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.bunnix.database.models.Service
import com.example.bunnix.database.models.VendorProfile
import com.example.bunnix.database.models.VerificationStep
import com.example.bunnix.frontend.*
import com.example.bunnix.presentation.viewmodel.AuthUiState
import com.example.bunnix.presentation.viewmodel.AuthViewModel
import com.example.bunnix.vendorUI.navigation.VendorNavHost
import com.example.bunnix.presentation.viewmodel.ProductViewModel
import com.example.bunnix.vendorUI.components.BunnixBottomNav
import com.example.bunnix.ui.theme.BunnixTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.bunnix.database.models.CartItem
import com.example.bunnix.presentation.viewmodel.CartViewModel
import com.example.bunnix.presentation.viewmodel.ServiceViewModel
import com.example.bunnix.presentation.viewmodel.UserViewModel
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.core.view.WindowCompat
import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bunnix.presentation.viewmodel.ChatViewModel
import com.example.bunnix.database.firebase.FirebaseManager
import com.example.bunnix.database.firebase.collections.CartCollection


// Color system
val OrangePrimaryModern = Color(0xFFFF6B35)
val OrangeLight = Color(0xFFFF8C61)
val OrangeSoft = Color(0xFFFFF0EB)
val TealAccent = Color(0xFF2EC4B6)
val SurfaceLight = Color(0xFFFAFAFA)
val TextPrimary = Color(0xFF1A1A2E)
val TextSecondary = Color(0xFF6B7280)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            BunnixTheme {
                AppNavigation()
            }
        }
    }

    // ===== MAIN APP NAVIGATION WITH SPLASH & ONBOARDING =====
    @Composable
    fun AppNavigation() {
        val context = LocalContext.current
        val prefs = UserPreferences(context)
        val navController = rememberNavController()

        val authViewModel: AuthViewModel = hiltViewModel()
        val verificationState by authViewModel.verificationState.collectAsState()
        val activity = LocalContext.current as Activity

        // Check app state
        val isFirstLaunch by prefs.isFirstLaunch.collectAsState(initial = true)
        val isLoggedIn by prefs.isLoggedIn.collectAsState(initial = false)
        val currentMode by prefs.getMode().collectAsState(initial = "CUSTOMER")

        // Determine start destination
        val startDestination = remember(isFirstLaunch, isLoggedIn, currentMode) {
            when {
                isFirstLaunch -> "splash"
                !isLoggedIn -> "login"
                currentMode == "VENDOR" -> "vendor_mode"
                else -> "customer_mode"
            }
        }

        NavHost(
            navController = navController, startDestination = "splash"
        ) {
            // ===== SPLASH SCREEN =====
            composable("splash") {
                AnimatedSplashScreen(onComplete = {
                    val route = when {
                        isFirstLaunch -> "onboarding"
                        !isLoggedIn -> "login"
                        else -> {
                            if (currentMode == "VENDOR") "vendor_mode" else "customer_mode"
                        }
                    }

                    navController.navigate(route) {
                        popUpTo("splash") { inclusive = true }
                    }
                })
            }

            // ===== ONBOARDING SCREEN =====
            composable("onboarding") {
                val scope = rememberCoroutineScope()

                OnboardingScreen(
                    onGetStarted = {
                        scope.launch {
                            prefs.setFirstLaunch(false)
                            navController.navigate("signup") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        }
                    }
                )
            }

            // ===== SIGNUP SCREEN =====
            composable("signup") {
                val scope = rememberCoroutineScope()
                val authViewModel: AuthViewModel = hiltViewModel()
                val verificationState by authViewModel.verificationState.collectAsState()
                val uiState by authViewModel.uiState.collectAsState()
                val lifecycleOwner = LocalLifecycleOwner.current

                // Auto-check verification when returning to app
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            if (verificationState.currentStep == VerificationStep.EMAIL_INSTRUCTIONS) {
                                authViewModel.checkEmailVerification()
                            }
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                // Handle successful signup
                LaunchedEffect(uiState) {
                    if (uiState is AuthUiState.Success) {
                        val user = (uiState as AuthUiState.Success).user
                        prefs.setLoggedIn(true)

                        if (user.isVendor) {
                            prefs.setMode("VENDOR")
                            navController.navigate("vendor_mode") {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            prefs.setMode("CUSTOMER")
                            navController.navigate("customer_mode") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }

                when (verificationState.currentStep) {
                    VerificationStep.IDLE -> {
                        SignupScreen(
                            isSwitchingMode = false,
                            currentMode = "customer",
                            verificationStep = verificationState.currentStep,
                            onLoginClick = {
                                navController.navigate("login") {
                                    popUpTo("signup") { inclusive = true }
                                }
                            },
                            onSignupSuccess = { user, password ->
                                val vendorData = if (!user.isVendor) null else VendorProfile(
                                    vendorId = "",
                                    businessName = user.name,
                                    category = "",
                                    address = user.address,
                                    phone = user.phone,
                                    email = user.email
                                )
                                authViewModel.initiateSignup(user, password, vendorData)
                            }
                        )
                    }

                    VerificationStep.SELECT_METHOD -> {
                        // Email only - no phone option
                        MethodSelectionScreen(
                            onEmailSelected = { authViewModel.startEmailVerification() }
                        )
                    }

                    VerificationStep.EMAIL_INSTRUCTIONS -> {
                        // Poll for verification
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(3000)
                                authViewModel.checkEmailVerification()
                            }
                        }

                        EmailInstructionScreen(
                            onCheckVerification = {
                                authViewModel.checkEmailVerification()
                            },
                            onBackClick = { authViewModel.resetStep() }
                        )
                    }

                    VerificationStep.COMPLETED -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    else -> {
                        // Handle any other steps
                        LaunchedEffect(Unit) {
                            authViewModel.resetStep()
                        }
                    }
                }
            }

            // ===== VENDOR SIGNUP =====
            composable("vendor_signup") {
                val authViewModel: AuthViewModel = hiltViewModel()
                val verificationState by authViewModel.verificationState.collectAsState()
                val uiState by authViewModel.uiState.collectAsState()
                val lifecycleOwner = LocalLifecycleOwner.current

                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            if (verificationState.currentStep == VerificationStep.EMAIL_INSTRUCTIONS) {
                                authViewModel.checkEmailVerification()
                            }
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                LaunchedEffect(uiState) {
                    if (uiState is AuthUiState.Success) {
                        val user = (uiState as AuthUiState.Success).user
                        prefs.setLoggedIn(true)
                        prefs.setMode("VENDOR")
                        navController.navigate("vendor_mode") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                when (verificationState.currentStep) {
                    VerificationStep.IDLE -> {
                        SignupScreen(
                            isSwitchingMode = true,
                            currentMode = "customer",
                            verificationStep = verificationState.currentStep,
                            onLoginClick = { navController.popBackStack() },
                            onSignupSuccess = { user, password ->
                                val vendorUser = user.copy(isVendor = true)
                                val vendorData = VendorProfile(
                                    vendorId = "",
                                    businessName = user.name,
                                    category = "",
                                    address = user.address,
                                    phone = user.phone,
                                    email = user.email
                                )
                                authViewModel.initiateSignup(vendorUser, password, vendorData)
                            }
                        )
                    }

                    VerificationStep.SELECT_METHOD -> {
                        MethodSelectionScreen(
                            onEmailSelected = { authViewModel.startEmailVerification() }
                        )
                    }

                    VerificationStep.EMAIL_INSTRUCTIONS -> {
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(3000)
                                authViewModel.checkEmailVerification()
                            }
                        }

                        EmailInstructionScreen(
                            onCheckVerification = {
                                authViewModel.checkEmailVerification()
                            },
                            onBackClick = { authViewModel.resetStep() }
                        )
                    }

                    VerificationStep.COMPLETED -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    else -> {
                        LaunchedEffect(Unit) {
                            authViewModel.resetStep()
                        }
                    }
                }
            }

            // ===== LOGIN SCREEN =====
            composable("login") {
                val scope = rememberCoroutineScope()

                LoginScreen(
                    authViewModel = hiltViewModel(),
                    onSignupClick = { navController.navigate("signup") },
                    onLoginSuccess = { isVendor ->
                        scope.launch {
                            prefs.setLoggedIn(true)
                            if (isVendor) {
                                prefs.setMode("VENDOR")
                                navController.navigate("vendor_mode") { popUpTo(0) { inclusive = true } }
                            } else {
                                prefs.setMode("CUSTOMER")
                                navController.navigate("customer_mode") { popUpTo(0) { inclusive = true } }
                            }
                        }
                    }
                )
            }

            // ===== CUSTOMER MODE =====
            composable("customer_mode") {
                val scope = rememberCoroutineScope()

                CustomerApp(
                    onSwitchToVendor = {
                        navController.navigate("vendor_signup")
                    },
                    onLogout = {
                        scope.launch {
                            prefs.logout()
                            FirebaseAuth.getInstance().signOut()
                            authViewModel.signOut()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // ===== VENDOR MODE =====
            composable("vendor_mode") {
                val scope = rememberCoroutineScope()

                VendorApp(
                    onSwitchToCustomerMode = {
                        scope.launch {
                            prefs.setMode("CUSTOMER")
                            navController.navigate("customer_mode") {
                                popUpTo("vendor_mode") { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    }

    // ===== SPLASH SCREEN =====
    @Composable
    fun SplashScreen(onComplete: () -> Unit) {
        LaunchedEffect(Unit) {
            delay(9000) // 2.5 seconds
            onComplete()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFF9D5C),
                            Color(0xFFFF7900)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // You can replace this with your logo
                Image(
                    painter = painterResource(R.drawable.bunnix_2),
                    contentDescription = null,
                    modifier = Modifier.size(300.dp)
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    "Bunnix",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Shop & Book Services",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }

    // ===== ONBOARDING SCREEN =====
    @Composable
    fun OnboardingScreen(onGetStarted: () -> Unit) {
        var currentPage by remember { mutableStateOf(0) }

        val onboardingPages = listOf(
            OnboardingPage(
                title = "Shop Products",
                description = "Browse and purchase products from local vendors",
                emoji = "🛍️"
            ),
            OnboardingPage(
                title = "Book Services",
                description = "Schedule appointments for services you need",
                emoji = "📅"
            ),
            OnboardingPage(
                title = "Become a Vendor",
                description = "Start selling your products and services",
                emoji = "💼"
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            // Page Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .width(if (index == currentPage) 24.dp else 8.dp)
                            .height(8.dp)
                            .background(
                                if (index == currentPage) OrangePrimaryModern else Color(0xFFE0E0E0),
                                androidx.compose.foundation.shape.CircleShape
                            )
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Emoji
            Text(
                onboardingPages[currentPage].emoji,
                fontSize = 120.sp
            )

            Spacer(Modifier.height(48.dp))

            // Title
            Text(
                onboardingPages[currentPage].title,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = TextPrimary
            )

            Spacer(Modifier.height(16.dp))

            // Description
            Text(
                onboardingPages[currentPage].description,
                fontSize = 16.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.weight(1f))

            // Buttons
            if (currentPage < 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onGetStarted) {
                        Text("Skip", color = TextSecondary, fontSize = 16.sp)
                    }

                    Button(
                        onClick = { currentPage++ },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryModern),
                        modifier = Modifier
                            .width(120.dp)
                            .height(48.dp)
                    ) {
                        Text("Next", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimaryModern)
                ) {
                    Text("Get Started", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    data class OnboardingPage(
        val title: String,
        val description: String,
        val emoji: String
    )

    // ===== CUSTOMER APP =====
    @Composable
    fun CustomerApp(
        onSwitchToVendor: () -> Unit,
        onLogout: () -> Unit
    ) {
        val cartViewModel: CartViewModel = hiltViewModel()
        val cartItems by cartViewModel.cartItems.collectAsState()
        val navController = rememberNavController()
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        val bottomBarScreens = listOf(
            Routes.Home,
            Routes.Cart,
            Routes.Chat,
            Routes.Notifications,
            Routes.Profile
        )

        Scaffold(
            contentWindowInsets = WindowInsets(0,0,0,0),
            bottomBar = {
                AnimatedVisibility(
                    visible = currentRoute in bottomBarScreens,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it }
                ) {
                    ModernBottomNavBar(navController, currentRoute)
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Routes.Home,
                modifier = Modifier.padding(padding)
            ) {
                composable(Routes.Home) {
                    HomeScreen(
                        onVendorClick = { id -> navController.navigate("vendor_detail/$id") },
                        onBookServiceClick = { navController.navigate(Routes.ServiceList) },
                        onShopProductClick = { navController.navigate(Routes.ProductList) },
                        onSearchClick = { query -> navController.navigate("search/${Uri.encode(query)}") },
//                        bottomBar = { ModernBottomNavBar(navController, Routes.Home) }
                    )
                }


                composable(
                    route = "vendor_detail/{vendorId}",
                    arguments = listOf(navArgument("vendorId") { type = NavType.StringType })
                ) { entry ->
                    val vendorId = entry.arguments?.getString("vendorId") ?: ""
                    val vendorViewModel: VendorViewModel = hiltViewModel()

                    val chatViewModel: ChatViewModel = hiltViewModel()
                    val userViewModel: UserViewModel = hiltViewModel()
                    val currentUser by userViewModel.user.collectAsState()
                    val vendor by vendorViewModel.vendorProfile.collectAsState()
                    val isLoading by vendorViewModel.isLoading.collectAsState()
                    val error by vendorViewModel.error.collectAsState()

                    // Fetch vendor when composable is first shown
                    LaunchedEffect(vendorId) {
                        if (vendorId.isNotBlank()) {
                            vendorViewModel.fetchVendor(vendorId)
                        }
                    }

                    when {
                        isLoading -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }

                        vendor != null -> VendorDetailScreen(
                            vendor = vendor!!,
                            onBack = { navController.popBackStack() },
                            onChat = {
                                currentUser?.let { user ->
                                    chatViewModel.getOrCreateChat(
                                        currentUserId = user.userId,
                                        vendorId = vendor!!.vendorId,
                                        vendorName = vendor!!.businessName,
                                        vendorImage = ""
                                    ) { chatId ->
                                        navController.navigate("chat_detail/$chatId")
                                    }
                                }
                            }
                        )

                        error != null -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { Text("Error: $error") }

                        else -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { Text("Vendor not found") }
                    }
                }


                composable(
                    route = "search/{query}",
                    arguments = listOf(navArgument("query") { type = NavType.StringType })
                ) { entry ->
                    val query = entry.arguments?.getString("query") ?: ""
                    val productViewModel: ProductViewModel = hiltViewModel()
                    val products by productViewModel.products.collectAsState()

                    SearchScreen(
                        query = query,
                        products = products,
                        services = emptyList(),
                        onProductClick = { id -> navController.navigate("product_detail/$id") },
                        onServiceClick = { _, _ -> }
                    )
                }

                composable(Routes.ServiceList) {

                    val serviceViewModel: ServiceViewModel = hiltViewModel() // BUT SEE NOTE BELOW
                    val services by serviceViewModel.services.collectAsState()

                    ServiceListScreen(
                        services = services,
                        onBack = { navController.popBackStack() },
                        onServiceClick = { service ->
                            navController.navigate("booking/${Uri.encode(service.serviceId)}")
                        }
                    )
                }

                composable(
                    route = "booking/{serviceId}",
                    arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
                ) { entry ->
                    val serviceId = entry.arguments?.getString("serviceId") ?: ""

                    val serviceViewModel: ServiceViewModel = hiltViewModel()
                    val serviceState by serviceViewModel.getService(serviceId).collectAsState(initial = null)
                    val service = serviceState

                    if (service == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        BookingScreen(
                            service = service,
                            onBack = { navController.popBackStack() },
                            onContinue = { details ->

                                val total = service.price
                                val orderId = "ORD-${System.currentTimeMillis()}"

                                navController.navigate("payment/$total/$orderId")
                            }
                        )
                    }
                }

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
                    arguments = listOf(navArgument("productId") { type = NavType.StringType })
                ) { entry ->
                    val productId = entry.arguments?.getString("productId") ?: ""
                    val productViewModel: ProductViewModel = hiltViewModel()
                    // Use a CartViewModel if you have one injected, or use CartCollection directly
                    val scope = rememberCoroutineScope()
                    val userId = FirebaseManager.getCurrentUserId()

                    val allProducts by productViewModel.products.collectAsState()
                    val product = allProducts.find { it.productId == productId }

                    if (product != null) {
                        ProductDetailsScreen(
                            product = product,
                            allProducts = allProducts,
                            onAddToCart = { prod, quantity ->
                                val item = CartItem(
                                    id = prod.productId,
                                    productId = prod.productId,
                                    name = prod.name,
                                    vendorId = prod.vendorId,
                                    vendorName = prod.vendorName,
                                    price = prod.discountPrice ?: prod.price,
                                    originalPrice = if (prod.discountPrice != null) prod.price else null,
                                    quantity = quantity,
                                    imageUrl = prod.imageUrls.firstOrNull() ?: "", // FIXED Type
                                    variant = null
                                )

                                // ✅ CALL CART COLLECTION DIRECTLY
                                scope.launch {
                                    if (userId != null) {
                                        CartCollection.addToCart(userId, item)
                                    }
                                }
                            },
                            onBuyNow = { prod, qty ->
                                // Navigate to checkout
                            },
                            onBack = { navController.popBackStack() },
                            onChatWithVendor = { vId ->
                                val vName = Uri.encode(product.vendorName)
                                val vImage = Uri.encode(product.imageUrls.firstOrNull() ?: "")
                                navController.navigate("chat_detail/chat_${product.vendorId}_$vId/$vName/$vImage/$vId")
                            }
                        )
                    } else {
                        ProductNotFoundScreen { navController.popBackStack() }
                    }
                }

//                composable(
//                    route = "checkout/{type}/{id}/{extra}",
//                    arguments = listOf(
//                        navArgument("type") { type = NavType.StringType },
//                        navArgument("id") { type = NavType.StringType },
//                        navArgument("extra") { type = NavType.StringType }
//                    )
//                ) { entry ->
//                    val type = entry.arguments?.getString("type") ?: ""
//                    val id = entry.arguments?.getString("id") ?: ""
//                    val extra = entry.arguments?.getString("extra") ?: ""
//
//                    val items = when (type) {
//
//                        "product" -> {
//                            val vm: ProductViewModel = hiltViewModel()
//                            val products by vm.products.collectAsState()
//                            val product = products.find { it.productId == id }
//
//                            val qty = extra.toIntOrNull() ?: 1
//
//                            product?.let {
//                                listOf(
//                                    CheckoutItem(
//                                        id = it.productId,
//                                        name = it.name,
//                                        imageUrl = it.imageUrls.firstOrNull() ?: "",
//                                        price = it.price,
//                                        quantity = qty,
//                                        variant = null
//                                    )
//                                )
//                            } ?: emptyList()
//                        }
//
//                        "service" -> listOf(
//                            CheckoutItem(
//                                id = id,
//                                name = "Service Booking ($extra)",
//                                imageUrl = "",
//                                price = 0.0,
//                                quantity = 1,
//                                variant = null
//                            )
//                        )
//
//                        "cart" -> cartItems.map {
//                            CheckoutItem(
//                                id = it.id,
//                                name = it.name,
//                                imageUrl = it.imageUrl ?: "",
//                                price = it.price,
//                                quantity = it.quantity,
//                                variant = it.variant
//                            )
//                        }
//
//                        else -> emptyList()
//                    }
//
//                    val total = items.sumOf { it.price * it.quantity }
//                    val orderId = "ORD-${System.currentTimeMillis() % 100000}"
//
//                    CheckoutScreen(
//                        items = items,
//                        total = total,
//                        isServiceBooking = type == "service",
//                        onBack = { navController.popBackStack() },
//                        onPaymentMethodSelect = { navController.navigate("payment/$total/$orderId") },
//                        onApplyPromo = {
//                            it.uppercase() in setOf(
//                                "WELCOME10",
//                                "SAVE20",
//                                "BUNNIX50"
//                            )
//                        },
//                        onPlaceOrder = { navController.navigate(Routes.OrderSuccess) }
//                    )
//                }

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
                            cartViewModel.clearCart()
                            navController.navigate(Routes.OrderSuccess) {
                                popUpTo(Routes.Home) { inclusive = false }
                            }
                        }
                    )
                }

                composable(Routes.OrderSuccess) {
                    OrderPlacedScreen(
                        orderId = "ORD-${System.currentTimeMillis() % 10000}",
                        onTrackOrder = { id ->
                            navController.navigate("track_order/$id") {
                                popUpTo(Routes.Home) { inclusive = false }
                            }
                        },
                        onContinueShopping = {
                            navController.navigate(Routes.Home) {
                                popUpTo(Routes.Home) { inclusive = true }
                            }
                        },

                        onViewReceipt = { id ->
                            navController.navigate("receipt/$id")
                        }

                    )
                }

                composable(
                    route = "track_order/{orderId}",
                    arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                ) { entry ->
                    val orderId = entry.arguments?.getString("orderId") ?: ""
                    TrackOrderScreen(
                        orderId = orderId,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.Chat) {
                    ChatListScreen(navController)
                }

                // Inside MainActivity.kt NavHost

                composable(
                    route = "chat_detail/{chatId}/{vendorName}/{vendorImage}",
                    arguments = listOf(
                        navArgument("chatId") { type = NavType.StringType },
                        navArgument("vendorName") { type = NavType.StringType },
                        navArgument("vendorImage") { type = NavType.StringType },
                        navArgument("vendorId") { type = NavType.StringType }
                    )
                ) { entry ->
                    val chatId = entry.arguments?.getString("chatId") ?: ""
                    val vendorName = Uri.decode(entry.arguments?.getString("vendorName") ?: "Unknown")
                    val vendorImage = Uri.decode(entry.arguments?.getString("vendorImage") ?: "")
                    val vendorId = entry.arguments?.getString("vendorId") ?: ""

                    ChatDetailScreen(
                        navController = navController,
                        chatId = chatId,
                        vendorName = vendorName,
                        vendorImageUrl = vendorImage,
                        vendorId = vendorId
                    )
                }

                composable(Routes.Notifications) {
                    NotificationScreen(navController, "current_user_id")
                }

                composable(Routes.Profile) {
                    val userViewModel: UserViewModel = hiltViewModel()
                    val user by userViewModel.user.collectAsState()
                    val vendorProfile by userViewModel.vendorProfile.collectAsState()
                    val isVendor by userViewModel.isVendor.collectAsState()

                    var showEditDialog by remember { mutableStateOf(false) }

                    // Use vendor data if available, otherwise user data
                    val displayName = vendorProfile?.businessName ?: user?.name ?: ""
                    val displayEmail = vendorProfile?.email ?: user?.email ?: ""
                    val displayPhone = vendorProfile?.phone ?: user?.phone ?: ""
                    val displayAddress = vendorProfile?.address ?: user?.address ?: ""

                    ProfileScreen(
                        userName = displayName,
                        userEmail = displayEmail,
                        userPhone = displayPhone,
                        isVendor = isVendor,
                        vendorBusinessName = vendorProfile?.businessName,
                        onBack = { navController.popBackStack() },
                        onEditProfile = { showEditDialog = true },
                        onViewOrders = { navController.navigate("order_history") },
                        onViewNotifications = { navController.navigate(Routes.Notifications) },
                        onSwitchMode = onSwitchToVendor,
                        onBecomeVendor = onSwitchToVendor,
                        onLogout = onLogout
                    )

                    if (isVendor && vendorProfile != null) {
                        // Vendor edit dialog
                        EditProfileDialog(
                            showDialog = showEditDialog,
                            onDismiss = { showEditDialog = false },
                            isVendor = true,
                            currentName = vendorProfile?.businessName ?: "",
                            currentEmail = vendorProfile?.email ?: "",
                            currentPhone = vendorProfile?.phone ?: "",
                            currentBusinessName = vendorProfile?.businessName,
                            currentBusinessAddress = vendorProfile?.address,
                            currentBusinessDescription = vendorProfile?.description,
                            onSaveProfile = { name, email, phone, businessName, address, description ->
                                // Update vendor profile in Firestore
                                userViewModel.updateVendorProfile(
                                    businessName = businessName ?: name,
                                    email = email,
                                    phone = phone,
                                    address = address ?: "",
                                    description = description ?: ""
                                )
                                showEditDialog = false
                            },
                            onChangeProfilePicture = {}
                        )
                    } else {
                        // Customer edit dialog
                        EditProfileDialog(
                            showDialog = showEditDialog,
                            onDismiss = { showEditDialog = false },
                            isVendor = false,
                            currentName = user?.name ?: "",
                            currentEmail = user?.email ?: "",
                            currentPhone = user?.phone ?: "",
                            currentBusinessName = null,
                            currentBusinessAddress = null,
                            currentBusinessDescription = null,
                            onSaveProfile = { name, email, phone, _, _, _ ->
                                // Update user profile in Firestore
                                userViewModel.updateUserProfile(name, email, phone)
                                showEditDialog = false
                            },
                            onChangeProfilePicture = {}
                        )
                    }
                }

                composable(Routes.Cart) {
                    CartScreen(
                        onBack = { navController.popBackStack() },
                        onCheckout = { navController.navigate(Routes.Checkout) },
                        onStartShopping = {
                            navController.navigate(Routes.Home) {
                                popUpTo(Routes.Home)
                            }
                        },
                    )
                }

                // In your NavHost
                composable(
                    route = "receipt/{orderId}",
                    arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val orderId = backStackEntry.arguments?.getString("orderId")
                    ViewReceiptScreen(
                        orderId = orderId,
                        onBackClick = { navController.popBackStack() },
                        onShareClick = { receipt -> /* Share logic */ },
                        onDownloadClick = { receipt -> /* Download PDF logic */ }
                    )
                }
            }
        }
    }



    // ===== VENDOR APP =====
    @Composable
    fun VendorApp(onSwitchToCustomerMode: () -> Unit = {}) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val bottomNavRoutes = listOf(
            "vendor_dashboard", "vendor_orders", "vendor_messages", "vendor_profile"
        )
        val showBottomNav = currentRoute in bottomNavRoutes

        Scaffold(
            contentWindowInsets = WindowInsets(0,0,0,0),
            bottomBar = {
                if (showBottomNav) {
                    BunnixBottomNav(
                        navController = navController
                        // items is hardcoded in BunnixBottomNav, don't pass it here
                    )
                }
            }
        ) { innerPadding ->
            VendorNavHost(
                navController = navController,
                onNavigateToLogin = onSwitchToCustomerMode, // changed from onSwitchToCustomerMode
                modifier = Modifier.padding(innerPadding)
            )
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
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
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
                         Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = iconColor
                            )
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
}