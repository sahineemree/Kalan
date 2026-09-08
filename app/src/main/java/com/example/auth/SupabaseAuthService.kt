package com.example.auth

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class SupabaseAuthResult(
    val isSuccess: Boolean,
    val userId: String? = null,
    val email: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val errorMessage: String? = null
)

object SupabaseAuthService {
    private const val TAG = "SupabaseAuth"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private fun getBaseUrl(): String {
        return try {
            val url = BuildConfig.SUPABASE_URL.trim()
            if (url.endsWith("/")) url.dropLast(1) else url
        } catch (e: Exception) {
            "https://wrcuorhnvupadimpxdek.supabase.co"
        }
    }

    private fun getAnonKey(): String {
        return try {
            BuildConfig.SUPABASE_ANON_KEY.trim()
        } catch (e: Exception) {
            "sb_publishable_7RY5MTiFmr2pn_L5JidFyQ_GdfGhWZV"
        }
    }

    /**
     * Converts a username or raw email to a valid email format for Supabase Auth
     */
    fun normalizeEmail(input: String): String {
        val trimmed = input.trim().lowercase()
        return if (trimmed.contains("@")) {
            trimmed
        } else {
            // If user entered only username, map to domain format
            "${trimmed.replace(" ", "_")}@kalan.app"
        }
    }

    /**
     * Sign Up with Email & Password + user metadata
     */
    suspend fun signUp(
        emailOrUsername: String,
        password: String,
        displayName: String = "",
        username: String = ""
    ): SupabaseAuthResult = withContext(Dispatchers.IO) {
        val email = normalizeEmail(emailOrUsername)
        val url = "${getBaseUrl()}/auth/v1/signup"
        val anonKey = getAnonKey()

        try {
            val payload = JSONObject().apply {
                put("email", email)
                put("password", password)
                val metadata = JSONObject().apply {
                    put("display_name", displayName)
                    put("username", username.ifBlank { emailOrUsername.trim() })
                }
                put("data", metadata)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", anonKey)
                .addHeader("Authorization", "Bearer $anonKey")
                .addHeader("Content-Type", "application/json")
                .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            Log.d(TAG, "Sign up HTTP ${response.code}: $responseBody")

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val userObj = json.optJSONObject("user") ?: json
                val userId = userObj.optString("id")
                val accessToken = if (json.has("access_token")) json.optString("access_token") else null
                val refreshToken = if (json.has("refresh_token")) json.optString("refresh_token") else null

                SupabaseAuthResult(
                    isSuccess = true,
                    userId = userId,
                    email = email,
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            } else {
                val errorMsg = parseErrorMessage(responseBody, "Kayıt işlemi gerçekleştirilemedi.")
                SupabaseAuthResult(isSuccess = false, errorMessage = errorMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sign up exception", e)
            SupabaseAuthResult(
                isSuccess = false,
                errorMessage = "Bağlantı hatası: ${e.localizedMessage ?: "Lütfen internet bağlantınızı kontrol edin."}"
            )
        }
    }

    /**
     * Sign In with Email & Password
     */
    suspend fun signIn(
        emailOrUsername: String,
        password: String
    ): SupabaseAuthResult = withContext(Dispatchers.IO) {
        val email = normalizeEmail(emailOrUsername)
        val url = "${getBaseUrl()}/auth/v1/token?grant_type=password"
        val anonKey = getAnonKey()

        try {
            val payload = JSONObject().apply {
                put("email", email)
                put("password", password)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", anonKey)
                .addHeader("Authorization", "Bearer $anonKey")
                .addHeader("Content-Type", "application/json")
                .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            Log.d(TAG, "Sign in HTTP ${response.code}: $responseBody")

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val userObj = json.optJSONObject("user")
                val userId = userObj?.optString("id") ?: json.optString("id")
                val accessToken = json.optString("access_token")
                val refreshToken = json.optString("refresh_token")

                SupabaseAuthResult(
                    isSuccess = true,
                    userId = userId,
                    email = email,
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            } else {
                val errorMsg = parseErrorMessage(responseBody, "Kullanıcı adı veya şifre hatalı.")
                SupabaseAuthResult(isSuccess = false, errorMessage = errorMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sign in exception", e)
            SupabaseAuthResult(
                isSuccess = false,
                errorMessage = "Bağlantı hatası: ${e.localizedMessage ?: "Lütfen internetinizi kontrol edin."}"
            )
        }
    }

    /**
     * Send Password Reset Email
     */
    suspend fun sendPasswordReset(
        emailOrUsername: String
    ): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val email = normalizeEmail(emailOrUsername)
        val url = "${getBaseUrl()}/auth/v1/recover"
        val anonKey = getAnonKey()

        try {
            val payload = JSONObject().apply {
                put("email", email)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", anonKey)
                .addHeader("Authorization", "Bearer $anonKey")
                .addHeader("Content-Type", "application/json")
                .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                Pair(true, "$email adresine şifre sıfırlama bağlantısı gönderildi.")
            } else {
                val msg = parseErrorMessage(responseBody, "Şifre sıfırlama talebi gönderilemedi.")
                Pair(false, msg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Recover exception", e)
            Pair(false, "Bağlantı hatası: ${e.localizedMessage}")
        }
    }

    /**
     * Sign Out
     */
    suspend fun signOut(accessToken: String?): Boolean = withContext(Dispatchers.IO) {
        if (accessToken.isNullOrBlank()) return@withContext true
        val url = "${getBaseUrl()}/auth/v1/logout"
        val anonKey = getAnonKey()

        try {
            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", anonKey)
                .addHeader("Authorization", "Bearer $accessToken")
                .post("{}".toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            Log.e(TAG, "Sign out exception", e)
            true
        }
    }

    private fun parseErrorMessage(jsonStr: String, defaultMsg: String): String {
        return try {
            val json = JSONObject(jsonStr)
            val msg = json.optString("msg", "")
            val errorDescription = json.optString("error_description", "")
            val message = json.optString("message", "")
            val error = json.optString("error", "")

            val raw = msg.ifBlank { errorDescription }.ifBlank { message }.ifBlank { error }
            if (raw.isBlank()) return defaultMsg

            when {
                raw.contains("Invalid login credentials", ignoreCase = true) -> "Kullanıcı adı veya şifre hatalı."
                raw.contains("User already registered", ignoreCase = true) -> "Bu kullanıcı / e-posta ile zaten kayıtlı bir hesap var."
                raw.contains("Password should be at least", ignoreCase = true) -> "Şifreniz en az 6 karakter olmalıdır."
                raw.contains("rate limit", ignoreCase = true) -> "Çok fazla istek gönderildi. Lütfen biraz bekleyin."
                else -> raw
            }
        } catch (e: Exception) {
            defaultMsg
        }
    }
}
