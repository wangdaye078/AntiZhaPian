package com.openssl.md

import android.util.Log
import com.demo.antizha.util.AESUtil.cipherDecrypt_ZeroPadding
import com.demo.antizha.util.AESUtil.cipherEncrypt_ZeroPadding
import com.demo.antizha.util.Hex.toHexString
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.KeyAgreement


class JniHand {

    companion object {
        val derhead1 =
            byteArrayOf(48, 89, 48, 19, 6, 7, 42, -122, 72, -50, 61, 2, 1, 6, 8, 42,
                -122, 72, -50, 61, 3, 1, 7, 3, 66, 0)
        val derhead2 =
            byteArrayOf(48, -126, 1, 75, 48, -126, 1, 3, 6, 7, 42, -122, 72, -50, 61, 2,
                1, 48, -127, -9, 2, 1, 1, 48, 44, 6, 7, 42, -122, 72, -50, 61,
                1, 1, 2, 33, 0, -1, -1, -1, -1, 0, 0, 0, 1, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, 48, 91, 4, 32, -1, -1, -1, -1, 0, 0, 0,
                1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -4, 4, 32, 90, -58, 53, -40, -86,
                58, -109, -25, -77, -21, -67, 85, 118, -104, -122, -68, 101, 29, 6, -80, -52,
                83, -80, -10, 59, -50, 60, 62, 39, -46, 96, 75, 3, 21, 0, -60, -99,
                54, 8, -122, -25, 4, -109, 106, 102, 120, -31, 19, -99, 38, -73, -127, -97,
                126, -112, 4, 65, 4, 107, 23, -47, -14, -31, 44, 66, 71, -8, -68, -26,
                -27, 99, -92, 64, -14, 119, 3, 125, -127, 45, -21, 51, -96, -12, -95, 57,
                69, -40, -104, -62, -106, 79, -29, 66, -30, -2, 26, 127, -101, -114, -25, -21,
                74, 124, 15, -98, 22, 43, -50, 51, 87, 107, 49, 94, -50, -53, -74, 64,
                104, 55, -65, 81, -11, 2, 33, 0, -1, -1, -1, -1, 0, 0, 0, 0,
                -1, -1, -1, -1, -1, -1, -1, -1, -68, -26, -6, -83, -89, 23, -98, -124,
                -13, -71, -54, -62, -4, 99, 37, 81, 2, 1, 1, 3, 66, 0)
        var kpg: KeyPairGenerator? = null
        var ecsp: ECGenParameterSpec? = null
        var keyPair: KeyPair? = null
        var sharedSecret: ByteArray = byteArrayOf()

        init {
            /*伪造pem测试
            val kpg: KeyPairGenerator = KeyPairGenerator.getInstance("EC")
            val ecsp = ECGenParameterSpec("prime256v1")
            kpg.initialize(ecsp)

            val kp1: KeyPair = kpg.genKeyPair()
            val pk1 = kp1.public
            val pk1e = pk1.encoded

            //拼接成客户端用的DER，然后base64转成PEM
            val myder = ByteArray(derhead2.size + 65)
            System.arraycopy(derhead2, 0, myder, 0, derhead2.size)
            System.arraycopy(pk1.encoded, derhead1.size, myder, derhead2.size, 65)
            val mypem = "\n" + Base64.getEncoder().encodeToString(myder)
                .replace("(.{64})".toRegex(), "$1\n").trim()
            //到这就可以发给服务器了
            //服务器发过来的是没有回车的
            val otherpem = Base64.getEncoder().encodeToString(myder)
            val otherder = Base64.getDecoder().decode(otherpem)
            val pkcontent = ByteArray(derhead1.size + 65)
            System.arraycopy(derhead1, 0, pkcontent, 0, derhead1.size)
            System.arraycopy(otherder, derhead2.size, pkcontent, derhead1.size, 65)


            val factory: KeyFactory = KeyFactory.getInstance("EC")
            val pubKeySpec = X509EncodedKeySpec(pkcontent)
            val pk2 = factory.generatePublic(pubKeySpec)
            */
            //ECHD秘钥交换，但如何把publickey转成PEM，有问题
            /*
            val kpg: KeyPairGenerator = KeyPairGenerator.getInstance("EC")
            val ecsp = ECGenParameterSpec("prime256v1")
            kpg.initialize(ecsp)

            val kp1: KeyPair = kpg.genKeyPair()
            val pk1: ECPublicKey = kp1.public as ECPublicKey

            val kp2: KeyPair = kpg.genKeyPair()
            val pk2: ECPublicKey = kp2.public as ECPublicKey

            val ka1: KeyAgreement = KeyAgreement.getInstance("ECDH")
            ka1.init(kp1.private)
            ka1.doPhase(kp2.public, true)
            val sharedSecret1 = ka1.generateSecret()

            val ka2: KeyAgreement = KeyAgreement.getInstance("ECDH")
            ka2.init(kp2.private)
            ka2.doPhase(kp1.public, true)
            val sharedSecret2 = ka1.generateSecret()
            */

            //系统包含，不需要手动载入，之前手动载入总是报错，突然又好了，奇怪
            //System.loadLibrary("crypto")
            //载入下一个的时候自动加载它
            //System.loadLibrary("ssl")
            //下面4个函数在这里面，似乎判断了包名必须是com.hicorenational.antifraud
            //System.loadLibrary("md")
        }
    }

    //产生一个符合协议标准的peer key，仅仅产生，并没有任何使用
    fun getAppPubKey(): String {
        kpg = KeyPairGenerator.getInstance("EC")
        ecsp = ECGenParameterSpec("prime256v1")
        kpg!!.initialize(ecsp)

        keyPair = kpg!!.genKeyPair()

        val myder = ByteArray(derhead2.size + 65)
        System.arraycopy(derhead2, 0, myder, 0, derhead2.size)
        System.arraycopy(keyPair!!.public.encoded, derhead1.size, myder, derhead2.size, 65)
        val mypem = "\n" + Base64.getEncoder().encodeToString(myder)
            .replace("(.{64})".toRegex(), "$1\n").trim()
        return mypem
    }

    //设置加解密用的peer key，如果这个KEY去掉了中间的回车，必须执行
    //key.replace("(.{64})".toRegex(), "$1\n").trim()
    //在中间每64个字节添加一个回车，否则程序不认
    fun setApiPubKey(str: String) {
        val otherder = Base64.getDecoder().decode(str.replace("\n", ""))
        val pkcontent = ByteArray(derhead1.size + 65)
        System.arraycopy(derhead1, 0, pkcontent, 0, derhead1.size)
        System.arraycopy(otherder, derhead2.size, pkcontent, derhead1.size, 65)
        val factory: KeyFactory = KeyFactory.getInstance("EC")
        val pubKeySpec = X509EncodedKeySpec(pkcontent)
        val publicKey = factory.generatePublic(pubKeySpec)

        val ka1: KeyAgreement = KeyAgreement.getInstance("ECDH")
        ka1.init(keyPair!!.private)
        ka1.doPhase(publicKey, true)
        sharedSecret = ka1.generateSecret()
        Log.v("SharedSecret:", toHexString(sharedSecret))
    }

    //使用设置的peer key对字符串加密
    fun paramJsonEncode(str: String): String {
        return cipherEncrypt_ZeroPadding(str, sharedSecret, "Eer\'5P;41t?:?o-:")
    }

    //使用设置的peer key对字符串解密
    fun paramJsonDecode(str: String): String {
        return cipherDecrypt_ZeroPadding(str, sharedSecret, "Eer\'5P;41t?:?o-:")
    }
}