package com.naaz.essvms.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.naaz.essvms.R
import com.naaz.essvms.utils.ColorFillerUtil
import com.naaz.essvms.utils.PhoneSelectionUtil
import com.naaz.essvms.utils.StoreUtil


class GenerateCodesActivity : AppCompatActivity() {

    private val phoneSelectionUtil = PhoneSelectionUtil()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_codes)

        if (supportActionBar != null) {
            supportActionBar?.hide()
        }

        findViewById<TextInputEditText>(R.id.mobileNumberText).setText("")
        findViewById<TextInputEditText>(R.id.fullNameText).filters += InputFilter.LengthFilter(100)
        findViewById<TextInputEditText>(R.id.fullNameText).setText("")
        findViewById<TextInputEditText>(R.id.userIdText).filters += InputFilter.LengthFilter(16)
        findViewById<TextInputEditText>(R.id.userIdText).setText("")

//        phoneSelectionUtil.findAndSetPhoneNumber(this, this, findViewById(R.id.mobileNumberText))

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

            findViewById<TextInputEditText>(R.id.mobileNumberText).setText("")
            findViewById<TextInputEditText>(R.id.fullNameText).setText("")
            findViewById<TextInputEditText>(R.id.userIdText).setText("")
            findViewById<TextInputEditText>(R.id.mobileNumberText).requestFocus()


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

    override fun onBackPressed() { return }
}