package com.naaz.essvms.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.naaz.essvms.R
import com.naaz.essvms.utils.BitmapUtil
import com.naaz.essvms.utils.StoreUtil

class DisplayCodesActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_codes)

        if (supportActionBar != null) {
            supportActionBar?.hide()
        }

        val mobileNumber = StoreUtil().retrieveLocalValue(
            this,
            resources.getString(R.string.key_user_mobile_number)
        )

        findViewById<TextView>(R.id.displayMobileText).text = mobileNumber

        findViewById<TextView>(R.id.displayFullNameText).text =
            StoreUtil().retrieveLocalValue(
                this,
                resources.getString(R.string.key_user_full_name)
            )

        findViewById<TextView>(R.id.displayUserIdText).text =
            StoreUtil().retrieveLocalValue(
                this,
                resources.getString(R.string.key_user_id)
            )

        if(StoreUtil().retrieveLocalValue(
            this,
            resources.getString(R.string.key_user_code_type)) == resources.getString(R.string.code_user_code_bar)) {
            findViewById<ImageView>(R.id.imageBarcode).apply {
                visibility = View.VISIBLE
            }
            displayBarcode(mobileNumber)
        } else {
            findViewById<ImageView>(R.id.imageQrcode).apply {
                visibility = View.VISIBLE
            }
            displayQrcode(mobileNumber)
        }
    }

    private fun displayQrcode(value: String) {
        var qrCodeBitmap = BitmapUtil().generateQRCodeBitmap(value, resources.getString(R.string.width_qrcode).dropLast(2).toInt())
        if (null != qrCodeBitmap) {
            findViewById<ImageView>(R.id.imageQrcode).setImageBitmap(qrCodeBitmap)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun displayBarcode(value: String) {
        val widthPixels = resources.getString(R.string.width_barcode).dropLast(2).toInt()
        val heightPixels = resources.getString(R.string.height_barcode).dropLast(2).toInt()

        findViewById<ImageView>(R.id.imageBarcode).setImageBitmap(
            BitmapUtil().createBarcodeBitmap(
                barcodeValue = value,
                barcodeColor = getColor(R.color.black),
                backgroundColor = getColor(android.R.color.white),
                widthPixels = widthPixels,
                heightPixels = heightPixels
            )
        )
    }

    override fun onBackPressed() { return }
}