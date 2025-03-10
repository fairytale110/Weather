package com.example.weatherdemo.vm

import com.example.weatherdemo.bean.Weather
import com.example.weatherdemo.http.BaseRepository
import com.example.weatherdemo.http.NetResponse

class MainRepository: BaseRepository() {

    suspend fun getWeather(latitude: Double, longitude: Double): NetResponse<Weather?> {

        return request {
            mService.getWeather(latitude, longitude, "temperature_2m");
        }
    }
}