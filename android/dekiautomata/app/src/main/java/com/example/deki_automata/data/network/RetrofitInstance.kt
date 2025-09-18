package com.example.deki_automata.data.network

import com.example.deki_automata.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private val baseUrl: String by lazy {
        val configuredUrl = BuildConfig.BASE_URL.trim()
        require(configuredUrl.isNotEmpty()) {
            "BASE_URL is not configured. Add BASE_URL to android/dekiautomata/local.properties before building the app."
        }
        val normalized = if (configuredUrl.endsWith('/')) configuredUrl else "$configuredUrl/"
        normalized.toHttpUrlOrNull()?.toString()
            ?: throw IllegalArgumentException("Invalid BASE_URL: $configuredUrl")
    }

    private val json = Json { ignoreUnknownKeys = true }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .apply {
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(loggingInterceptor)
            }
        }
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}
