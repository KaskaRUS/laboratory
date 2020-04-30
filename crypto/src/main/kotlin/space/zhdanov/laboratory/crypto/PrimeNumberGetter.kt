package space.zhdanov.laboratory.crypto

import space.zhdanov.laboratory.crypto.utils.rangeTo
import java.math.BigInteger

interface PrimeNumberGetter {
    fun getRandomPrimeNumber(max: BigInteger, min: BigInteger = BigInteger.TWO): BigInteger
    fun getListOfPrimes(max: BigInteger, min: BigInteger = BigInteger.TWO): List<BigInteger>
}

class SimplePrimeGetter : PrimeNumberGetter {

    private val primes = mutableListOf<BigInteger>(BigInteger.TWO)
    private var lastCheck = BigInteger.TWO

    override fun getRandomPrimeNumber(max: BigInteger, min: BigInteger): BigInteger {
        findAllPrimesUntil(max)

        return primes.filter { it >= min }.random()
    }

    override fun getListOfPrimes(max: BigInteger, min: BigInteger): List<BigInteger> {
        findAllPrimesUntil(max)

        return primes.filter { it in min..max }
    }

    private fun findAllPrimesUntil(max: BigInteger) {
        for (x in (lastCheck + BigInteger.ONE)..max) {
            if (primes.none { x % it == BigInteger.ZERO })
                primes.add(x)
        }

        lastCheck = primes.last()
    }
}
