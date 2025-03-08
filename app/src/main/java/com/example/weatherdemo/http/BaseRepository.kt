package com.example.weatherdemo.http

import android.util.Log

open class BaseRepository {
    protected open val mService:ApiServer by lazy {
        return@lazy RetrofitClient.apiService;
    }

    suspend fun <T> request(block: suspend () -> T?): NetResponse<T?> {
        runCatching {
            try {
                block.invoke()
            } catch (error: Exception) {
                Log.e("BaseRepository", "request error: ${error.message}");
                return NetResponse(code = NetResponse.FAILED, message = error.message);
            }
        }.onSuccess { data: T? ->
            Log.d("BaseRepository", "request onSuccess ${data.toString()}");
            return NetResponse(data, code = NetResponse.SUCCESS);
        }.onFailure { error: Throwable? ->
            Log.e("BaseRepository", "request onFailure");
            return NetResponse(code = NetResponse.FAILED, message = error?.message ?: "Unknown net error");
        }
        Log.e("BaseRepository", "request Unknown");
        return NetResponse(code = NetResponse.FAILED, message = "Unknown error");
    }
}