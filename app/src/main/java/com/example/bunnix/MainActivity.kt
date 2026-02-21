package com.example.bunnix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bunnix.backend.Routes
import com.example.bunnix.data.CartData
import com.example.bunnix.frontend.*
import com.example.bunnix.model.vendorList
import com.example.bunnix.vendorUI.navigation.VendorBottomNavItem
import com.example.bunnix.vendorUI.navigation.VendorNavHost
import com.example.bunnix.presentation.viewmodel.ProductViewModel
import com.example.bunnix.vendorUI.components.BunnixBottomNav
import com.example.bunnix.ui.theme.BunnixTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BunnixTheme {
                // ✅ Read mode here at the top level and decide which app to show
                val context = LocalContext.current
                val prefs = UserPreferences(context)
                val currentMode by prefs.getMode().collectAsState(initial = "CUSTOMER")

                if (currentMode == "VENDOR") {
                    VendorApp(
                        onSwitchToCustomerMode = {
                            // VendorApp will call prefs.switchMode() internally via ProfileScreen
                        }
                    )
                } else {
                    BunnixNavigation()
                }
            }
        }
    }
}

// ✅ VENDOR APP
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorApp(onSwitchToCustomerMode: () -> Unit = {}) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavRoutes = listOf(
        "dashboard", "orders_bookings", "messages", "analytics", "profile"
    )
    val showBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BunnixBottomNav(
                    navController = navController,
                    items = listOf(
                        VendorBottomNavItem.Dashboard,
                        VendorBottomNavItem.Orders,
                        VendorBottomNavItem.Messages,
                        VendorBottomNavItem.Analytics,
                        VendorBottomNavItem.Profile
                    )
                )
            }
        }
    ) { innerPadding ->
        VendorNavHost(
            navController = navController,
            onSwitchToCustomerMode = onSwitchToCustomerMode,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

// ✅ CUSTOMER APP
@Composable
fun BunnixNavigation() {

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
        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                BottomNavBar(navController)
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Routes.Splash,
            modifier = Modifier.padding(padding)
        ) {

            // ✅ Splash Screen
            composable(Routes.Splash) {
                val context = LocalContext.current
                val prefs = UserPreferences(context)

                SplashScreen(
                    userPrefs = prefs,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Routes.Splash) { inclusive = true }
                        }
                    }
                )
            }

            // ✅ Signup Route
            composable(Routes.Signup) {
                SignupScreen(
                    userPrefs = UserPreferences(LocalContext.current),
                    onLogin = {
                        navController.navigate(Routes.Login)
                    }
                )
            }

            // ✅ Login Route
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

            // ✅ 1. HOME SCREEN
            composable(Routes.Home) {
                HomeScreen(
                    onVendorClick = { id ->
                        navController.navigate("vendor_detail/$id")
                    },
                    onBookServiceClick = {
                        navController.navigate(Routes.ServiceList)
                    },
                    onShopProductClick = {
                        navController.navigate(Routes.ProductList)
                    },
                    onSearchClick = { query ->
                        navController.navigate("search/$query")
                    }
                )
            }

            // ✅ 2. VENDOR DETAIL SCREEN
            composable(
                route = "vendor_detail/{vendorId}",
                arguments = listOf(
                    navArgument("vendorId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val vendorId = backStackEntry.arguments?.getInt("vendorId") ?: 0
                val selectedVendor = vendorList.find { it.id == vendorId } ?: vendorList.first()

                VendorDetailScreen(
                    vendor = selectedVendor,
                    onBack = {
                        navController.popBackStack()
                    },
                    onBookService = { service, price ->
                        navController.navigate("booking/$service/$price")
                    },
                    onViewProducts = {
                        navController.navigate(Routes.ProductList)
                    }
                )
            }

            composable(
                route = Routes.Search,
                arguments = listOf(
                    navArgument("query") { type = NavType.StringType }
                )
            ) { entry ->
                val query = entry.arguments?.getString("query") ?: ""

                SearchScreen(
                    query = query,
                    onProductClick = { productId ->
                        navController.navigate("product_detail/$productId")
                    },
                    onServiceClick = { serviceName, price ->
                        navController.navigate("booking/$serviceName/$price")
                    }
                )
            }

            // ✅ 3. SERVICE LIST
            composable(Routes.ServiceList) {
                ServiceListScreen(
                    onBack = { navController.popBackStack() },
                    onServiceClick = { service, price ->
                        navController.navigate("booking/$service/$price")
                    }
                )
            }

            // ✅ 4. BOOKING SCREEN
            composable(
                route = Routes.Booking,
                arguments = listOf(
                    navArgument("serviceName") { type = NavType.StringType },
                    navArgument("price") { type = NavType.StringType }
                )
            ) { entry ->
                val service = entry.arguments?.getString("serviceName") ?: ""
                val price = entry.arguments?.getString("price") ?: ""

                BookingScreen(
                    serviceName = service,
                    price = price,
                    onContinue = {
                        navController.navigate("payment/$price")
                    }
                )
            }

            // ✅ Checkout Screen
            composable(
                route = Routes.Checkout,
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("price") { type = NavType.IntType },
                    navArgument("isProduct") { type = NavType.BoolType }
                )
            ) { entry ->
                val title = entry.arguments?.getString("title") ?: ""
                val price = entry.arguments?.getInt("price") ?: 0
                val isProduct = entry.arguments?.getBoolean("isProduct") ?: false

                CheckoutScreen(
                    title = title,
                    price = price,
                    isProduct = isProduct,
                    onBack = { navController.popBackStack() },
                    onContinueToPayment = { total ->
                        navController.navigate("payment/$total")
                    }
                )
            }

            composable(
                route = Routes.Payment,
                arguments = listOf(
                    navArgument("total") { type = NavType.StringType }
                )
            ) { entry ->
                val total = entry.arguments?.getString("total") ?: ""

                PaymentMethodScreen(
                    total = total,
                    onBack = { navController.popBackStack() },
                    onPaySuccess = {
                        navController.navigate(Routes.OrderSuccess)
                    }
                )
            }

            // ✅ Product List - BACKEND INTEGRATED
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

            // ✅ Product Detail - BACKEND INTEGRATED
            composable("product_detail/{productId}") { entry ->
                val productId = entry.arguments?.getString("productId") ?: ""
                val productViewModel: ProductViewModel = hiltViewModel()
                val allProducts by productViewModel.products.collectAsState()
                val product = allProducts.find { it.productId == productId }

                if (product != null) {
                    ProductDetailsScreen(
                        product = product,
                        allProducts = allProducts,
                        onAddToCart = {
                            CartData.addToCart(it)
                            navController.navigate("cart")
                        },
                        onBuyNow = {
                            navController.navigate("checkout/${it.name}/${it.price.toInt()}/true")
                        }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }

            composable(Routes.Chat) {
                ChatScreen()
            }

            composable(Routes.Notifications) {
                NotificationScreen()
            }

            // ✅ Profile Screen - with vendor mode switch support
            composable(Routes.Profile) {
                val context = LocalContext.current
                val prefs = UserPreferences(context)
                val currentMode by prefs.getMode().collectAsState(initial = "CUSTOMER")
                val hasVendor by prefs.hasVendorAccount().collectAsState(initial = false)
                val scope = rememberCoroutineScope()

                ProfileScreen(
                    currentMode = currentMode,
                    vendorEnabled = hasVendor,
                    onSwitchMode = {
                        // ✅ Switching to VENDOR mode - prefs update triggers
                        // the top-level mode check in MainActivity, swapping to VendorApp
                        scope.launch {
                            prefs.switchMode()
                        }
                    },
                    onLogout = {
                        scope.launch {
                            prefs.logout()
                            navController.navigate("login") {
                                popUpTo(0)
                            }
                        }
                    }
                )
            }

            // ✅ Cart Screen
            composable("cart") {
                CartScreen(
                    onBack = { navController.popBackStack() },
                    onCheckout = {
                        navController.navigate("checkout/Cart Items/5000/true")
                    }
                )
            }

            // ✅ Success Screen
            composable(Routes.OrderSuccess) {
                OrderPlacedScreen(
                    onTrackOrder = {},
                    onContinueShopping = {
                        navController.navigate(Routes.Home)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BunnixTheme {
        OnboardingActivity()
    }
}