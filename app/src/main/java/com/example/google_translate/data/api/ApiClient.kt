package com.example.google_translate.data.api

import android.content.Context
import androidx.viewbinding.BuildConfig
import com.example.google_translate.utils.Constants.ACCEPT_ENCODING
import com.example.google_translate.utils.Constants.BASE_URL
import com.example.google_translate.utils.Constants.CONTENT_TYPE
import com.example.google_translate.utils.Constants.X_RAPID_API_HOST
import com.example.google_translate.utils.Constants.X_RAPID_API_KEY
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


object ApiClient {

    private val client = buildClient()

    private val retrofit = buildRetrofit(client)


    private fun buildRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private fun buildClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val builder = OkHttpClient.Builder()
        try {
            builder.callTimeout(1, TimeUnit.MINUTES)
                .addNetworkInterceptor(Interceptor { chain ->
                    var request = chain.request()
                    val builder = request.newBuilder()
                    builder.addHeader("Accept", "application/json")
                    request = builder.build()
                    chain.proceed(request)
                })
        } catch (e: Exception) {
            when (e) {
                is SocketTimeoutException -> {
                    throw SocketTimeoutException()
                }
            }
        }

        if (BuildConfig.DEBUG) {
            // Debug holatdan keyin o`chirish kerak!!!!!!!!!!!!!!!!!!!!!!!
            builder.addInterceptor(interceptor)
            builder.addNetworkInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
        return builder.build()
    }

    fun <T> createServiceWithAuth(service: Class<T>?): T {
        val newClient =
            client.newBuilder().addInterceptor(Interceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.addHeader("content-type", CONTENT_TYPE)
                builder.header("Accept-Encoding", ACCEPT_ENCODING)
                builder.header("X-RapidAPI-Host", X_RAPID_API_HOST)
                builder.header("X-RapidAPI-Key", X_RAPID_API_KEY)
                chain.proceed(builder.build())
            })
                .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
        val newRetrofit = retrofit.newBuilder().client(newClient).build()
        return newRetrofit.create(service!!)
    }
}

