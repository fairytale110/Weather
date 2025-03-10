package com.example.weatherdemo.utils

fun String.weatherHour(): String {
    return if (this.contains("T")) this.split("T")[1] else this;
}

fun String.weatherDayOfMonth(): String {
    try {
        val day = DateUtils.getDayOfMonth(this, DateUtils.FORMAT_YYYY_MM_DD_T_HH_MM);
        val month = DateUtils.getMonth(this, DateUtils.FORMAT_YYYY_MM_DD_T_HH_MM);
        return "$day ${DateUtils.getMonthShortName(month)}";
    } catch (error: Exception) {
        return this;
    }
}
