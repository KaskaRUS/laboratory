package space.zhdanov.laboratory.crypto

import java.util.*

fun main() {
    println("Choose action:")
    println("1. Generate key")
    println("2. Encode text:")
    println("3. Decode text:")

    val choose = readLine()?.toInt() ?: -1
    when (choose) {
        1 -> generateKeys()
        2 -> encodeText()
        3 -> decodeText()
        else -> println("Incorrect choose")
    }
}

fun decodeText() {
    println("Enter private key:")
    val nValue = readLine()
    val dValue = readLine()
    val privateKey = "$nValue\n$dValue".toByteArray(Charsets.US_ASCII)
    println("Enter encoded message:")
    readLine()?.let { message ->
        val crypto = SimpleRSA(SimpleRSAKeyGenerator(SimplePrimeGetter(), 8))
        val encodedText = Base64.getDecoder().decode(message)
        val decodedText = crypto.decode(encodedText, privateKey)
        println("Decoded message: ")
        println(decodedText.toString(Charsets.UTF_8))
    }
}

fun encodeText() {
    println("Enter public key:")
    val nValue = readLine()
    val eValue = readLine()
    val publicKey = "$nValue\n$eValue".toByteArray(Charsets.US_ASCII)
    println("Enter your message:")
    readLine()?.let { message ->
        val crypto = SimpleRSA(SimpleRSAKeyGenerator(SimplePrimeGetter(), 8))
        val encodedText = crypto.encode(message.toByteArray(charset = Charsets.UTF_8), publicKey)
        println("Encoded message: ")
        println(Base64.getEncoder().encodeToString(encodedText))
    }
}

fun generateKeys() {
    println("Write size of key(bits):")
    val keySize = readLine()?.toInt() ?: 8
    println("Generate key($keySize bits):")
    val keyGenerator = SimpleRSAKeyGenerator(SimplePrimeGetter(), keySize)
    keyGenerator.generate()

    println("You private key")
    println(keyGenerator.getPrivateKey().toString(Charsets.US_ASCII))

    println("You public key")
    println(keyGenerator.getPublicKey().toString(Charsets.US_ASCII))
}