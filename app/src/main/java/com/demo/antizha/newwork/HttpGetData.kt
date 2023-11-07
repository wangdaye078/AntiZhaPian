package com.demo.antizha.newwork

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.demo.antizha.interfaces.IApiResult
import com.demo.antizha.ui.HiCore
import com.demo.antizha.util.LogUtils
import com.google.gson.Gson
import okhttp3.*
import java.io.*
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPInputStream
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object UnsafeOkHttpClient {
    var mHandler = object : Handler(Looper.getMainLooper()) {}
    val timeOut: Long = 6
    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>,
                                        authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>,
                                        authType: String) {
        }

        override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
            return arrayOf()
        }
    })

    fun getBuilder(): OkHttpClient.Builder {
        //创建不安全的SSL，忽略证书错误，这样就可以用自己的服务器了。
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        val hostnameVerifier = HostnameVerifier { _, _ -> true }
        builder.hostnameVerifier(hostnameVerifier)
        return builder
    }

    /*观察者模式代码
    private fun getObservable(call: Call): Observable<String> {
        return Observable.create { emitter: ObservableEmitter<String> ->
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    LogUtils.debug("getObservable ", "onFailure")
                    emitter.onError(e)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    try {
                        LogUtils.debug("getObservable ", "onResponse")
                        //如果不手动设置gzip，让okhttp自动处理，如果返回的数据有问题，调用bytes()的时候会异常
                        //如果手动设置gzip，如果返回的数据有问题，则在decodeData里会抛出异常
                        val bodyString = decodeData(response.body()?.bytes(), response.headers())
                        emitter.onNext(bodyString)
                        emitter.onComplete()
                    } catch (e: IOException) {
                        LogUtils.debug("getObservable ", "onResponse data error")
                        emitter.onError(e)
                    }
                }
            })
        }
    }

    class MiddleSubscriber : Observer<String> {
        private var iApiResult: IApiResult

        constructor(iApiResult: IApiResult) {
            this.iApiResult = iApiResult
        }

        override fun onSubscribe(d: Disposable) {
        }

        override fun onNext(t: String) {
            LogUtils.debug("MiddleSubscriber ", "onNext")
            iApiResult.onSuccess(t)
        }

        override fun onError(e: Throwable) {
            iApiResult.onError()
        }

        override fun onComplete() {
        }

    }
    */

    fun createCall(request: Request, addHead: Boolean, addGzip: Boolean): Call {
        val builder = getBuilder()
        if (addHead)
            builder.addInterceptor(RequestParamInterceptor(addGzip))
        builder.dns(HookDns()).retryOnConnectionFailure(true)
            .connectTimeout(timeOut, TimeUnit.SECONDS)
            .readTimeout(timeOut, TimeUnit.SECONDS)
            .writeTimeout(timeOut, TimeUnit.SECONDS)
        val client = builder.build()
        return client.newCall(request)
    }

    fun asynchronousCall(call: Call, iApiResult: IApiResult) {
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                LogUtils.debug("asynchronousCall ", "onFailure")
                //如果需要，也应该改成mHandler.post
                iApiResult.onError()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                LogUtils.debug("asynchronousCall ", "onResponse")
                try {
                    //如果不手动设置gzip，让okhttp自动处理，如果返回的数据有问题，调用bytes()的时候会异常
                    //如果手动设置gzip，如果返回的数据有问题，则在decodeData里会抛出异常
                    val bodyString = decodeData(response.body()?.bytes(), response.headers())
                    mHandler.post(object : Runnable {
                        override fun run() {
                            iApiResult.onSuccess(bodyString)
                        }
                    })
                } catch (e: IOException) {
                    LogUtils.debug("asynchronousCall ", "onResponse data error")
                    onFailure(call, e)
                }
            }
        })
    }

    fun callRequest(request: Request, addHead: Boolean, addGzip: Boolean, iApiResult: IApiResult) {
        val call = createCall(request, addHead, addGzip)
        asynchronousCall(call, iApiResult)
        /*观察者模式代码
        val observable = getObservable(call)
        val middleSubscriber = MiddleSubscriber(iApiResult)
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(middleSubscriber)
         */
    }

    fun getDataByGet(url: String, addHead: Boolean, iApiResult: IApiResult) {
        val requestBuilder = Request.Builder().get().url(url)
        val request = requestBuilder.build()
        LogUtils.debug("getDataByGet ", url)

        callRequest(request, addHead, true, iApiResult)
    }

    fun getDataByPost(url: String,
                      bodyMap: HashMap<String, String>?,
                      addHead: Boolean,
                      iApiResult: IApiResult) {

        val json = if (bodyMap == null) "{}" else Gson().toJson(bodyMap)
        val requestBuilder = Request.Builder()
            .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
            .url(url)
        val request = requestBuilder.build()
        LogUtils.debug("getDataByPost ", url)

        callRequest(request, addHead, false, iApiResult)
    }

    fun getDataByPostSyn(url: String,
                         bodyMap: HashMap<String, String>?,
                         addHead: Boolean,
                         iApiResult: IApiResult) {
        try {
            val json = if (bodyMap == null) "{}" else Gson().toJson(bodyMap)
            val requestBuilder = Request.Builder()
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                .url(url)
            val request = requestBuilder.build()
            LogUtils.debug("getDataByPostSyn ", url)

            val call = createCall(request, false, addHead)
            //痛步请求
            try {
                val response = call.execute()
                val bodyString = decodeData(response.body()?.bytes(), response.headers())
                iApiResult.onSuccess(bodyString)
            } catch (e: IOException) {
                iApiResult.onError()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            //callBackFunc("")
        }
    }
}

fun bodyIsGzip(array: ByteArray): Boolean {
    if (array.size < 6)
        return false
    if (array[1] != 31.toByte())
        return false
    if (array[2] != 139.toByte())
        return false
    if (array[3] != 8.toByte())
        return false
    return true
}

@Throws(IOException::class)
fun decodeData(data: ByteArray?, headers: Headers): String {
    var iszip = false
    val encoding = headers.get("Content-Encoding")
    if (encoding != null && TextUtils.equals("gzip", encoding))
        iszip = true
    if (iszip != bodyIsGzip(data!!)) {
        LogUtils.debug("decodeData ", "gzip error")
        throw IOException()
    }
    val bodyString: String
    if (bodyIsGzip(data)) {
        var baos = ByteArrayOutputStream()
        val gzipIn = GZIPInputStream(ByteArrayInputStream(data))
        var readByte: Int
        while (gzipIn.read().also { readByte = it } != -1) baos.write(readByte)
        bodyString = baos.toString("UTF-8")
    } else {
        bodyString = data.toString(charset("UTF-8"))
    }
    return bodyString
}

fun saveBuff2File(data: String, saveFile: String) {
    val path = HiCore.context.getExternalFilesDir(null)?.path
    val file = File(path, saveFile)
    val fileWriter = FileOutputStream(file, false)
    fileWriter.write(data.toByteArray(charset("UTF_8")))
    fileWriter.close()

}

fun loadBuff4File(readFile: String): String {
    val path = HiCore.context.getExternalFilesDir(null)?.path
    val file = File(path, readFile)
//file.exists()总是返回false
    if (!file.canRead())
        return ""
    val inStream = FileInputStream(file)
    val inputReader = InputStreamReader(inStream, charset("UTF_8"))
    inputReader.encoding
    val buff = inputReader.readText()
    inStream.close()
    return buff
}