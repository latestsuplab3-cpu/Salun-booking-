package com.example.data.remote.supabase

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object SupabaseConfig {
    const val PROJECT_ID = "llhyjuqthwsdmauvucqc"
    val BASE_URL: String = if (BuildConfig.SUPABASE_URL.isNotBlank()) BuildConfig.SUPABASE_URL else "https://llhyjuqthwsdmauvucqc.supabase.co"
    val API_KEY: String = if (BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) BuildConfig.SUPABASE_ANON_KEY else "sb_publishable_paPvi-O_zUaqU5XovfFeZA_9sZKzdIk"
}

/**
 * Direct REST Client for Supabase PostgREST API.
 * Uses OkHttp which is already bundled in the Android app.
 * Does not require external thick SDKs, guaranteeing 100% build stability.
 */
class SupabaseClient(
    private val baseUrl: String = SupabaseConfig.BASE_URL,
    private val apiKey: String = SupabaseConfig.API_KEY
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private fun buildRequest(endpoint: String, method: String = "GET", body: String? = null): Request {
        val url = if (endpoint.startsWith("http")) endpoint else "$baseUrl/rest/v1/$endpoint"
        val builder = Request.Builder()
            .url(url)
            .addHeader("apikey", apiKey)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("Prefer", "return=representation")

        when (method.uppercase()) {
            "GET" -> builder.get()
            "POST" -> builder.post((body ?: "{}").toRequestBody(jsonMediaType))
            "PATCH" -> builder.patch((body ?: "{}").toRequestBody(jsonMediaType))
            "DELETE" -> builder.delete()
        }
        return builder.build()
    }

    suspend fun query(table: String, selectQuery: String = "*"): Result<String> = withContext(Dispatchers.IO) {
        try {
            val req = buildRequest("$table?select=$selectQuery", "GET")
            client.newCall(req).execute().use { res ->
                val body = res.body?.string() ?: ""
                if (res.isSuccessful) {
                    Result.success(body)
                } else {
                    Log.e("SupabaseClient", "Query error: ${res.code} - $body")
                    Result.failure(Exception("Supabase HTTP ${res.code}: $body"))
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseClient", "Network exception in query", e)
            Result.failure(e)
        }
    }

    suspend fun insert(table: String, jsonPayload: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val req = buildRequest(table, "POST", jsonPayload)
            client.newCall(req).execute().use { res ->
                val body = res.body?.string() ?: ""
                if (res.isSuccessful || res.code == 201) {
                    Result.success(body)
                } else {
                    Log.e("SupabaseClient", "Insert error: ${res.code} - $body")
                    Result.failure(Exception("Supabase HTTP ${res.code}: $body"))
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseClient", "Network exception in insert", e)
            Result.failure(e)
        }
    }

    suspend fun upsert(table: String, jsonPayload: String, onConflict: String = "id"): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/rest/v1/$table?on_conflict=$onConflict"
            val builder = Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "resolution=merge-duplicates,return=representation")
                .post(jsonPayload.toRequestBody(jsonMediaType))

            client.newCall(builder.build()).execute().use { res ->
                val body = res.body?.string() ?: ""
                if (res.isSuccessful || res.code == 201) {
                    Result.success(body)
                } else {
                    Log.e("SupabaseClient", "Upsert error: ${res.code} - $body")
                    Result.failure(Exception("Supabase HTTP ${res.code}: $body"))
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseClient", "Network exception in upsert", e)
            Result.failure(e)
        }
    }

    suspend fun update(table: String, matchColumn: String, matchValue: String, jsonPayload: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val endpoint = "$table?$matchColumn=eq.$matchValue"
            val req = buildRequest(endpoint, "PATCH", jsonPayload)
            client.newCall(req).execute().use { res ->
                val body = res.body?.string() ?: ""
                if (res.isSuccessful) {
                    Result.success(body)
                } else {
                    Log.e("SupabaseClient", "Update error: ${res.code} - $body")
                    Result.failure(Exception("Supabase HTTP ${res.code}: $body"))
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseClient", "Network exception in update", e)
            Result.failure(e)
        }
    }

    suspend fun testConnection(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val req = Request.Builder()
                .url("$baseUrl/rest/v1/")
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer $apiKey")
                .get()
                .build()

            client.newCall(req).execute().use { res ->
                if (res.isSuccessful || res.code == 200 || res.code == 404) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("Server returned status ${res.code}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
