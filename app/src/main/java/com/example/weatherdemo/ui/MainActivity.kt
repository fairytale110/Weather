package com.example.weatherdemo.ui

import com.example.weatherdemo.R
import com.example.weatherdemo.base.BaseActivity
import com.example.weatherdemo.bean.Weather
import com.example.weatherdemo.databinding.LayoutMainBinding
import com.example.weatherdemo.http.NetResponse
import com.example.weatherdemo.vm.MainViewModel

/**
 * Main activity
 */
class MainActivity : BaseActivity<LayoutMainBinding, MainViewModel>() {
    private val TAG: String = "MainActivity";
    private var latitude: Double = 52.52;
    private var longitude: Double = 13.419;

    override fun getLayoutResourceId(): Int = R.layout.layout_main;

    override fun getVmClass(): Class<MainViewModel> = MainViewModel::class.java

    override fun initView() {
        super.initView();
    }

    override fun initData() {
        super.initData()
        mViewModel.weatherData.observe(this, { data: NetResponse<Weather?> ->
            // dismiss loading
            if (data.isSuccess) {
                // handle request success
                log("${TAG} weather data size:${data.data?.hourly?.time?.size ?: 0}")
                return@observe
            }
            // handle request error
        });
        mViewModel.getWeather(latitude, longitude);
    }
}