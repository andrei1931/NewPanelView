package com.example.pv_menu.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pv_menu.DashboardRepository
import com.example.pv_menu.GenerationPower
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val _powerData = MutableLiveData<Map<String, List<GenerationPower>>>()
    val powerData: LiveData<Map<String, List<GenerationPower>>> get() = _powerData

    fun fetchPowerDataForSystem(systemId: String, start: String, end: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = mutableMapOf<String, List<GenerationPower>>()

                val generationPowerList = repository.fetchData(systemId, start, end)
                data[systemId] = generationPowerList

                _powerData.postValue(data)
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Failed to fetch power data", e)
            }
        }
    }
    fun setCachedPowerData(data: Map<String, List<GenerationPower>>) {
        _powerData.postValue(data)
    }
}
