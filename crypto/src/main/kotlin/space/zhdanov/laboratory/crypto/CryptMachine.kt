package space.zhdanov.laboratory.crypto

interface Encoder {
    fun encode(data: ByteArray, key: ByteArray): ByteArray
}

interface Decoder {
    fun decode(data: ByteArray, key: ByteArray): ByteArray
}
