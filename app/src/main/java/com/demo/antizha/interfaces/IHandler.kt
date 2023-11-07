package com.demo.antizha.interfaces

import android.os.Handler
import android.os.Looper
import android.os.Message


/* loaded from: classes3.dex */
object IHandler {
    private var listenerWebAct: HandleWebActListener? = null
    private var listenerWebDet: HandleWebDetailListener? = null
    private var listenerWebFlg: HandleWebFlgListener? = null
    var mHandler = Holder(Looper.getMainLooper())
    fun setHandleMsgListener(handleListener: HandleListener?) {
        when (handleListener) {
            is HandleWebFlgListener -> {
                listenerWebFlg = handleListener
            }
            is HandleWebActListener -> {
                listenerWebAct = handleListener
            }
            is HandleWebDetailListener -> {
                listenerWebDet = handleListener
            }
        }
    }

    interface HandleListener {
        fun handleMsg(message: Message?)

        companion object {
            val mHandler: Handler = IHandler.mHandler
        }
    }

    interface HandleWebActListener : HandleListener

    interface HandleWebDetailListener : HandleListener

    interface HandleWebFlgListener : HandleListener

    class Holder(looper: Looper?) : Handler(looper!!) {
        override fun handleMessage(message: Message) {
            super.handleMessage(message)
            if (listenerWebFlg != null) {
                listenerWebFlg!!.handleMsg(message)
            }
            if (listenerWebAct != null) {
                listenerWebAct!!.handleMsg(message)
            }
            if (listenerWebDet != null) {
                listenerWebDet!!.handleMsg(message)
            }
        }
    }
}