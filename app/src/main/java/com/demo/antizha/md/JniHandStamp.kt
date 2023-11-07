package com.demo.antizha.md

import android.text.TextUtils
import com.demo.antizha.BuildConfig
import com.demo.antizha.interfaces.IApiResult
import com.demo.antizha.interfaces.IOneClickListener
import com.demo.antizha.newwork.UnsafeOkHttpClient
import com.demo.antizha.util.LogUtils
import com.demo.antizha.util.ResponseDataTypeAdaptor
import com.google.gson.Gson
import com.openssl.md.JniHand
import okhttp3.Headers
import java.util.concurrent.atomic.AtomicLong


object JniHandStamp {
    var jni = JniHand()
    var peer: String = ""
    var peerGetted = false

    var atomicInte: AtomicLong = AtomicLong(1L)
    var expireTime: Long = 0      //返回值是300，代表300秒后，peer过期
    var isExpireQurey = false       //peer是否有效
    var lastLocal: Long = 0
    var identity: String = ""

    class OnGetPeer : IApiResult {
        var iOneClickListener: IOneClickListener

        constructor(iOneClickListener: IOneClickListener) {
            this.iOneClickListener = iOneClickListener
        }

        override fun onError() {
            LogUtils.debug("OnGetPeer Error", "")
        }

        override fun onSuccess(data: String) {
            LogUtils.debug("OnGetPeer Success", data)
            onPeerGetted(data)
            iOneClickListener.clickOKBtn()
        }
    }

    @Synchronized
    fun <T> princiHttp(t: T?): HashMap<String, String> {
        val hashMap: HashMap<String, String> = HashMap()
        if (t != null) {
            val m21549a: String = ResponseDataTypeAdaptor.buildGson().toJson(t)
            val paramJsonEncode = jni.paramJsonEncode(m21549a)
            hashMap["data"] = paramJsonEncode
        }
        return hashMap
    }

    fun handshareKeySyn(iOneClickListener: IOneClickListener) {
        //如果有效，就不更新peer
        if (this.isExpireQurey) {
            iOneClickListener.clickOKBtn()
            return
        }
        val appPubKey = jni.getAppPubKey()
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["peer"] = appPubKey
        UnsafeOkHttpClient.getDataByPostSyn(
            /*"https://192.168.2.101/hicore/api/Account/handshark",*/
            BuildConfig.RELEASE_API_URL + "/api/Account/handshark",
            bodyMap = hashMap,
            addHead = true,
            OnGetPeer(iOneClickListener))
    }

    fun handshareKeyAsyn(iOneClickListener: IOneClickListener) {
        val appPubKey = jni.getAppPubKey()
        /*
        jni.setApiPubKey("MIIBSzCCAQMGByqGSM49AgEwgfcCAQEwLAYHKoZIzj0BAQIhAP////8AAAABAAAAAAAAAAAAAAAA////////////////MFsEIP////8AAAABAAAAAAAAAAAAAAAA///////////////8BCBaxjXYqjqT57PrvVV2mIa8ZR0GsMxTsPY7zjw+J9JgSwMVAMSdNgiG5wSTamZ44ROdJreBn36QBEEEaxfR8uEsQkf4vOblY6RA8ncDfYEt6zOg9KE5RdiYwpZP40Li/hp/m47n60p8D54WK84zV2sxXs7LtkBoN79R9QIhAP////8AAAAA//////////+85vqtpxeehPO5ysL8YyVRAgEBA0IABEDb3semEzNTxYUtEa7PAEAfLrTQoxZAwsmBFuU6WwBFRnhV4CaPkQJ1WFRPWTfo4mps/y8V9ViePKh/8rNHE+w=".replace(
            "(.{64})".toRegex(),
            "$1\n").trim())
        val t1 = jni.paramJsonEncode("0123456789abcdef")
        val t2 = jni.paramJsonEncode("0123456789abcde")
        */
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["peer"] = appPubKey
        UnsafeOkHttpClient.getDataByPost(
            /*"https://192.168.2.101/hicore/api/Account/handshark",*/
            BuildConfig.RELEASE_API_URL + "/api/Account/handshark",
            bodyMap = hashMap,
            addHead = true,
            OnGetPeer(iOneClickListener))
    }

    class PeerBody(val peer: String, val identity: String, val expire: String, val seqid: String)
    class PeerData(val data: PeerBody?, val code: Int)

    class SData(val sData: String)

    fun getSData(data: String): String {
        if (data.isEmpty())
            return String()
        if (data[0] != '{')
            return String()
        val hashMap = Gson().fromJson(data, HashMap::class.java)
        if (!hashMap.containsKey("sData"))
            return String()
        val text = jni.paramJsonDecode(hashMap["sData"].toString())
        return text
    }

    fun onPeerGetted(data: String) {
        if (data.isEmpty())
            return
        if (data[0] != '{')
            return
        val peerData = Gson().fromJson(data, PeerData::class.java)
        if (peerData.code != 0)
            return
        peer = peerData.data!!.peer.replace("(.{64})".toRegex(), "$1\n").trim()
        jni.setApiPubKey(peer)
        identity = jni.paramJsonDecode(peerData.data.identity)
        val seqid = jni.paramJsonDecode(peerData.data.seqid)
        if (!TextUtils.isEmpty(seqid)) {
            atomicInte.set(seqid.toLong())
        }

        val expire = jni.paramJsonDecode(peerData.data.expire)
        if (!TextUtils.isEmpty(expire)) {
            expireTime = expire.toLong()
        }
        lastLocal = System.currentTimeMillis()
        isExpireQurey = true
        peerGetted = true
    }

    fun getSeqID(): String {
        if (!peerGetted)
            return ""
        return jni.paramJsonEncode(this.atomicInte.incrementAndGet().toString())
    }

    @Synchronized
    fun expireFail(ForceExpiration: Boolean): Boolean {
        if (ForceExpiration) {
            isExpireQurey = false
            return true
        } else if (isNeedDelay()) {
            return false
        } else {
            if (expireTime - (System.currentTimeMillis() - lastLocal) / 1000 > 0) {
                return false
            }
            //peer没有在4-5分钟之间被重置，所以过期了。
            isExpireQurey = false
            return true
        }
    }

    fun isNeedDelay(): Boolean {
        val currentTimeMillis = (System.currentTimeMillis() - lastLocal) / 1000
        val j = expireTime
        if (j - currentTimeMillis <= 0 || j - currentTimeMillis >= 60) {
            //初始化后0-4分钟走这
            return false
        }
        //初始化后4-5分钟走这。
        //如果4-5分钟之间有发数据，那这个peer的有效期将从当前时间重置
        lastLocal = System.currentTimeMillis()
        return true
    }

}