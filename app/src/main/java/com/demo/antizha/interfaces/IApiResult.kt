package com.demo.antizha.interfaces

import okhttp3.Headers

interface IApiResult {
    fun onSuccess(data: String)
    fun onError()
}