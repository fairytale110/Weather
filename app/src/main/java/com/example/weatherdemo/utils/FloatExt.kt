package com.example.weatherdemo.utils

fun Float.fix(): Float {
    return "%.1f".format(this).toFloat();
}