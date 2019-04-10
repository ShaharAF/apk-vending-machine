package com.candyapp.appsflyer

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import org.json.JSONObject

class CandyApplication: Application(), AppsFlyerConversionListener {
    companion object {
        var isAttributed = false
        var isDeferredDeepLink = false
        var devMode = BuildConfig.DEBUG
        var cv: MutableMap<String, String>? = null
    }
    val TAG = CandyApplication::class.java.simpleName
    val appsFlyerDevKey: String = "AF_DEV_KEY_PLACEHOLDER"
    override fun onCreate() {
        super.onCreate()
        Log.d(AppsFlyerLib.LOG_TAG, "[$TAG][onCreate]")
        AppsFlyerLib.getInstance().init(appsFlyerDevKey, this)
        AppsFlyerLib.getInstance().setCollectIMEI(false)
        AppsFlyerLib.getInstance().setDebugLog(true)
        AppsFlyerLib.getInstance().enableUninstallTracking("717887381464")
        AppsFlyerLib.getInstance().setCustomerUserId("guest")
        AppsFlyerLib.getInstance().startTracking(this)
    }

    override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
        Log.i(AppsFlyerLib.LOG_TAG, "[$TAG][onAppOpenAttribution] >>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        val json = JSONObject()
        data?.map{
            Log.i(AppsFlyerLib.LOG_TAG,"key: ${it.key} Value: ${it.value}")
            json.put(it.key, it.value)
        }
        val sp = getSharedPreferences("retargeting", 0)
        sp.edit().putString("json_data", json.takeIf { it.length()>0 }?.toString()).apply()

        Log.i(AppsFlyerLib.LOG_TAG, "[$TAG][onAppOpenAttribution] <<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }

    override fun onAttributionFailure(p0: String?) {
        Log.d(AppsFlyerLib.LOG_TAG, "[$TAG] [onAttributionFailure] ${p0}")
    }

    override fun onInstallConversionDataLoaded(conversionData: MutableMap<String, String>?) {
        Log.d(AppsFlyerLib.LOG_TAG, "[$TAG][onInstallConversionDataLoaded]")
        isDeferredDeepLink = false
        conversionData?.let { data ->
            data.map{ Log.d(AppsFlyerLib.LOG_TAG,"key: ${it.key} Value: ${it.value}") }
            if(data["is_first_launch"] == "true") {
                if(data["af_adset"] == "deferreddeeplink") {
                    isDeferredDeepLink = true
                    with(Intent(this, BuyDiscountActivity::class.java)) {
                        putExtra(BuyDiscountActivity.PARAM_AD, data["af_ad"])
                        putExtra(BuyDiscountActivity.PARAM_DISCOUNT, data["discount"])
                        startActivity(this@with)
                    }
                    return
                }
            }
        }
        isAttributed = true
        cv = conversionData
    }

    override fun onInstallConversionFailure(p0: String?) {
        Log.d(AppsFlyerLib.LOG_TAG, "[$TAG][onInstallConversionFailure] ${p0}")
    }

}