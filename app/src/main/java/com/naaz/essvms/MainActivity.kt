package com.naaz.essvms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.naaz.essvms.activities.DisplayCodesActivity
import com.naaz.essvms.activities.GenerateCodesActivity
import com.naaz.essvms.utils.DecryptionUtil
import com.naaz.essvms.utils.StoreUtil
import java.util.Arrays

class MainActivity : AppCompatActivity() {
    private lateinit var errorMessageView : TextView

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkRequestPermission()

//        StoreUtil().storeValueLocally(this, resources.getString(R.string.key_app_run), resources.getString(R.string.code_new_run))
//        StoreUtil().storeValueLocally(this, resources.getString(R.string.key_user_status), resources.getString(R.string.code_user_unregistered))
//        val decryptionUtil = DecryptionUtil()
//        val pass = decryptionUtil.cipherEncrypt("NAAZ_SOFT_ENCRYT", "")

//        if(isUserRegistered()) {
//            redirectToDisplayActivity()
//        } else if (!isFirstTimeRun()) {
            redirectToNextActivity()
//        } else {
//            setContentView(R.layout.activity_main)
//            findViewById<RelativeLayout>(R.id.mainActivityLayout).apply {
//                visibility = View.VISIBLE
//            }
//            createMainWindow()
//        }

        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.READ_PHONE_STATE
            )
            val permissionsRequest = mutableListOf<String>()
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsRequest.add(permission)
                }
            }
            if (permissionsRequest.size > 0) {
                requestPermissions(permissionsRequest.toTypedArray(), 100)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createMainWindow() {
        errorMessageView = findViewById(R.id.errorMessageMain)

        val adminLoginBtn = findViewById<Button>(R.id.adminLoginBtn)
        adminLoginBtn.setOnClickListener {
            validateAdminCredentials()
        }

        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordText)
        passwordEditText.setOnClickListener {
            cleanPasswordText()
        }
    }

    private fun isUserRegistered(): Boolean {
        return StoreUtil().retrieveLocalValue(
            this,
            resources.getString(R.string.key_user_status)
        ) == resources.getString(R.string.code_user_registered)
    }

    private fun isFirstTimeRun(): Boolean {
        val storeUtil = StoreUtil()
        var appRunCode = storeUtil.retrieveLocalValue(this, resources.getString(R.string.key_app_run))
        return appRunCode == resources.getString(R.string.code_new_run)
    }

    private fun cleanPasswordText() {
        errorMessageView.apply {
            text = ""
            visibility = TextView.INVISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun validateAdminCredentials() {

        if (isValidAdmin()){
            StoreUtil().storeValueLocally(
                this,
                resources.getString(R.string.key_app_run),
                resources.getString(R.string.code_old_run)
            )
            redirectToNextActivity()
        } else {
            errorMessageView.apply {
                text = resources.getString(R.string.invalid_password_error_msg)
                visibility = TextView.VISIBLE
            }
        }
    }

    private fun redirectToNextActivity() {
        val myIntent = Intent(
            this,
            GenerateCodesActivity::class.java
        )
        startActivity(myIntent)
    }

    private fun redirectToDisplayActivity() {
        val myIntent = Intent(
            this,
            DisplayCodesActivity::class.java
        )
        startActivity(myIntent)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun isValidAdmin(): Boolean {
        var passEditText = findViewById<TextInputEditText>(R.id.passwordText)
        if (passEditText.text.toString().trim() == "") {
            errorMessageView.apply {
                text = resources.getString(R.string.blank_password_error_msg)
                visibility = TextView.VISIBLE
            }
        }

        var encryptKey = resources.getString(R.string.enc_key)
        var encryptPass = DecryptionUtil().cipherEncrypt(encryptKey, passEditText.text.toString())?.trim()
        var adminPasses = resources.getStringArray(R.array.admin_passes)
        return Arrays.stream(adminPasses).anyMatch{ pass -> pass == encryptPass}
    }
}