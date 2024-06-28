package com.example.pv_menu

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/power")
    suspend fun getPowerData(
        @Query("systemId") systemId: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): List<GenerationPower>
}
