package com.naaz.essvms.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsApi
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.naaz.essvms.R
import com.naaz.essvms.utils.ColorFillerUtil
import com.naaz.essvms.utils.StoreUtil

class GenerateCodesActivity : AppCompatActivity() {
    lateinit var phoneNumber: TextInputEditText

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_codes)

        if (supportActionBar != null) {
            supportActionBar?.hide()
        }

        phoneNumber = findViewById(R.id.mobileNumberText)

        findViewById<TextInputEditText>(R.id.fullNameText).filters += InputFilter.LengthFilter(100)
        findViewById<TextInputEditText>(R.id.userIdText).filters += InputFilter.LengthFilter(16)

        phoneSelection()

       /* val fullNameEditText = findViewById<Button>(R.id.fullNameText)
        fullNameEditText.setOnClickListener {
            fullNameEditTextClickEvent()
        }

        val userIdEditText = findViewById<Button>(R.id.userIdText)
        userIdEditText.setOnClickListener {
            userIdEditTextClickEvent()
        }*/

        val generateQrCodeBtn = findViewById<Button>(R.id.generateQrCodeBtn)
        generateQrCodeBtn.setOnClickListener {
            StoreUtil().storeValueLocally(
                this,
                resources.getString(R.string.key_user_code_type),
                resources.getString(R.string.code_user_code_qr)
            )
            generateCodeInfo()
        }

        val generateBarCodeBtn = findViewById<Button>(R.id.generateBarCodeBtn)
        generateBarCodeBtn.setOnClickListener {
            StoreUtil().storeValueLocally(
                this,
                resources.getString(R.string.key_user_code_type),
                resources.getString(R.string.code_user_code_bar)
            )
            generateCodeInfo()
        }
    }

    private fun userIdEditTextClickEvent() {
        findViewById<TextInputLayout>(R.id.userIdText).apply {
            setBoxStrokeColorStateList(ColorFillerUtil().getNormalColor(this.context))
        }
    }

    private fun fullNameEditTextClickEvent() {
        findViewById<TextInputLayout>(R.id.fullNameLayout).apply {
            setBoxStrokeColorStateList(ColorFillerUtil().getNormalColor(this.context))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateCodeInfo() {
        if(isValidGenerateCodesInfo()) {
            StoreUtil().storeValueLocally(
                this,
                resources.getString(R.string.key_user_mobile_number),
                findViewById<TextInputEditText>(R.id.mobileNumberText).text.toString()
            )

            StoreUtil().storeValueLocally(
                this,
                resources.getString(R.string.key_user_full_name),
                findViewById<TextInputEditText>(R.id.fullNameText).text.toString()
            )

            StoreUtil().storeValueLocally(
                this,
                resources.getString(R.string.key_user_id),
                findViewById<TextInputEditText>(R.id.userIdText).text.toString()
            )

            StoreUtil().storeValueLocally(
                this,
                resources.getString(R.string.key_user_status),
                resources.getString(R.string.code_user_registered)
            )

            val myIntent = Intent(
                this,
                DisplayCodesActivity::class.java
            )
            startActivity(myIntent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isValidGenerateCodesInfo(): Boolean {
        val errorMessageView = findViewById<TextView>(R.id.errorMessage)
        var hasError = false

        if (findViewById<TextInputEditText>(R.id.mobileNumberText).text.toString().trim() == "") {
            findViewById<TextInputLayout>(R.id.mobileNoLayout).apply {
                setBoxStrokeColorStateList(ColorFillerUtil().getErrorColor(this.context))
            }
            hasError = true
        }

        if (findViewById<TextInputEditText>(R.id.fullNameText).text.toString().trim() == "") {
            findViewById<TextInputLayout>(R.id.fullNameLayout).apply {
                setBoxStrokeColorStateList(ColorFillerUtil().getErrorColor(this.context))
            }
            hasError = true
        }

        if(findViewById<TextInputEditText>(R.id.userIdText).text.toString().trim() == "") {
            findViewById<TextInputLayout>(R.id.userIdLayout).apply {
                setBoxStrokeColorStateList(ColorFillerUtil().getErrorColor(this.context))
            }
            hasError = true
        }

        if (hasError) {
            errorMessageView.apply {
                text = resources.getString(R.string.generate_code_error_msg)
                visibility = TextView.VISIBLE
            }
        }

        return !hasError
    }

    private fun isUserRegistered(): Boolean {
        val storeUtil = StoreUtil()
        var userStatusCode = storeUtil.retrieveLocalValue(this, resources.getString(R.string.key_user_status))
        return userStatusCode == resources.getString(R.string.code_user_registered)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun phoneSelection() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val intent = Credentials.getClient(this).getHintPickerIntent(hintRequest)
        val intentSenderRequest = IntentSenderRequest.Builder(intent.intentSender)

        var hintLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result: ActivityResult? ->
            if (result != null && result.data != null) {
                when (result.resultCode) {
                    CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE -> Toast.makeText(this, "No phone numbers found", Toast.LENGTH_LONG).show().also {
                        phoneNumber.setText("1234567890")
                    }
                    RESULT_OK -> {
                        val credential: Credential? = result.data!!.getParcelableExtra(Credential.EXTRA_KEY)
                        credential?.apply {
                            var mobileNumber = credential.id
                            val maxMobileLength = resources.getString(R.string.max_mobile_length).toInt()
                            if(mobileNumber.length > maxMobileLength) {
                                mobileNumber = credential.id.takeLast(maxMobileLength)
                            }
                            phoneNumber.setText(mobileNumber)
                        }
                    }
                }
            }
        }

        hintLauncher.launch(intentSenderRequest.build())
    }

    override fun onBackPressed() { return }
}