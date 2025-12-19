package com.example.bunnix.frontend

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.bunnix.backend.NetworkResult
import com.example.bunnix.backend.Product
import com.example.bunnix.backend.SearchViewModel

/**
 * Main Search Screen composable.
 *
 * This screen is responsible ONLY for:
 * - Displaying UI
 * - Sending user actions to the ViewModel
 * - Reacting to state changes from the ViewModel
 *
 * All business logic (filtering, searching, history) lives in SearchViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Product) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    // Collect search-related state from the ViewModel
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.filteredResults.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val availableCategories by viewModel.availableCategories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val productsState by viewModel.productsState.collectAsState()

    // Used to automatically focus the search text field
    val focusRequester = remember { FocusRequester() }

    // Used to control (hide) the software keyboard
    val keyboardController = LocalSoftwareKeyboardController.current

    // Coroutine scope used for refresh actions
    val coroutineScope = rememberCoroutineScope()

    // Automatically focus the search bar when the screen opens
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            // Top bar that contains the search input field
            SearchTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onClearClick = { viewModel.clearSearch() },
                onBackClick = onNavigateBack,
                onSearch = {
                    // When user presses search on the keyboard
                    if (searchQuery.isNotBlank()) {
                        viewModel.addToSearchHistory(searchQuery)
                        keyboardController?.hide()
                    }
                },
                focusRequester = focusRequester
            )
        }
    ) { padding ->

        // Pull-to-refresh wrapper
        SwipeRefresh(
            state = rememberSwipeRefreshState(
                isRefreshing = productsState is com.example.bunnix.backend.NetworkResult.Loading
            ),
            onRefresh = {
                coroutineScope.launch {
                    viewModel.clearSearch()
                }
            },
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {

                // Show category filters only when searching
                AnimatedVisibility(
                    visible = searchQuery.isNotBlank() && availableCategories.isNotEmpty()
                ) {
                    CategoryFilterChips( //CategoryFilterChips is red
                        categories = availableCategories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.selectCategory(it) } // it is red
                    )
                }

                /**
                 * UI state handling:
                 * - No query → show search history
                 * - Loading → show loader
                 * - Empty results → show empty state
                 * - Results → show products
                 */
                when {
                    searchQuery.isBlank() -> {
                        SearchHistoryContent( // SearchHistoryContent is red
                            history = searchHistory,
                            onHistoryItemClick = { query -> //query is underlined  red
                                viewModel.updateSearchQuery(query)
                                viewModel.addToSearchHistory(query)
                            },
                            onClearHistory = { viewModel.clearSearchHistory() },
                            onRemoveItem = { viewModel.removeFromHistory(it) }
                        )
                    }

                    productsState is com.example.bunnix.backend.NetworkResult.Loading -> {
                        LoadingState() //LoadingState() is red
                    }

                    searchResults.isEmpty() -> {
                        NoResultsState(searchQuery = searchQuery)
                    }

                    else -> {
                        SearchResultsContent(
                            results = searchResults,
                            searchQuery = searchQuery,
                            onProductClick = onNavigateToDetail
                        )
                    }
                }
            }
        }
    }
}

/**
 * Top app bar containing the search text field.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onBackClick: () -> Unit,
    onSearch: () -> Unit,
    focusRequester: FocusRequester
) {
    TopAppBar(
        title = {
            TextField(
                value = searchQuery,
                // Every character typed updates the ViewModel
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search products...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    // Clear button only appears when text is not empty
                    AnimatedVisibility(visible = searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearClick) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() })
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

/**
 * Displays search results in a scrollable list.
 */
@Composable
fun SearchResultsContent(
    results: List<Product>,
    searchQuery: String,
    onProductClick: (Product) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(results, key = { it.id }) { product ->
            // Clicking a product navigates to the detail screen
            BestValueDealCard(
                product = product,
                onClick = { onProductClick(product) }
            )
        }
    }
}

/**
 * Shown when no products match the search query.
 */
@Composable
fun NoResultsState(searchQuery: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "No results for \"$searchQuery\"")
            Text(text = "Try different keywords or check your spelling")
        }
    }
}

@Composable
fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun CategoryFilterChips(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    Row(Modifier.padding(8.dp)) {
        categories.forEach { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                label = { Text(category) }
            )
        }
    }
}

@Composable
fun BestValueDealCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(onClick = onClick) {
        Column(Modifier.padding(16.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleMedium)
            Text(product.description)
        }
    }
}

@Composable
fun SearchHistoryContent(
    history: List<String>,
    onHistoryItemClick: (String) -> Unit,
    onClearHistory: () -> Unit,
    onRemoveItem: (String) -> Unit
) {
    Column {
        history.forEach {
            Text(it, modifier = Modifier.clickable { onHistoryItemClick(it) })
        }
    }
}



