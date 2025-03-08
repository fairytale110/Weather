package com.example.weatherdemo

import android.app.Application

class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this;
    }

    companion object {
        private lateinit var application: WeatherApplication;

        fun getApplication(): WeatherApplication {
            return this.application;
        }
    }
}