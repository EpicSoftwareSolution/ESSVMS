package com.naaz.essvms.utils

import android.util.Base64
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class DecryptionUtil {

    fun cipherEncrypt(encryptionKey: String, strToEncrypt: String): String? {
        try {
            val secretKeySpec = SecretKeySpec(encryptionKey.toByteArray(), "AES")
            val iv = encryptionKey.toByteArray()
            val ivParameterSpec = IvParameterSpec(iv)

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)

            val encryptedValue = cipher.doFinal(strToEncrypt.toByteArray())
            return Base64.encodeToString(encryptedValue, Base64.DEFAULT)
        } catch (e: Exception) {
            e.message?.let{ Log.e("encryptor", it) }
        }
        return null
    }

    fun cipherDecrypt(encryptionKey: String, strToDecypt: String): String? {
        try {
            val secretKeySpec = SecretKeySpec(encryptionKey.toByteArray(), "AES")
            val iv = encryptionKey.toByteArray()
            val ivParameterSpec = IvParameterSpec(iv)

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)

            val decodedValue = Base64.decode(strToDecypt, Base64.DEFAULT)
            val decryptedValue = cipher.doFinal(decodedValue)
            return String(decryptedValue)
        } catch (e: Exception) {
            e.message?.let{ Log.e("decryptor", it) }
        }
        return null
    }

   /* fun encrypt(context: Context, strToEncrypt: String): ByteArray {
        val plainText = strToEncrypt.toByteArray(Charsets.UTF_8)
        val keygen = KeyGenerator.getInstance("AES")
        keygen.init(256)
        val key = keygen.generateKey()
        saveSecretKey(context, key)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val cipherText = cipher.doFinal(plainText)
        saveInitializationVector(context, cipher.iv)

        val sb = StringBuilder()
        for (b in cipherText) {
            sb.append(b.toChar())
        }
        Toast.makeText(context, "dbg encrypted = [" + sb.toString() + "]", Toast.LENGTH_LONG).show()

        return cipherText
    }

    fun decrypt(context:Context, dataToDecrypt: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        val ivSpec = IvParameterSpec(getSavedInitializationVector(context))
        cipher.init(Cipher.DECRYPT_MODE, getSavedSecretKey(context), ivSpec)
        val cipherText = cipher.doFinal(dataToDecrypt)

        val sb = StringBuilder()
        for (b in cipherText) {
            sb.append(b.toChar())
        }
        Toast.makeText(context, "dbg decrypted = [" + sb.toString() + "]", Toast.LENGTH_LONG).show()

        return cipherText
    }*/
}