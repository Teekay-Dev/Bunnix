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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bunnix.backend.NetworkResult
import com.example.bunnix.model.Product
import com.example.bunnix.backend.SearchViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Product) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.filteredResults.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val availableCategories by viewModel.availableCategories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val productsState by viewModel.productsState.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            SearchTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                onClearClick = viewModel::clearSearch,
                onBackClick = onNavigateBack,
                onSearch = {
                    if (searchQuery.isNotBlank()) {
                        viewModel.addToSearchHistory(searchQuery)
                        keyboardController?.hide()
                    }
                },
                focusRequester = focusRequester
            )
        }
    ) { padding ->

        SwipeRefresh(
            state = rememberSwipeRefreshState(
                isRefreshing = productsState is NetworkResult.Loading
            ),
            onRefresh = { coroutineScope.launch { viewModel.clearSearch() } },
            modifier = Modifier.padding(padding)
        ) {
            Column(Modifier.fillMaxSize()) {

                AnimatedVisibility(
                    visible = searchQuery.isNotBlank() && availableCategories.isNotEmpty()
                ) {
                    CategoryFilterChips(
                        categories = availableCategories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = viewModel::selectCategory
                    )
                }

                when {
                    searchQuery.isBlank() -> {
                        SearchHistoryContent(
                            history = searchHistory,
                            onHistoryItemClick = {
                                viewModel.updateSearchQuery(it)
                                viewModel.addToSearchHistory(it)
                            },
                            onClearHistory = viewModel::clearSearchHistory,
                            onRemoveItem = viewModel::removeFromHistory
                        )
                    }

                    productsState is NetworkResult.Loading -> {
                        LoadingState()
                    }

                    searchResults.isEmpty() -> {
                        NoResultsState(searchQuery)
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
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search products...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearClick) {
                            Icon(Icons.Default.Clear, null)
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
                Icon(Icons.Default.ArrowBack, null)
            }
        }
    )
}



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
    LazyColumn {
        items(results) { product ->
            Text(product.name)
        }
    }

}


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



