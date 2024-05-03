package com.example.pv_menu

import android.content.Context

class SharedPrefsUtil(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences("date", Context.MODE_PRIVATE)

    fun saveData(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }
}
