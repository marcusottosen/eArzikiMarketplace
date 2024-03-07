package com.example.earzikimarketplace.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.earzikimarketplace.data.model.dataClass.DBCategory
import com.example.earzikimarketplace.data.model.supabaseAdapter.ListingsDB
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _categories = MutableLiveData<List<DBCategory>>()
    val categories: LiveData<List<DBCategory>> = _categories
    //private val supabaseAdapter = SupabaseAdapter()
    private val listingsDB = ListingsDB()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    init {
        _isLoading.value = true
    }

    fun fetchCategories(context: Context) {
        viewModelScope.launch {
            val initialCategories = DBCategory.createDefaultCategories(context)
            _categories.value = initialCategories
            _isLoading.value = false // loading of initial data done

            // fetch item counts in parallel and update categories when retrieved
            val updatedCategories = initialCategories.map { category ->
                async {
                    category.copy(itemCount = fetchItemCountForCategory(category.categoryID))
                }
            }.awaitAll()

            // Update categories with the fetched item counts
            _categories.value = updatedCategories
        }
    }

    private suspend fun fetchItemCountForCategory(category: Int): Result<Int> {
        return listingsDB.getCategoryCount(category)
    }
}