package site.aleksa.hse.kpo.restorini.common.util

import java.security.spec.KeySpec
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Hash Utils object
 */
object HashUtils {
    private const val ALGORITHM = "PBKDF2WithHmacSHA512"
    private const val ITERATIONS = 1
    private const val KEY_LENGTH = 256
    private const val SECRET = "aleksa@b0OlydXWyjjGELs1cyKZSyCx"

    private fun ByteArray.toHexLower(): String =
        joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

    /**
     * Generate Hast for password
     */
    fun generateHash(password: String?): String {
        val combinedSalt = SECRET.toByteArray()
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)
        val spec: KeySpec = PBEKeySpec(password!!.toCharArray(), combinedSalt, ITERATIONS, KEY_LENGTH)
        val key: SecretKey = factory.generateSecret(spec)
        val hash: ByteArray = key.encoded
        return hash.toHexLower()
    }
}