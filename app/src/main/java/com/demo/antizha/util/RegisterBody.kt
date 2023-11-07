package com.demo.antizha.util

class RegisterBody {
    var appVersion: String? = null
    var idNumber: String? = null
    var imei: String? = null
    var innerversion: String? = null
    var loginType: Int = 0
    var name: String? = null
    var os: Int = 0
    var osVersion: String? = null
    var pCode: String? = null
    var password: String? = null
    var phoneNum: String? = null
    var policeUserID: String? = null
    var region: String? = null
    var requestIP: String? = null
    var smsVerifyCode: String? = null
    var verificationLogID: String? = null

    override fun toString(): String {
        return "RegisterBody{phoneNum='$phoneNum', password='$password', appVersion='$appVersion', os=$os, osVersion='$osVersion', imei='$imei', requestIP='$requestIP', region='$region'}"
    }

}