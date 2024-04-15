package com.example.pv_menu

data class GenerationPower(
    val generationMW: Double
) {
    override fun toString(): String {
        return  "$generationMW"
    }
}
