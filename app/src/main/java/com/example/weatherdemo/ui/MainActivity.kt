package com.example.weatherdemo.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherdemo.R
import com.example.weatherdemo.base.BaseActivity
import com.example.weatherdemo.bean.Weather
import com.example.weatherdemo.databinding.LayoutMainBinding
import com.example.weatherdemo.http.NetResponse
import com.example.weatherdemo.view.WeatherAdapter
import com.example.weatherdemo.vm.MainViewModel


/**
 * Main activity
 */
class MainActivity : BaseActivity<LayoutMainBinding, MainViewModel>() {
    private val TAG: String = "MainActivity";
    private val LOCATION_CODE: Int = 9001;
    private lateinit var locationManager: LocationManager;
    private var locationProvider: String = "";
    private var latitude = 0.0;
    private var longitude = 0.0;
    private val adater: WeatherAdapter by lazy { WeatherAdapter(null) }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            latitude = location.latitude;
            longitude = location.longitude;
            mViewModel.getWeather(location.latitude, location.longitude);
        }
    }

    override fun getLayoutResourceId(): Int = R.layout.layout_main;

    override fun getVmClass(): Class<MainViewModel> = MainViewModel::class.java

    override fun initView() {
        super.initView();
        mViewBinding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mViewBinding.recyclerView.adapter = adater;
        mViewBinding.refreshLayout.setOnRefreshListener {
            mViewModel.getWeather(latitude, longitude);
        }
        mViewBinding.refreshLayout.isRefreshing = true;
        mViewBinding.textStatus?.visibility = View.VISIBLE;
    }

    override fun initData() {
        super.initData()
        getLocation()
        mViewModel.weatherData.observe(this, { data: NetResponse<Weather?> ->
            // dismiss loading
            mViewBinding.refreshLayout.isRefreshing = false;
            mViewBinding.textStatus?.visibility = View.GONE;
            if (data.isSuccess) {
                // handle request success
                log("${TAG} weather data size:${data.data?.hourly?.time?.size ?: 0}")
                adater.setData(data.data);
                mViewBinding.contentChart.setData(data.data?.hourly)
                return@observe
            }
            // handle request error
        });
    }

    private fun getLocation() {
        log("getLocation");
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager;
        //获取权限（如果没有开启权限，会弹出对话框，询问是否开启权限）
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) ||
            (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), LOCATION_CODE
            )
        } else {
            loadProviders();
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        when (requestCode) {
            LOCATION_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                try {
                    loadProviders();
                } catch (error: SecurityException) {
                    error.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Need location permission first", Toast.LENGTH_LONG).show()
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun loadProviders() {
        val providers = locationManager.getProviders(true)
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER
        }
        val location = locationManager.getLastKnownLocation(locationProvider)
        if (location != null) {
            mViewModel.getWeather(location.latitude, location.longitude);
        } else {
            locationManager.requestLocationUpdates(
                locationProvider,
                3000L,
                1F,
                locationListener
            )
        }
    }
}