package com.example.weatherdemo.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.weatherdemo.R
import com.example.weatherdemo.bean.Weather
import androidx.core.graphics.toColorInt
import com.example.weatherdemo.utils.weatherDayOfMonth
import com.example.weatherdemo.utils.weatherHour

/**
 * Weather data list adapter
 */
open class WeatherAdapter(weatherData: Weather?) : RecyclerView.Adapter<WeatherViewHolder>() {
    private var mWeatherData: Weather? = null;

    init {
        this.mWeatherData = weatherData;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_weather_temp, parent, false);
        return WeatherViewHolder(itemView);
    }

    override fun getItemCount(): Int {
        return mWeatherData?.hourly?.temperature_2m?.size ?: 0;
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val dateValue = mWeatherData?.hourly?.time?.get(position)?:"";
        var hourValue: String = dateValue.weatherHour();
        hourValue = if (hourValue.equals("00:00")) dateValue.weatherDayOfMonth() else hourValue;

        holder.setHourValue(hourValue)
        holder.setTemperatureValue("${mWeatherData?.hourly?.temperature_2m?.get(position)}${mWeatherData?.hourly_units?.temperature_2m ?: "℃"}")
        holder.itemView.setBackgroundColor(holder.itemView.context.getColor(if (position % 2 == 0) R.color.gray_item_background else R.color.white));
    }

    open fun setData(weatherData: Weather?) {
        mWeatherData = weatherData;
        notifyDataSetChanged();
    }
}

class WeatherViewHolder(itemView: View) : ViewHolder(itemView) {

    fun setHourValue(hour: String?) {
        itemView.findViewById<TextView>(R.id.text_weather_hour).text = hour ?: "";
    }

    fun setTemperatureValue(temperature: String?) {
        itemView.findViewById<TextView>(R.id.text_weather_temp).text = "${temperature ?: ""}";
    }
}