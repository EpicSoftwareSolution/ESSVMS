package com.naaz.essvms.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.naaz.essvms.R
import java.io.*
import java.util.*
import kotlin.collections.HashMap

class StoreUtil {
    private val fileName = "essvms"
    private val PREFERENCE = "PRIVATE-PREFERENCE"

    fun storeValueLocally(context: Context, key: String, value: String) {
        try {
            storeValueBySharedPref(context, key, value)
        } catch ( ex : Exception) {
            println("Exception while storing value in shared pref: $ex")
        }

        try {
            storeValueLocallyInFile(context, key, value)
        } catch ( ex : Exception) {
            println("Exception while storing value in file: $ex")
        }
    }

    private fun storeValueBySharedPref(context: Context, key: String, value: String) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val outputStream = ObjectOutputStream(byteArrayOutputStream)
        outputStream.writeObject(value)
        val strToSave = String(android.util.Base64.encode(byteArrayOutputStream.toByteArray(), android.util.Base64.DEFAULT))
        val sharedPref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, strToSave)
        editor.apply()
    }

    private fun storeValueLocallyInFile(context: Context, key: String, value: String) {
        var alreadyStoredVal = retrieveLocalValuesFromFile(context)
        var newValue = ""
        if(alreadyStoredVal != null && alreadyStoredVal.isNotEmpty()) {
            val valuesMap = alreadyStoredVal.split(",")
                .associateTo(mutableMapOf())  {
                    val (left, right) = it.split("=")
                    left to right
                }
            valuesMap[key] = value

            newValue = valuesMap.entries.joinToString(
                separator = ",",
            )
        } else {
            newValue = "$key=$value"
        }

        context.openFileOutput(fileName, MODE_PRIVATE).use {
                output ->
            with(output) { write((newValue).toByteArray()) }
        }
    }

    fun retrieveLocalValue(context: Context, key: String,): String {
        var returnValue = ""
        try {
            retrieveValueFromSharedPref(context, key).also { returnValue = it }
        } catch ( ex : Exception) {
            println("Exception while retrieving value from shared pref: $ex")
        }

        return returnValue.ifEmpty {
            try {
                returnValue = retrieveValueFromFile(context, key)
            } catch (ex: Exception) {
                println("Exception while retrieving value from shared pref: $ex")
                ""
            }
            returnValue
        }
    }

    private fun retrieveValueFromSharedPref(context: Context, key: String): String {
        val sharedPref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        val strSecretKey = sharedPref.getString(key, null)
            ?: return context.resources.getString(R.string.code_new_run)
        val bytes = android.util.Base64.decode(strSecretKey, android.util.Base64.DEFAULT)
        val inputStream = ObjectInputStream(ByteArrayInputStream(bytes))
        return inputStream.readObject().toString()
    }

    private fun retrieveValueFromFile(context: Context, key: String): String {
        var alreadyStoredVal = retrieveLocalValuesFromFile(context)
        return if(alreadyStoredVal != null && alreadyStoredVal.isNotEmpty()) {
            val valuesMap = alreadyStoredVal.split(",")
                .associateTo(mutableMapOf())  {
                    val (left, right) = it.split("=")
                    left to right
                }
            valuesMap[key]?:""
        } else {
            ""
        }
    }

    private fun retrieveLocalValuesFromFile(context: Context): String {
        return try {
            val fis: FileInputStream = context.openFileInput(fileName)
            val scanner = Scanner(fis)
            scanner.useDelimiter("\\Z")
            val content: String = scanner.next()
            scanner.close()
            content
        } catch (ex : Exception) {
            println("Exception while storing value : $ex")
            ""
        }
    }
}