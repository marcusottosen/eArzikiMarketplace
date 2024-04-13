package com.example.earzikimarketplace

import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseClientFactory
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseClientNotInitializedException
import com.example.earzikimarketplace.data.model.supabaseAdapter.SupabaseManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.PostgrestBuilder
import io.github.jan.supabase.postgrest.query.PostgrestResult
import io.ktor.http.Headers
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testng.annotations.BeforeTest
import java.nio.file.Path
import kotlin.test.assertFailsWith


class SupabaseManagerTest {

    private lateinit var factory: SupabaseClientFactory
    private lateinit var supabaseClient: SupabaseClient

    @BeforeEach
    fun setUp() {
        // Mock the factory and the SupabaseClient
        factory = mockk()
        supabaseClient = mockk(relaxed = true)

        // Setup the mock to return the mocked SupabaseClient when createSupabaseClient is called
        every { factory.createSupabaseClient(any(), any()) } returns supabaseClient
    }

    @Test
    fun `initialize Client successfully`() {
        SupabaseManager.initializeClient("api-key", "api-url", factory)

        // Verify client was initialized
        assertEquals(supabaseClient, SupabaseManager.getClient())
    }

    @Test
    fun `getClient throws if not initialized`() {
        assertFailsWith<SupabaseClientNotInitializedException> {
            SupabaseManager.getClient()
        }
    }
}


class MainKtTestMock {

    private lateinit var supabaseClient : SupabaseClient

    @BeforeTest
    fun setUp() {

        supabaseClient = mockk<SupabaseClient>()
        val postgrest = mockk<Postgrest>()
        val postgrestBuilder = mockk<PostgrestBuilder>()
        val postgrestResult = PostgrestResult(body = null, headers = Headers.Empty)

        every { supabaseClient.postgrest } returns postgrest
        every { postgrest["path"] } returns postgrestBuilder
        coEvery { postgrestBuilder.insert(values = any<List<Path>>()) } returns postgrestResult
    }
}