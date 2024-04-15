package com.example.pv_menu.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pv_menu.DashboardRepository
import com.example.pv_menu.GenerationPower

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val _powerData = MutableLiveData<List<GenerationPower>>()
    val powerData: LiveData<List<GenerationPower>>
        get() = _powerData

    fun fetchPowerData(start: String, end: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val data = repository.fetchData(start, end)
            _powerData.postValue(data)
        }
    }
}
