package com.example.pv_menu

import androidx.lifecycle.MutableLiveData

class PublicMutableLiveData<T>(initialValue: T) : MutableLiveData<T>(initialValue) {
    override fun setValue(value: T) {
        super.setValue(value)
    }
}