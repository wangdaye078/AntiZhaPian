package com.demo.antizha.newwork

import android.text.TextUtils
import com.demo.antizha.UserInfoBean
import com.demo.antizha.interfaces.IOneClickListener
import com.demo.antizha.md.JniHandStamp
import com.demo.antizha.ui.HiCore
import com.demo.antizha.util.MD5Utils
import com.demo.antizha.util.SystemUtils
import com.google.gson.Gson
import okhttp3.*
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*


class RequestParamInterceptor : Interceptor {
    var gzip: Boolean = false

    constructor(_gzip: Boolean) {
        gzip = _gzip
    }

    private fun encodeHead(str: String): String? {
        return if (TextUtils.isEmpty(str)) {
            ""
        } else try {
            URLEncoder.encode(str, "UTF-8")
        } catch (err: UnsupportedEncodingException) {
            err.printStackTrace()
            ""
        }
    }

    private fun signStr(): String {
        val a2: String = MD5Utils.getMd5StringCharLowercase("hicore123")
        val a3: String = MD5Utils.getMd5StringCharLowercase(
            ("android/api/file/upload$a2").lowercase(Locale.getDefault())
        )
        return "android $a3"
    }

    private fun setHeader(builder: Request.Builder?) {
        if (builder != null) {
            if (gzip)
                builder.addHeader("Accept-Encoding", "gzip")
            builder.addHeader("deviceid", UserInfoBean.imei)
            builder.addHeader("identity", JniHandStamp.identity)
            builder.addHeader("seqid", JniHandStamp.getSeqID())
            if (JniHandStamp.isNeedDelay()) {
                //通知服务器，重置peer的有效期
                builder.addHeader("delay", "1")
            } else {
                builder.addHeader("delay", "0")
            }
            builder.addHeader("os-version", SystemUtils.getOsVer())
            builder.addHeader("os-type", "0")
            var os_brand = SystemUtils.getBrand()
            if (os_brand == "google")
                os_brand = "OnePlus"
            builder.addHeader("os-brand", os_brand)
            var os_model = SystemUtils.getModel()
            var os_model_sub = os_model.substring(0, 4)
            if (os_model_sub == "sdk_")
                os_model = "LE2117"
            builder.addHeader("os-model", os_model)
            //渠道标记，APK在渠道发布重新打包的时候会修改，比如豌豆荚是huawei，应用宝是yingyongbao
            builder.addHeader("market", HiCore.app.getChannel())
            builder.addHeader("app-version", UserInfoBean.version)
            builder.addHeader("imei", UserInfoBean.imei)
            builder.addHeader("app-version-code", UserInfoBean.innerVersion.toString() + "")
            builder.addHeader("api-version", "164")
            builder.addHeader("sign", signStr())
            builder.addHeader("UM-deviceToken", "")
            builder.addHeader("nodeid", UserInfoBean.adcode)
            //RegionConfigHttp.getNodeRegionId()
            builder.addHeader("nodeCode", UserInfoBean.adcode)
            //http://api.map.baidu.com/geocoder/v2/?ak=2ae1130ce176b453fb29e59a69b18407&callback=renderOption&output=xml&address=北京市.北京市.东城区&city=北京市
            //2.0.1开始，经纬度和地址都发的是空
            builder.addHeader("longitude", "0")
            builder.addHeader("latitude", "0")
            builder.addHeader("address", "")
            //token的格式类似如此，但是如果加上一个自己编的，那就会返回错误，如果不写，那某些安全级别低的就访问还能成功
            //val s = "Bearer 6v7eoQunKVCNJfY8K2BHVLRsPKVxrSL08l3i.H1ExaHIY0cvFNH1EQM2LiZY7bD9zIsgUwaSlgrSmbB2Hh8Y6vKiK3lS8fP40KLdJ3Weo0VenjprZiXsQsTIlJq4oRKAL8TsBBL4IgcE41pMHUX5JahP2QqGQjkwKankuZtqkpSGtn92Bt71GWaQL3jXwTxnukNDr4FLqAM6Z8OdtWAzznOSgegTF90yhrruEA5Yd8adfaUxWp2FoM96TqFe05LdV0AhSfRxu5KxW4DJMjuwMGcdzeWwSsmyPptpKe5VQVZvftLBkLPVN5xi4ciL47HudeksIPQuMTZYC0txMHLD2HLssS6pWPYvhZJ3c2O2avEsagmjT2g.H1q2zQUsbxW7oieMVAcCH63xEzkXVGvYkhjnrIJEhO5"
            builder.addHeader("Authorization", UserInfoBean.acctoken)
            builder.addHeader("policeToken", "")
        }
    }

