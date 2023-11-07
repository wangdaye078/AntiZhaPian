package com.demo.antizha.util

//CRC-64/XZ
class CRC64 {
    private var crc: Long = -1

    companion object {
        private const val poly = -0x3693a86a2878f0beL
        private val crcTable = LongArray(256)
        fun digest(buf: ByteArray): CRC64 {
            val crc = CRC64()
            crc.update(buf)
            return crc
        }

        init {
            for (b in crcTable.indices) {
                var r = b.toLong()
                for (i in 0..7) {
                    r = if (r and 1 == 1L)
                        (r ushr 1) xor poly
                    else
                        r ushr 1
                }
                crcTable[b] = r
            }
        }
    }

    fun update(b: Byte) {
        crc = crcTable[b.toInt() xor crc.toInt() and 0xFF] xor (crc ushr 8)
    }

    @JvmOverloads
    fun update(buf: ByteArray, offset: Int = 0, len: Int = buf.size) {
        var off = offset
        val end = off + len
        while (off < end) crc =
            crcTable[buf[off++].toInt() xor crc.toInt() and 0xFF] xor (crc ushr 8)
    }

    val value: Long
        get() = crc.inv()

    val bytes: ByteArray
        get() {
            val value = crc.inv()
            val b = ByteArray(8)
            for (i in 0..7) {
                b[7 - i] = (value ushr i * 8).toByte()
            }
            return b
        }
}
