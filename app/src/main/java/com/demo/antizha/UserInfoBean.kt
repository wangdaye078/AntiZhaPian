package com.demo.antizha

import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.provider.Settings
import android.text.TextUtils
import com.demo.antizha.ui.HiCore
import com.demo.antizha.util.AddressBean
import com.demo.antizha.util.CRC64
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.hjq.toast.ToastUtils
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


object UserInfoBean {
    var perfectProgress: Int = 0     //进度
    var accountId: String = ""      //账号ID
    var clusterID: Long = 0
    var exp: Long = 0            //token的过期时间
    var imei: String = ""            //设备码
    var useorigimei: Boolean = false//是否使用原始的机器码
    var name: String = ""           //名字
    var id: String = ""             //身份证前后2个字符
    var mobileNumber: String = ""   //电话前2后3
    var region: String = ""         //地区,比如 安徽省.淮北市.杜集区
    var adcode: String = ""         //地区码，比如 安徽省淮北市杜集区 adcode:340602
    var addr: String = ""           //详细地址
    var professionName: String = "" //职业
    var acctoken: String = ""           //用户TOKEN只能正常注册登录后获得，否则服务器会拒绝
    var longitude: String = ""          //设置地区的经度，每天会略做随机修改
    var latitude: String = ""           //设置地区的纬度，每天会略做随机修改
    var refTudeTime: String = ""        //上次刷新经纬度的日期，和当前不同就该刷新了
    var version: String = ""
    var innerVersion = 0
    var getVerTime: String = ""        //上次获取新颁布的日期，和当前不同就该刷新了

    fun init() {
        val context: Context = HiCore.context
        val settings: SharedPreferences = context.getSharedPreferences("setting", 0)
        accountId = settings.getString("account", "").toString()
        imei = settings.getString("imei", "").toString()
        useorigimei = settings.getBoolean("originalimei", false)
        if (TextUtils.isEmpty(imei)) {
            val timei =
                Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            imei = if (useorigimei) timei else toHexStr(CRC64.digest(timei.toByteArray()).bytes)
        }
        name = settings.getString("name", "").toString()
        id = settings.getString("id", "").toString()
        mobileNumber = settings.getString("phone", "").toString()
        region = settings.getString("region", "").toString()
        adcode = settings.getString("adcode", "").toString()
        addr = settings.getString("address", "").toString()
        professionName = settings.getString("work", "").toString()
        longitude = settings.getString("longitude", "").toString()
        latitude = settings.getString("latitude", "").toString()
        refTudeTime = settings.getString("refTudeTime", "").toString()
        version = settings.getString("version", "2.0.8").toString()
        innerVersion = settings.getInt("innerVersion", 144)
        getVerTime = settings.getString("getVerTime", "").toString()

        clusterID = 365268909
        exp = (System.currentTimeMillis() + 86400000L * 30L) / 1000
        if (TextUtils.isEmpty(region)) {
            region = "北京市.北京市.东城区"
            adcode = "110101"
        }
        checkLongitudeLatitude()

        if (TextUtils.isEmpty(version)) {
            version = BuildConfig.VERSION_NAME
            innerVersion = BuildConfig.VERSION_CODE
        }
        getToken()
        calcProgress()
    }

    fun commit() {
        val context: Context = HiCore.context
        val settings: SharedPreferences = context.getSharedPreferences("setting", 0)
        val editor: SharedPreferences.Editor = settings.edit()
        editor.putString("account", accountId)
        editor.putString("imei", imei)
        editor.putBoolean("originalimei", useorigimei)
        editor.putString("name", name)
        editor.putString("id", id)
        editor.putString("phone", mobileNumber)
        editor.putString("region", region)
        editor.putString("adcode", adcode)
        editor.putString("address", addr)
        editor.putString("work", professionName)
        editor.putString("longitude", longitude)
        editor.putString("latitude", latitude)
        editor.putString("refTudeTime", refTudeTime)
        editor.putString("version", version)
        editor.putInt("innerVersion", innerVersion)
        editor.putString("getVerTime", getVerTime)
        editor.apply()
        calcProgress()
    }

    fun setVer(ver: String, verCode: Int) {
        if (!TextUtils.equals(ver, version) || innerVersion != verCode) {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // HH:mm:ss
            val currentDate = Date(System.currentTimeMillis())
            getVerTime = simpleDateFormat.format(currentDate)
            version = ver
            innerVersion = verCode
            commit()
        }
    }

    fun calcProgress() {
        perfectProgress = 0
        if (!TextUtils.isEmpty(name)) {
            perfectProgress += 30
        }
        if (!TextUtils.isEmpty(id)) {
            perfectProgress += 30
        }
        if (!TextUtils.isEmpty(region)) {
            perfectProgress += 20
        }
        if (!TextUtils.isEmpty(addr)) {
            perfectProgress += 20
        }
        if (perfectProgress >= 100) {
            perfectProgress = 100
        }
    }

