package com.demo.antizha

import com.demo.antizha.util.Parameters

interface OnWebListener {
    fun shouldIntercept(aVar: Parameters?)
    fun webJsFinish()
}