package com.example.earzikimarketplace.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.earzikimarketplace.R
import com.example.earzikimarketplace.data.model.dataClass.DBCategory
import com.example.earzikimarketplace.data.model.supabaseAdapter.ListingsDB
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseManager
import com.example.earzikimarketplace.data.model.supabaseAdapter.getLocalUserData
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _categories = MutableLiveData<List<DBCategory>>()
    val categories: LiveData<List<DBCategory>> = _categories
    //private val supabaseAdapter = SupabaseAdapter()
    private val listingsDB = ListingsDB()


    fun fetchCategories(context: Context) {
        // Use DBCategory's defaultCategories
        val initialCategories = DBCategory.defaultCategories
        _categories.value = initialCategories

        viewModelScope.launch {
            //DB user data test
            try {
                Log.d("HomeViewModel DB user TEST", "starting")
                val userInfo = SupabaseManager.getLoggedInUser()
                Log.d("HomeViewModel DB user TEST", userInfo.toString())
            }catch (e: Exception){
                Log.e("HomeViewModel DB user TEST", e.toString())
            }


            //Local user data test
            try {
                val localUser = getLocalUserData(context)
                Log.d("HomeViewModel local USER TEST", "the local users email is: ${localUser.email}")

            }catch (e: Exception){
                Log.e("HomeViewModel local user TEST", e.toString())
            }



            // Fetch item counts in parallel and update categories
            initialCategories.map { category ->
                async { category.copy(itemCount = fetchItemCountForCategory(context, category.categoryID)) }
            }.awaitAll().let { updatedCategories ->
                _categories.value = updatedCategories
            }
        }

        //val userTEST = getLocalUserData(context)
        //Log.d("HomeViewModel user EMAIL", userTEST.email)
    }

    private suspend fun fetchItemCountForCategory(context: Context, category: Int): Result<Int> {
        /*val results = listOf(1,2,3,4,5)
        val sum = results.sum()
        return Result.success(sum)*/

        return listingsDB.getCategoryCount(category)
    }
}