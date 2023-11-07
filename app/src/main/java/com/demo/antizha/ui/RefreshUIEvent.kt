package com.demo.antizha.ui

class RefreshUIEvent {
    companion object {
        public val SELECT_WEB_FRAGMENT = 104
    }
    var msgCode : Int
    constructor(msg : Int) {
        msgCode = msg
    }
}