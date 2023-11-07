package com.demo.antizha.newwork

import okhttp3.Dns
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.net.InetAddress

class HookDns : Dns {
    //控制将一个服务器地址重定向到我的伪造服务器，用来进行测试
    val isHook2Local = false
    var DnsMap: HashMap<String, ByteArray> = HashMap()

    init {
        DnsMap["fzapp.gjfzpt.cn"] = byteArrayOf(192.toByte(), 168.toByte(), 2, 101)
        DnsMap["oss.gjfzpt.cn"] = byteArrayOf(192.toByte(), 168.toByte(), 2, 101)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun deserialization(byteData: ByteArray): InetAddress {
        val byteArrayInputStream = ByteArrayInputStream(byteData)
        val objectInputStream = ObjectInputStream(byteArrayInputStream)
        val tobject: Any = objectInputStream.readObject()
        objectInputStream.close()
        return tobject as InetAddress
    }

    override fun lookup(hostname: String): List<InetAddress> {
        if (isHook2Local && DnsMap.contains(hostname)) {
            val t_netAddress = InetAddress.getByAddress(hostname, DnsMap[hostname])
            var ips: ArrayList<InetAddress> = ArrayList<InetAddress>()
            ips.add(t_netAddress)
            return ips
        }
        return Dns.SYSTEM.lookup(hostname)
    }
}
