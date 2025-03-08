package com.example.weatherdemo.bean

import com.google.gson.Gson

/**
 * Weather data object
 */
data class Weather(
    var latitude: Double? = 52.52,
    var longitude: Double? = 13.419,
    var elevation: Double? = 44.812,
    var generationtime_ms: Double? = 2.2119,
    var utc_offset_seconds: Double? = 0.0,
    var timezone: String? = "Europe/Berlin",
    var timezone_abbreviation: String? = "CEST",
    var hourly: Hourly? = Hourly(),
    var hourly_units: HourlyUnits? = HourlyUnits()
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}

data class Hourly(
    var time: ArrayList<String>? = arrayListOf(),
    var temperature_2m: ArrayList<Double>? = arrayListOf()
)

data class HourlyUnits(
    var temperature_2m: String? = "°C"
)