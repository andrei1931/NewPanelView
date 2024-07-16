package com.example.pv_menu
import com.example.pv_menu.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http:// 10.111.11.21:8080/"




    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY  // Set log level as needed
    }

    val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
