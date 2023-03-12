package com.firstapplication.dormapp

import android.util.Log
import java.lang.StringBuilder
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class Encryptor {

    fun encrypt(toEncrypt: String): String? {
        val messageDigest: MessageDigest
        val digest: ByteArray

        try {
            messageDigest = MessageDigest.getInstance(algorithm)
            messageDigest.reset()
            messageDigest.update(toEncrypt.toByteArray())

            digest = messageDigest.digest()
        } catch (e: NoSuchAlgorithmException) {
            Log.e(this::class.java.simpleName, e.message ?: e.stackTraceToString())
            return null
        }

        val bigInt = BigInteger(1, digest)
        val md5Hex = StringBuilder(bigInt.toString(16))
        while (md5Hex.length < 32) {
            md5Hex.insert(0, "0")
        }

        return md5Hex.toString()
    }

    companion object {
        @JvmStatic
        private val algorithm = "MD5"
    }
}