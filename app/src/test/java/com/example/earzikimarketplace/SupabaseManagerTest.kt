package com.example.earzikimarketplace

import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseClientFactory
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseManager
import io.mockk.mockk
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import io.github.jan.supabase.SupabaseClient

class SupabaseManagerTest {

    private lateinit var factory: SupabaseClientFactory
    private lateinit var supabaseClient: SupabaseClient

    @BeforeEach
    fun setUp() {
        // Initialize mocks
        factory = mockk()
        supabaseClient = mockk(relaxed = true)

        // returns the mocked client
        every { factory.createSupabaseClient(any(), any()) } returns supabaseClient
    }

    @Test
    fun `initializeClient sets up SupabaseClient correctly`() {
        val apiUrl: String = BuildConfig.ApiUrl
        val apiKey: String = BuildConfig.ApiKey

        SupabaseManager.initializeClient(apiKey, apiUrl, factory)

        // Verify that the factory was used
        verify { factory.createSupabaseClient(apiKey, apiUrl) }
    }
}