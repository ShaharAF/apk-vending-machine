package com.candyapp.appsflyer

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import com.appsflyer.AFLogger
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AppsFlyerProperties
import com.appsflyer.ServerParameters
import kotlinx.android.synthetic.main.activity_conversion.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class ConversionActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversion)
        val referrer = AppsFlyerProperties.getInstance().getReferrer(this)
        if(referrer?.isNotEmpty() == true){
            tvGoogleReferrer.setTextColor(resources.getColor(R.color.colorPrimary))
            tvGoogleReferrer.text = referrer
        } else {
            tvGoogleReferrer.setTextColor(resources.getColor(R.color.colorAccent))
            tvGoogleReferrer.text = getString(R.string.google_referrer_not_set)
        }
        val customerUserId = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.APP_USER_ID)
        if(customerUserId?.isNotEmpty() == true){
            tvCustomerUserId.setTextColor(resources.getColor(R.color.colorPrimary))
            tvCustomerUserId.text = customerUserId
        }else{
            tvCustomerUserId.setTextColor(resources.getColor(R.color.colorAccent))
            tvCustomerUserId.text = getString(R.string.customer_user_id_not_set)
        }
        tvGAID.text = AppsFlyerProperties.getInstance().getString("advertiserId")
        tvAppsFlyerId.text = AppsFlyerProperties.getInstance().getString("uid")
        tvCVData.text = getConversionDataText().takeIf { it.isNotEmpty() } ?: "Initializing"
        tvRetargetData.text = getRetargetingDataText().takeIf { it.isNotEmpty() } ?: "No Re-targeting Data!"
    }

    private fun getConversionDataText(map: Map<String, String>? = getConversionDataMap()): String {
        val sb = StringBuffer()
        map?.map {
            sb.append("${it.key} : ${it.value}\n")
        }
        return sb.toString()
    }
    private fun getRetargetingDataText(map: Map<String, String>? = getRetargetingDataMap()): String {
        val sb = StringBuffer()
        map?.map {
            sb.append("${it.key} : ${it.value}\n")
        }
        return sb.toString()
    }
    private fun getConversionDataMap(): Map<String, String>? {
        val sharedPreferences = getSharedPreferences("appsflyer-data", 0)
        val inputString = sharedPreferences.getString("attributionId", null) ?: return null
        val conversionData = HashMap<String, String>()

        try {
            val jsonObject = JSONObject(inputString)
            val iterator = jsonObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next() as String
                if ("is_cache" != key) {
                    val value = jsonObject.getString(key)
                    if (!TextUtils.isEmpty(value) && "null" != value) {
                        conversionData[key] = value
                    }
                }
            }
        } catch (e: JSONException) {
            AFLogger.afErrorLog(e.message, e)
            return null
        }

        return conversionData
    }

    private fun getRetargetingDataMap(): Map<String, String>? {
        val sharedPreferences = getSharedPreferences("retargeting", 0)
        val json_data = sharedPreferences.getString("json_data", null) ?: return null
        val conversionData = HashMap<String, String>()

        try {
            val jsonObject = JSONObject(json_data)
            val iterator = jsonObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next() as String
                val value = jsonObject.getString(key)
                if (!TextUtils.isEmpty(value) && "null" != value) {
                    conversionData[key] = value
                }
            }
        } catch (e: JSONException) {
            AFLogger.afErrorLog(e.message, e)
            return null
        }

        return conversionData
    }

}