package space.zhdanov.laboratory.crypto

import space.zhdanov.laboratory.crypto.exceptions.FailDecodingMessageException
import space.zhdanov.laboratory.crypto.utils.toInt
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.ByteBuffer
import kotlin.properties.Delegates

class SimpleRSA(
        private val keyGenerator: RSAKeyGenerator
) : Decoder, Encoder {
    override fun encode(data: ByteArray, key: ByteArray): ByteArray {
        val (nValue, eValue) = keyGenerator.parsePublicKey(key)
        val (sizeIn, sizeOut) = getSizePacket(nValue)

        val lengthData = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(data.size).array()
        val roundData = roundByteArrayInBack(lengthData + data, sizeIn)
        val output = ByteArrayOutputStream()
        for (x in roundData.indices step sizeIn) {
            val buf = ByteArray(1) { 0 } + roundData.copyOfRange(x, x + sizeIn)
            val number = BigInteger(buf)
            output.writeBytes(roundByteArrayInFront(number.modPow(eValue, nValue).toByteArray(), sizeOut))
        }

        val result = output.toByteArray()
        output.close()
        return result
    }

    override fun decode(data: ByteArray, key: ByteArray): ByteArray {
        val (nValue, dValue) = keyGenerator.parsePublicKey(key)
        val (sizeIn, sizeOut) = getSizePacket(nValue)

        val roundData = roundByteArrayInBack(data, sizeOut)
        val output = ByteArrayOutputStream()
        for (x in roundData.indices step sizeOut) {
            val buf = ByteArray(1) { 0 } + roundData.copyOfRange(x, x + sizeOut)
            val number = BigInteger(buf)
            output.writeBytes(roundByteArrayInFront(number.modPow(dValue, nValue).toByteArray(), sizeIn))
        }

        val result = output.toByteArray()
        output.close()
        val length = result.copyOfRange(0, Int.SIZE_BYTES).toInt()
        if (length > 0 && result.size >= Int.SIZE_BYTES + length)
            return result.copyOfRange(Int.SIZE_BYTES, Int.SIZE_BYTES + length)
        else
            throw FailDecodingMessageException("Data not decoded")
    }

    private fun roundByteArrayInBack(data: ByteArray, sizeIn: Int) =
            if (data.size % sizeIn == 0) data else data + ByteArray(sizeIn - (data.size % sizeIn))

    private fun roundByteArrayInFront(data: ByteArray, sizeIn: Int) =
            when {
                data.size > sizeIn -> data.copyOfRange(data.size - sizeIn, data.size)
                data.size < sizeIn -> ByteArray(sizeIn - (data.size % sizeIn)) + data
                else -> data
            }

    /**
     * @param nValue - max number
     * return size of packet in bytes
     */
    private fun getSizePacket(nValue: BigInteger): Pair<Int, Int> {
        val size = nValue.bitLength() / 8
        return Pair(size, size + if (nValue.bitLength() % 8 == 0) 0 else 1)
    }

}

class SimpleRSAKeyGenerator(
        private val primeNumberGetter: PrimeNumberGetter,
        private val bits: Int
) : RSAKeyGenerator() {

    private var eValue: BigInteger by Delegates.notNull()
    private var nValue: BigInteger by Delegates.notNull()
    private var dValue: BigInteger by Delegates.notNull()

    override fun getE(): BigInteger = eValue

    override fun getN(): BigInteger = nValue

    override fun getD(): BigInteger = dValue

    override fun generate() {
        val min = BigInteger.valueOf(2).pow(bits)
        val max = BigInteger.valueOf(2).pow(bits + 1)

        val p = primeNumberGetter.getRandomPrimeNumber(max, min)
        val q = primeNumberGetter.getRandomPrimeNumber(max, min)
        nValue = p * q

        val f = (p - BigInteger.ONE) * (q - BigInteger.ONE)
        val halfF = f.div(BigInteger.TWO)
        eValue = primeNumberGetter.getListOfPrimes(f, BigInteger.valueOf(3))
                .filter { f.mod(it) != BigInteger.ZERO }
                .random()
        dValue = primeNumberGetter.getListOfPrimes(nValue)
                .firstOrNull { (it * eValue).mod(f) == BigInteger.ONE }
                ?: run {
                    var variant = nValue
                    while ((variant * eValue).mod(f) != BigInteger.ONE)
                        variant++
                    variant
                }
    }
}