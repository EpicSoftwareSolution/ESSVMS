package com.naaz.essvms.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsApi
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.naaz.essvms.R

class PhoneSelectionUtil {
    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.O)
    fun findAndSetPhoneNumber(context: Context, activity: AppCompatActivity, phoneNumberInputText: TextInputEditText) {
        try {
            var mobileNumber = ""
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.READ_SMS
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED) {
                var telephonyManager = context.getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
                mobileNumber = telephonyManager.line1Number.toString()
            }

            if(mobileNumber == "") {
                phoneSelection(context, activity, phoneNumberInputText)
            } else {
                setMobileNumber(context, activity, phoneNumberInputText, mobileNumber)
            }

        } catch (ex: Exception) {
            println(ex)
            setMobileNumber(context, activity, phoneNumberInputText, "")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMobileNumber(context: Context,
                                activity: AppCompatActivity,
                                mobileNumberInputText: TextInputEditText,
                                mobileNumberInput: String) {
        if(mobileNumberInput.trim() == "") {
            mobileNumberInputText.apply {
                focusable = TextInputEditText.FOCUSABLE
            }
            activity.findViewById<TextInputLayout>(R.id.mobileNoLayout).apply {
                isEnabled = true
            }
        } else {
            var mobileNumber = mobileNumberInput
            val maxMobileLength = context.resources.getString(R.string.max_mobile_length).toInt()
            if(mobileNumber.length > maxMobileLength) {
                mobileNumber = mobileNumber.takeLast(maxMobileLength)
            }
            mobileNumberInputText.setText(mobileNumber)

            mobileNumberInputText.apply {
                focusable = TextInputEditText.NOT_FOCUSABLE
            }
            activity.findViewById<TextInputLayout>(R.id.mobileNoLayout).apply {
                isEnabled = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun phoneSelection(context: Context,
                               activity: AppCompatActivity,
                               phoneNumberInputText: TextInputEditText) {

        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val intent = Credentials.getClient(context).getHintPickerIntent(hintRequest)
        val intentSenderRequest = IntentSenderRequest.Builder(intent.intentSender)

        var hintLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result: ActivityResult? ->
            if (result != null && result.data != null) {
                when (result.resultCode) {
                    CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE -> setMobileNumber(context, activity, phoneNumberInputText, "")
                    AppCompatActivity.RESULT_OK -> {
                        val credential: Credential? = result.data!!.getParcelableExtra(Credential.EXTRA_KEY)
                        credential?.apply {
                            var mobileNumber = credential.id
                            val maxMobileLength = context.resources.getString(R.string.max_mobile_length).toInt()
                            if(mobileNumber.length > maxMobileLength) {
                                mobileNumber = credential.id.takeLast(maxMobileLength)
                            }
                            setMobileNumber(context, activity, phoneNumberInputText, mobileNumber)
                        }
                    }
                }
            }
        }

        hintLauncher.launch(intentSenderRequest.build())
    }
}