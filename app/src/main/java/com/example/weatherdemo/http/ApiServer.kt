package com.example.weatherdemo.http

import com.example.weatherdemo.bean.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServer {

    @GET("v1/forecast") // ?latitude=52.52&longitude=13.41&hourly=temperature_2m
    suspend fun getWeather(@Query("latitude") latitude: Double, @Query("longitude") longitude: Double, @Query("hourly") hourly: String): Weather?
}