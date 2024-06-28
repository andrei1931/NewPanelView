package com.example.pv_menu

class DashboardRepository(private val apiService: ApiService) {

    suspend fun fetchData(systemId: String, start: String, end: String): List<GenerationPower> {
        return apiService.getPowerData(systemId, start, end)
    }
}
