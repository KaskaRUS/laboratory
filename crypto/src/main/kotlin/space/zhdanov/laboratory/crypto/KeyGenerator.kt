package space.zhdanov.laboratory.crypto

import java.math.BigInteger

interface KeyGenerator {
    fun getPrivateKey(): ByteArray
    fun getPublicKey(): ByteArray
    fun generate()
}

abstract class RSAKeyGenerator : KeyGenerator {

    abstract fun getE(): BigInteger
    abstract fun getN(): BigInteger
    abstract fun getD(): BigInteger

    final override fun getPrivateKey(): ByteArray {
        val s: String = getN().toString() + "\n" + getD().toString()
        return s.toByteArray(charset = Charsets.US_ASCII)
    }

    final override fun getPublicKey(): ByteArray {
        val s: String = getN().toString() + "\n" + getE().toString()
        return s.toByteArray(charset = Charsets.US_ASCII)
    }
}