    @Throws(IOException::class)
    private fun getParamContent(requestBody: RequestBody): String {
        val buffer = Buffer()
        requestBody.writeTo(buffer)
        return buffer.readUtf8()
    }

    //发送的如果是 {"data":"h7Xr14ZaOXw=="}这样的数据，那就把数据解密出来
    @Throws(IOException::class)
    private fun getDecodeParam(request: Request): String {
        val body = request.body()
        if (body != null && body !is FormBody && body !is MultipartBody && body.contentLength() > 0) {
            try {
                val optString: String = JSONObject(getParamContent(body)).optString("data")
                if (!TextUtils.isEmpty(optString)) {
                    return JniHandStamp.jni.paramJsonDecode(optString)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return ""
    }

    private fun infilterRequest(request: Request): Boolean {
        val httpUrl = request.url().toString()
        return httpUrl.contains("getalldictionaryv2") || httpUrl.contains("handshark") || httpUrl.contains(
            "oss-test.gjfzpt.cn") || httpUrl.contains("oss.gjfzpt.cn")
    }

    //如果发现peer已经过期，那就需要先更新，然后再发数据
    @Synchronized
    private fun handleRequest(_request: Request, param: String, status: Int): Request {
        var request = _request
        val body: RequestBody? = request.body()
        if (body == null) {
            return request;
        }
        if (infilterRequest(request)) {
            return request
        }
        if (body !is FormBody && body !is MultipartBody) {
            try {
                var jSONString = ""
                val hashMap = HashMap<Any, Any>()
                val newBuilder = request.newBuilder()
                if (JniHandStamp.expireFail(false) || status == 470) {
                    JniHandStamp.expireFail(true)
                    JniHandStamp.handshareKeySyn(object : IOneClickListener {
                        override fun clickOKBtn() {
                            newBuilder.header("identity", JniHandStamp.identity + "")
                            newBuilder.header("seqid", JniHandStamp.getSeqID())
                        }
                    })
                }
                if (!TextUtils.isEmpty(param)) {
                    hashMap["data"] = JniHandStamp.jni.paramJsonEncode(param)
                    jSONString = Gson().toJson(hashMap)
                }
                request = newBuilder.method(request.method(),
                    RequestBody.create(body.contentType(), jSONString)).build()
            } catch (unused: Exception) {
                return request
            }
        }
        return request
    }

    @Throws(IOException::class)
    private fun handleResponse(chain: Interceptor.Chain,
                               request: Request,
                               param: String): Response {
        val proceed = chain.proceed(request)
        if (proceed.code() == 470)
            return chain.proceed(handleRequest(request, param, 470))
        else
            return proceed
    }

    @Throws(IOException::class)  // okhttp3.Interceptor
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val newBuilder: Request.Builder = request.newBuilder()
        setHeader(newBuilder)
        if (request.body() is FormBody) {
            val builder = FormBody.Builder()
            val formBody = request.body() as FormBody
            for (i in 0 until formBody.size()) {
                builder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i))
            }
            newBuilder.method(request.method(), builder.build())
        }
        val build = newBuilder.build()
        val decodeParam = getDecodeParam(build)
        return handleResponse(chain, handleRequest(build, decodeParam, 0), decodeParam)
        //return chain.proceed(newBuilder.build())
    }

}