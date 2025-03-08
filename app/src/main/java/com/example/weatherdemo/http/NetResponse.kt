package com.example.weatherdemo.http

import com.google.gson.Gson
import java.io.Serializable

open class NetResponse<T>(
    open val data: T? = null,
    open val code: Int,
    open val message: String? = null
) : Serializable {
    val isSuccess: Boolean
        get() = code == SUCCESS;

    companion object {
        val SUCCESS: Int = 0;
        val FAILED: Int = -1;
    }

    override fun toString(): String {
        return Gson().toJson(this);
    }
}