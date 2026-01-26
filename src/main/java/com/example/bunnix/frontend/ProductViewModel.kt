package com.example.bunnix.frontend

import android.app.Application
import androidx.lifecycle.*
import com.example.bunnix.data.ProductRepository
import com.example.bunnix.model.Product
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository(application)

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    fun loadProducts() {
        viewModelScope.launch {
            try {
                _products.value = repository.syncProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
