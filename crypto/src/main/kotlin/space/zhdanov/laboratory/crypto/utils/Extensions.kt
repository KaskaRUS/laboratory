package space.zhdanov.laboratory.crypto.utils

import java.math.BigInteger

operator fun BigInteger.rangeTo(that: BigInteger): BigIntegerRange {
    return BigIntegerRange(this, that)
}

class BigIntegerRange(override val start: BigInteger,
                      override val endInclusive: BigInteger) : ClosedRange<BigInteger>, Iterable<BigInteger> {

    override fun iterator(): Iterator<BigInteger> {
        return BigIntegerIterator(start, endInclusive)
    }
}

class BigIntegerIterator(start: BigInteger, private val endInclusive: BigInteger) : Iterator<BigInteger> {

    private var initValue = start

    override fun hasNext(): Boolean {
        return initValue <= endInclusive
    }

    override fun next(): BigInteger {
        return initValue++
    }
}