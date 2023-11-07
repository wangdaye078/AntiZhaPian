package com.demo.antizha.ui.fragment.web

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WebViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is web Fragment"
    }
    val text: LiveData<String> = _text
}