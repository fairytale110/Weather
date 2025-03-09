package com.example.weatherdemo.base

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity<VB : ViewDataBinding, VM: BaseViewModel> : ComponentActivity() {
    private val TAG: String = "BaseViewBindingActivity";

    protected lateinit var mViewBinding: VB;

    protected lateinit var mViewModel: VM;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = DataBindingUtil.inflate<VB>(layoutInflater, getLayoutResourceId(), null, false);
        setContentView(mViewBinding.root);
        mViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[getVmClass()];

        initView();
        initData();
    }

    @LayoutRes protected abstract fun getLayoutResourceId(): Int;

    protected abstract fun getVmClass(): Class<VM>;

    protected open fun initView() {
        log("initView");
    }

    protected open fun initData() {
        log("initData");
    }

    protected open fun log(message: String?) {
        message?.run {
            Log.i("App","$message");
        }
    }
}