package com.demo.antizha.newwork

import com.demo.antizha.BuildConfig
import com.demo.antizha.interfaces.IApiResult
import com.demo.antizha.md.JniHandStamp
import com.demo.antizha.newwork.UnsafeOkHttpClient.getDataByPost
import com.demo.antizha.util.LogUtils
import okhttp3.Headers

object DictionaryUtils {
    var step: Int = 0
    var ask: ArrayList<String> = ArrayList()
    var answer: ArrayList<String> = ArrayList()

    init {
        ask.add("ProtorolVersion,SecretVersion,AnServiceAgreement,AnConcealAgreement,androidh5host,androidhandbook,failureNumStartValidate")
        ask.add("whiteTelList,connectionTel,virusUpdateIntervalHours")
    }

    class OnGetDictionary : IApiResult {
        override fun onError() {
            LogUtils.debug("OnGetDictionary Error", "")
        }

        override fun onSuccess(data: String) {
            LogUtils.debug("OnGetDictionary Success", data)
            onDictionaryGetted(data)
        }
    }

    fun getDictionary() {
        if (step == ask.size)
            return
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["dictionarykeys"] = ask[step]
        getDataByPost(
            BuildConfig.RELEASE_API_URL + "/api/AppConfig/getalldictionaryv3",
            bodyMap = JniHandStamp.princiHttp(hashMap),
            addHead = true,
            OnGetDictionary()
        )
    }

    fun onDictionaryGetted(data: String) {
        val text = JniHandStamp.getSData(data)
        answer.add(text)
        step += 1
        getDictionary()
    }
}