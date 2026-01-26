package com.example.bunnix.backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.model.Product
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

    // Search input
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Selected category
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    // 🔥 MAIN SEARCH PIPELINE (DB-backed)
    val productsState: StateFlow<NetworkResult<List<Product>>> =
        searchQuery
            .debounce(300)
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    flowOf(NetworkResult.Success(emptyList()))
                } else {
                    repository.searchProducts(query)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = NetworkResult.Loading()
            )

    // Category list
    val availableCategories: StateFlow<List<String>> =
        productsState
            .map { state ->
                (state as? NetworkResult.Success)
                    ?.data
                    ?.map { it.category }
                    ?.distinct()
                    ?: emptyList()
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    // Category-filtered results
    val filteredResults: StateFlow<List<Product>> =
        combine(productsState, selectedCategory) { state, category ->
            val data = (state as? NetworkResult.Success)?.data ?: emptyList()
            if (category == null) data
            else data.filter { it.category.equals(category, true) }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    // Search history
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory = _searchHistory.asStateFlow()

    init {
        loadSearchHistory()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) addToSearchHistory(query)
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _selectedCategory.value = null
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            dataStoreManager.getSearchHistory().collect {
                _searchHistory.value = it
            }
        }
    }

    private fun addToSearchHistory(query: String) {
        viewModelScope.launch {
            val updated = _searchHistory.value.toMutableList()
            updated.remove(query)
            updated.add(0, query)
            if (updated.size > 10) updated.removeLast()
            _searchHistory.value = updated
            dataStoreManager.saveSearchHistory(updated)
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
            val updated = _searchHistory.value.toMutableList()
            updated.remove(query)
            _searchHistory.value = updated
            dataStoreManager.saveSearchHistory(updated)
        }
    }
}
