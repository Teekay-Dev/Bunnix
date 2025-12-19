package com.example.bunnix.backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Products state
    private val _productsState = MutableStateFlow<NetworkResult<List<Product>>>(NetworkResult.Loading())
    val productsState: StateFlow<NetworkResult<List<Product>>> = _productsState.asStateFlow()


    // Search results with debounce
    val searchResults: StateFlow<List<Product>> = searchQuery
        .debounce(300)
        .combine(productsState) { query, state ->
            when (state) {
                is NetworkResult.Success -> {
                    if (query.isBlank()) {
                        emptyList()
                    } else {
                        state.data?.filter { product ->
                            product.name.contains(query, ignoreCase = true) ||
                                    product.category.contains(query, ignoreCase = true) ||
                                    product.description.contains(query, ignoreCase = true)
                        } ?: emptyList()
                    }
                }
                else -> emptyList()
            }
        }

        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Search history - now from DataStore
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    // Selected category filter
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Available categories
    val availableCategories: StateFlow<List<String>> = productsState
        .map { state ->
            when (state) {
                is NetworkResult.Success -> {
                    state.data?.map { it.category }?.distinct() ?: emptyList()
                }
                else -> emptyList()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered results (by category)
    val filteredResults: StateFlow<List<Product>> = combine(
        searchResults,
        selectedCategory
    ) { results, category ->
        if (category == null) {
            results
        } else {
            results.filter { it.category.equals(category, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadProducts()
        loadSearchHistory()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addToSearchHistory(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            val currentHistory = _searchHistory.value.toMutableList()

            // Remove if already exists (to move it to top)
            currentHistory.remove(query)

            // Add to beginning
            currentHistory.add(0, query)

            // Keep only last 10 searches
            if (currentHistory.size > 10) {
                currentHistory.removeAt(currentHistory.size - 1)
            }

            _searchHistory.value = currentHistory
            dataStoreManager.saveSearchHistory(currentHistory)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            _searchHistory.value = emptyList()
            dataStoreManager.saveSearchHistory(emptyList())
        }
    }

    fun removeFromHistory(query: String) {
        viewModelScope.launch {
            val currentHistory = _searchHistory.value.toMutableList()
            currentHistory.remove(query)
            _searchHistory.value = currentHistory
            dataStoreManager.saveSearchHistory(currentHistory)
        }
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _selectedCategory.value = null
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getProducts().collect { result ->
                _productsState.value = result
            }
        }
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            dataStoreManager.getSearchHistory().collect { history ->
                _searchHistory.value = history
            }
        }
    }
}