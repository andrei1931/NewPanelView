package com.example.pv_menu

import com.google.gson.annotations.SerializedName

data class GenerationPower(
    @SerializedName("generationMW") val generationMW: Double,
    @SerializedName("timestamp") val timestamp: String
)
