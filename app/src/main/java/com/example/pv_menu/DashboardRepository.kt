package com.example.pv_menu
import android.util.Log

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import retrofit2.Callback
import com.example.pv_menu.ApiService
import com.example.pv_menu.ApiClient
import com.example.pv_menu.Power
import retrofit2.Call
import retrofit2.Response
import java.util.*


class DashboardRepository(private val apiService: ApiService) {

    suspend fun fetchData(start: String, end: String): List<GenerationPower> {
        return apiService.getPowerData(start, end)
    }
}
