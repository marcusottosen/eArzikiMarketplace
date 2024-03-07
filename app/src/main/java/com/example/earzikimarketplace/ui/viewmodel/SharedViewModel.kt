package com.example.earzikimarketplace.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.earzikimarketplace.data.model.dataClass.Listing

class SharedViewModel(private val startActivity: (Intent) -> Unit) : ViewModel() {
    private val _listing = MutableLiveData<Listing>()
    val listing: LiveData<Listing> get() = _listing

    fun setItem(listing: Listing) {
        _listing.value = listing
    }
    // Used for intent needed for WhatsApp API
    companion object {
        fun provideFactory(
            startActivity: (Intent) -> Unit
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
                    return SharedViewModel(startActivity) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

}


