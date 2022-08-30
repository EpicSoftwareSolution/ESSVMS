package com.naaz.essvms.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.naaz.essvms.R
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class StoreUtil {

    private val PREFERENCE = "PRIVATE-PREFERENCE"

    fun storeValueLocally(context: Context, key: String, value: String) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val outputStream = ObjectOutputStream(byteArrayOutputStream)
        outputStream.writeObject(value)
        val strToSave = String(android.util.Base64.encode(byteArrayOutputStream.toByteArray(), android.util.Base64.DEFAULT))
        val sharedPref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, strToSave)
        editor.apply()
    }

    fun retrieveLocalValue(context: Context, key: String,): String {
        val sharedPref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        val strSecretKey = sharedPref.getString(key, null) ?: return context.resources.getString(R.string.code_new_run)
        val bytes = android.util.Base64.decode(strSecretKey, android.util.Base64.DEFAULT)
        val inputStream = ObjectInputStream(ByteArrayInputStream(bytes))
        val secretKey = inputStream.readObject().toString()
        return secretKey
    }
}