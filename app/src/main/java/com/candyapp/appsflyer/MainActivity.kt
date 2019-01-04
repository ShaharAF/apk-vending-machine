package com.candyapp.appsflyer

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AppsFlyerLib
import com.candyapp.appsflyer.CandyApplication.Companion.devMode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    val onButtonClick: View.OnClickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {
            val evtParams = mutableMapOf<String, Any?>()
            with(Intent(this@MainActivity, BuyActivity::class.java)) {
                if(v!!.id == R.id.btnBuyMM) {
                    putExtra(BuyActivity.ITEM_TYPE, BuyActivity.ITEM_TYPE_MM)
                    evtParams.put(AFInAppEventParameterName.CONTENT_ID, "M&M")
                } else {
                    putExtra(BuyActivity.ITEM_TYPE, BuyActivity.ITEM_TYPE_SKITTLES)
                    evtParams.put(AFInAppEventParameterName.CONTENT_ID, "Skittles")
                }
                startActivity(this@with)
            }
            AppsFlyerLib.getInstance().trackEvent(this@MainActivity, "View Product", evtParams)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnBuyMM.setOnClickListener(onButtonClick)
        btnBuyMM.setOnLongClickListener {
            DialogFactory.showGoogleReferrer(this@MainActivity)
            true
        }

        btnBuySkittles.setOnClickListener(onButtonClick)
        btnBuySkittles.setOnLongClickListener {
            DialogFactory.showAppsFlyerId(this@MainActivity)
            true
        }
        btnRegister.setOnClickListener { startActivity(Intent(this@MainActivity, RegisterActivity::class.java)) }
        btnRegister.setOnLongClickListener {
            DialogFactory.showCustomerUserId(this@MainActivity)
            true
        }
        if(devMode) {
            btnConversionData.visibility = View.VISIBLE
            btnConversionData.setOnClickListener { startActivity(Intent(this@MainActivity, ConversionActivity::class.java)) }
        } else {
            btnConversionData.visibility = View.GONE
        }
    }
}
