package com.example.bunnix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bunnix.backend.Routes
import com.example.bunnix.data.CartData
import com.example.bunnix.data.ProductData
import com.example.bunnix.frontend.BookingScreen
import com.example.bunnix.frontend.HomeScreen
import com.example.bunnix.frontend.OnboardingActivity
//import com.example.bunnix.frontend.OrderPlacedScreen
import com.example.bunnix.frontend.PaymentMethodScreen
import com.example.bunnix.frontend.ServiceListScreen
import com.example.bunnix.frontend.VendorDetailScreen
import com.example.bunnix.frontend.*
import com.example.bunnix.model.vendorList
import com.example.bunnix.ui.theme.BunnixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BunnixTheme {
//                OnboardingActivity()
                BunnixNavigation()
            }
        }
    }
}



@Composable
fun BunnixNavigation() {

    val navController = rememberNavController()
    // ✅ Get current route
    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    // ✅ Screens where BottomBar should appear
    val bottomBarScreens = listOf(
        Routes.Home,
        Routes.Cart,
        Routes.Chat,
        Routes.Notifications,
        Routes.Profile
    )

    Scaffold(

        // ✅ Bottom Navigation Added Here
        bottomBar = {
            BottomNavBar(navController)
        }

    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Routes.Home,
            modifier = Modifier.padding(padding)
        ) {

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

                val vendorId =
                    backStackEntry.arguments?.getInt("vendorId") ?: 0

                // Find vendor from list
                val selectedVendor =
                    vendorList.find { it.id == vendorId }
                        ?: vendorList.first()

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

                val service =
                    entry.arguments?.getString("serviceName") ?: ""

                val price =
                    entry.arguments?.getString("price") ?: ""

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


// ✅ Product List
            composable(Routes.ProductList) {
                ProductListScreen(
                    onBack = { navController.popBackStack() },

                    onProductClick = { product ->
                        navController.navigate("product_detail/${product.id}")
                    }
                )
            }

// ✅ Product Detail
            composable("product_detail/{productId}") { entry ->

                val id = entry.arguments?.getString("productId")?.toInt() ?: 0
                val product =
                    ProductData.products.find { it.id == id }
                        ?: ProductData.products.first()

                ProductDetailsScreen(
                    product = product,
                    allProducts = ProductData.products,

                    onAddToCart = {
                        CartData.addToCart(it)
                        navController.navigate("cart")
                    },

                    onBuyNow = {
                        navController.navigate("checkout/${it.name}/${it.price}/true")
                    }
                )
            }


            composable(Routes.Chat) {
                ChatScreen()
            }

            composable(Routes.Notifications) {
                NotificationScreen()
            }

            composable(Routes.Profile) {
                ProfileScreen()
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