    fun isVerified(): Boolean {
        if (TextUtils.isEmpty(name))
            return false
        if (TextUtils.isEmpty(id))
            return false
        if (TextUtils.isEmpty(mobileNumber))
            return false
        if (TextUtils.isEmpty(accountId))
            return false
        return true
    }

    fun checkLongitudeLatitude() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // HH:mm:ss
        val currentDate = Date(System.currentTimeMillis())
        val dateString = simpleDateFormat.format(currentDate)
        if (!dateString.equals(refTudeTime)) {
            val regions = TextUtils.split(region, "\\.")
            if (regions.size != 3)
                return
            val provinces: List<AddressBean.HiProvince> = AddressBean.getHiProvince()
            for (province in provinces) {
                if (province.name != regions[0])
                    continue
                for (city in province.cityList) {
                    if (city.name != regions[1])
                        continue
                    for (town in city.townList) {
                        if (town.name == regions[2]) {
                            val random = Random()
                            val flongitude =
                                town.longitude.toFloat() + (random.nextInt(200) - 100).toFloat() / 1000.0F
                            val flatitude =
                                town.latitude.toFloat() + (random.nextInt(200) - 100).toFloat() / 1000.0F
                            longitude = flongitude.toString()
                            latitude = flatitude.toString()
                            refTudeTime = dateString
                            commit()
                        }
                    }
                }
            }
        }
    }

    fun getToken() {
        try {
            ///sdcard/Android/data/com.demo.antizha/files/note_national.xml
            val path = HiCore.context.getExternalFilesDir(null)?.path
            val file = File(path, "note_national.xml")
            //file.exists()总是返回false
            if (!file.canRead())
                return
            val iStream = FileInputStream(file)
            val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
            val builder: DocumentBuilder = factory.newDocumentBuilder()
            val document: Document = builder.parse(iStream)
            val stringList: NodeList = document.getElementsByTagName("string")
            for (i in 0 until stringList.length) {
                val node = stringList.item(i)
                val nodeName = node.attributes.item(0).textContent
                if (nodeName.equals("sp_user_bean")) {
                    class TokenPackage(val token: String)

                    val value = node.textContent
                    val token = Gson().fromJson(value, TokenPackage::class.java)
                    if (token != null) {
                        val tokenSub: List<String> = token.token.split(".")
                        if (tokenSub.size != 3)
                            return
                        val payLoadJson =
                            String(Base64.getUrlDecoder().decode(tokenSub[1]), charset("UTF-8"))
                        val gsonBuilder = GsonBuilder().registerTypeAdapter(
                            PayLoads::class.java,
                            PayLoadAdapter()
                        )
                        val gson = gsonBuilder.create()
                        val pl = gson.fromJson(payLoadJson, PayLoads::class.java)
                        val currentTime = System.currentTimeMillis() / 1000
                        if (pl.getExp() < currentTime) {
                            ToastUtils.show("Token Is Expire!")
                            return
                        }
                        acctoken = token.token
                        return
                    }
                }
            }
        } catch (err: Exception) {

        }
    }

    private fun hmacSha1(seed: ByteArray, input: ByteArray): ByteArray? {
        val secretKeySpec = SecretKeySpec(seed, "HmacSHA256")
        return try {
            val instance: Mac = Mac.getInstance("HmacSHA256")
            instance.init(secretKeySpec)
            instance.doFinal(input)
        } catch (unused: InvalidKeyException) {
            null
        } catch (unused: NoSuchAlgorithmException) {
            null
        }
    }

    class TokenModel {
        var ClusterID: Long = 0
        lateinit var ID: String
        lateinit var MobileNumber: String
        lateinit var RegionCode: String
        var Role: String = "Client"
        var TokenType: Int = 1
    }

    open class PayLoad(var name: String, var value: Any)

    class PayLoads {
        var sub: ArrayList<PayLoad> = ArrayList<PayLoad>()
        fun getExp(): Long {
            for (i in sub) {
                if (i.name == "exp")
                    return i.value as Long
            }
            return 0
        }
    }

    class PayLoadAdapter : TypeAdapter<PayLoads>() {
        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: PayLoads) {
            out.beginObject()
            for (payLoad in value.sub) {
                when (payLoad.value) {
                    is String -> {
                        out.name(payLoad.name).value(payLoad.value as String)
                    }
                    is Long -> {
                        out.name(payLoad.name).value(payLoad.value as Long)
                    }
                    is Int -> {
                        out.name(payLoad.name).value(payLoad.value as Int)
                    }
                }
            }
            out.endObject()
        }

        @Throws(IOException::class)
        override fun read(jread: JsonReader?): PayLoads {
            val pl = PayLoads()
            if (jread == null)
                return pl
            jread.beginObject()
            while (jread.hasNext()) {
                when (val name = jread.nextName()) {
                    "exp" -> pl.sub.add(PayLoad("exp", jread.nextLong()))
                    else -> pl.sub.add(PayLoad(name, jread.nextString()))
                }
            }
            jread.endObject()
            return pl
        }
    }

    fun makeToken(): String {       //伪造TOKEN的算法应该是正确的，但是没有正确的seed，算不出正确的TOKEN
        val headerJson = """{"alg":"HS256","typ":"JWT"}"""
        val header = Base64.getUrlEncoder().withoutPadding().encodeToString(
            headerJson.toByteArray(StandardCharsets.UTF_8)
        )

        val gsonBuilder = GsonBuilder().registerTypeAdapter(PayLoads::class.java, PayLoadAdapter())
        val gson = gsonBuilder.create()

        val tm = TokenModel()
        tm.ClusterID = clusterID
        tm.ID = accountId
        tm.MobileNumber = mobileNumber
        tm.RegionCode = adcode

        val pl = PayLoads()
        pl.sub.add(PayLoad("TokenModel", gson.toJson(tm)))
        pl.sub.add(PayLoad("exp", exp))
        pl.sub.add(PayLoad("iss", "PreventFraudAPI"))
        pl.sub.add(PayLoad("aud", "PreventFraudAPP"))

        val payLoadJson = gson.toJson(pl)
        val payload = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(payLoadJson.toByteArray(StandardCharsets.UTF_8))
        val seed = "hicore2020051518".toByteArray(charset("UTF_8"))
        val value = ("$header.$payload").toByteArray(charset("UTF_8"))
        val sign = Base64.getUrlEncoder().withoutPadding().encodeToString(hmacSha1(seed, value))
        return String.format("Bearer %s.%s.%s", header, payload, sign)
    }
}


