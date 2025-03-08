package com.example.weatherdemo.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherdemo.base.BaseViewModel
import com.example.weatherdemo.bean.Weather
import com.example.weatherdemo.http.NetResponse
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : BaseViewModel(application) {
    private val repository: MainRepository = MainRepository();

    val weatherData: MutableLiveData<NetResponse<Weather?>> = MutableLiveData();

    fun getWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            weatherData.value = repository.getWeather(latitude, longitude);
        }
    }

}