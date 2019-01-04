package com.candyapp.appsflyer

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import com.appsflyer.AFHelper
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AppsFlyerProperties
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity:Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setTitle(R.string.title_register)
        btnRegister.setOnClickListener {
            AppsFlyerLib.getInstance().setCustomerUserId(inputCustomerUserId?.text?.toString())
            AppsFlyerLib.getInstance().trackEvent(this@RegisterActivity, AFInAppEventType.COMPLETE_REGISTRATION, null)
            DialogFactory.showEventSent(this@RegisterActivity, AFInAppEventType.COMPLETE_REGISTRATION, null)
        }
        btnBack.setOnClickListener { finish() }
        inputCustomerUserId.setText(AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.APP_USER_ID))
    }
}