class Dp2Px(context: Context) {
    var density: Float = 0.0F
    fun dp2px(value: Int): Int {
        return (value * density + 0.5).toInt()
    }

    init {
        density = context.resources.displayMetrics.density
    }
}

lateinit var dp2px: Dp2Px

fun toHexStr(byteArray: ByteArray) =
    with(StringBuilder()) {
        byteArray.forEach {
            val hex = it.toInt() and (0xFF)
            val hexStr = Integer.toHexString(hex)
            if (hexStr.length == 1) append("0").append(hexStr)
            else append(hexStr)
        }
        toString()
    }

fun getRoundBitmapByShader(
    bitmap: Bitmap?,
    outWidth: Int,
    outHeight: Int,
    radius: Float,
    boarder: Float
): Bitmap? {
    if (bitmap == null) {
        return null
    }
    val height = bitmap.height
    val width = bitmap.width
    val widthScale = outWidth * 1f / width
    val heightScale = outHeight * 1f / height
    val matrix = Matrix()
    matrix.setScale(widthScale, heightScale)
    //创建输出的bitmap
    val desBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888)
    //创建canvas并传入desBitmap，这样绘制的内容都会在desBitmap上
    val canvas = Canvas(desBitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //创建着色器
    val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    //给着色器配置matrix
    bitmapShader.setLocalMatrix(matrix)
    paint.shader = bitmapShader
    //创建矩形区域并且预留出border
    val rect = RectF(boarder, boarder, (outWidth - boarder), (outHeight - boarder))
    //把传入的bitmap绘制到圆角矩形区域内
    canvas.drawRoundRect(rect, radius, radius, paint)
    if (boarder > 0) {
        //绘制boarder
        val boarderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        boarderPaint.color = Color.GREEN
        boarderPaint.style = Paint.Style.STROKE
        boarderPaint.strokeWidth = boarder
        canvas.drawRoundRect(rect, radius, radius, boarderPaint)
    }
    return desBitmap
}

fun str2time(str: String): Long {
    return try {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(str)
        date?.time ?: 0
    } catch (e2: Exception) {
        e2.printStackTrace()
        0
    }
}

fun optimizationTimeStr(str: String): String {
    if (TextUtils.isEmpty(str)) {
        return ""
    }
    val currentTimeMillis: Long = (System.currentTimeMillis() - str2time(str)) / 1000
    return when {
        currentTimeMillis < 30 -> {
            "刚刚"
        }
        currentTimeMillis < 3600 -> {
            (currentTimeMillis / 60).toString() + "分钟前"
        }
        currentTimeMillis >= 86400 -> {
            str.substring(0, 11)
        }
        else -> {
            (currentTimeMillis / 3600).toString() + "小时前"
        }
    }
}
