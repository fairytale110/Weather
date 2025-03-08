package com.example.weatherdemo.http

import com.example.weatherdemo.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class RetrofitClient {

    companion object {
        val client = RetrofitClient();
        val apiService: ApiServer
            get() = client.getService();

    }

    private val API_HOST = "https://api.open-meteo.com";

    private var apiService: ApiServer? = null;

    private val retrofit: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .addInterceptor(getHttpLoggingInterceptor())
            .connectTimeout(10, TimeUnit.SECONDS)
        builder.build()
    }

    //
    private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.BASIC
        }
        return logging
    }

    open fun getService(): ApiServer {
        if (apiService == null) {
            apiService = Retrofit.Builder()
                .client(retrofit)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(API_HOST)
                .build()
                .create(ApiServer::class.java)
        }
        return apiService!!;
    }

}