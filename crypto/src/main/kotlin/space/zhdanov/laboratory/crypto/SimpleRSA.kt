package space.zhdanov.laboratory.crypto

import java.math.BigInteger
import kotlin.properties.Delegates

class SimpleRSA(
        private val keyGenerator: RSAKeyGenerator
) {

}

class SimpleRSAKeyGenerator(
        private val primeNumberGetter: PrimeNumberGetter,
        private val bits: Int
) : RSAKeyGenerator() {

    private var eValue: BigInteger by Delegates.notNull()
    private var nValue: BigInteger by Delegates.notNull()
    private var dValue: BigInteger by Delegates.notNull()

    init {
        generate()
    }

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
        eValue = primeNumberGetter.getRandomPrimeNumber(f, halfF)
        while (f.mod(eValue) == BigInteger.ZERO)
            eValue = primeNumberGetter.getRandomPrimeNumber(f, halfF)

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

fun main() {
    val generator = SimpleRSAKeyGenerator(SimplePrimeGetter(), 6)
    println(generator.getPublicKey().contentToString())
    println(generator.getPrivateKey().contentToString())
}