package com.example.weatherdemo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.weatherdemo.R

/**
 * Main activity
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main);
    }
}