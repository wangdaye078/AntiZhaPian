package com.demo.antizha.ui

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message

abstract class DownTimer {
    private var time: Long = 0

    private var interval: Long = 0

    private var step = 0

    private var stoped: Boolean

    var handler: Handler

    var runnable: Runnable

    inner class HandlerC10629a internal constructor(looper: Looper?) : Handler(
        looper!!) {
        override fun handleMessage(message: Message) {
            super.handleMessage(message)
            if (message.what == 1) {
                val j = time - interval * step
                if (j <= 0) {
                    step = 0
                    val downTimer = this@DownTimer
                    downTimer.handler.removeCallbacks(downTimer.runnable)
                    onTimer()
                    return
                }
                onTimeChang(j)
                updataStep(this@DownTimer)
                val downTimer2 = this@DownTimer
                downTimer2.handler.postDelayed(downTimer2.runnable, downTimer2.interval)
            }
        }
    }

    constructor(time: Long, interval: Long) {
        this.stoped = false
        this.handler = HandlerC10629a(Looper.getMainLooper())
        this.runnable = Runnable {
            begin()
        }
        this.time = time
        this.interval = interval
        this.step = 0
        this.stoped = false
        this.handler.post(runnable)
    }

    abstract fun onTimeChang(j: Long)

    abstract fun onTimer()

    fun isStoped(): Boolean {
        return if (Build.VERSION.SDK_INT >= 29) {
            !handler.hasCallbacks(runnable)
        } else stoped
    }

    fun begin() {
        handler.sendEmptyMessage(1)
    }

    fun reset(time: Long, interval: Long) {
        this.time = time
        this.interval = interval
        this.step = 0
        this.stoped = false
        this.handler.post(this.runnable)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
        stoped = true
    }

    constructor() {
        stoped = false
        handler = HandlerC10629a(Looper.getMainLooper())
        runnable = Runnable {
            begin()
        }
    }

    companion object {
        fun updataStep(downTimer: DownTimer): Int {
            val i = downTimer.step
            downTimer.step = i + 1
            return i
        }
    }
}