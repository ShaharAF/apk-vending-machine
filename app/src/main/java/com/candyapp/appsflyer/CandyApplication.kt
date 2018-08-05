package com.candyapp.appsflyer

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib

class CandyApplication: Application(), AppsFlyerConversionListener {
    val TAG = CandyApplication::class.java.simpleName
    val appsFlyerDevKey: String = "HvYAWjCjQXts7xbPJuTjfn"
    override fun onCreate() {
        super.onCreate()
        Log.d(AppsFlyerLib.LOG_TAG, "[$TAG][onCreate]")
        AppsFlyerLib.getInstance().setCollectIMEI(false)
        AppsFlyerLib.getInstance().setDebugLog(true)
        AppsFlyerLib.getInstance().registerConversionListener(this, this)
        AppsFlyerLib.getInstance().startTracking(this, appsFlyerDevKey)
    }

    override fun onAppOpenAttribution(conversionData: MutableMap<String, String>?) {
        Log.d(AppsFlyerLib.LOG_TAG, "[$TAG][onAppOpenAttribution]")
        conversionData?.let { data ->
            data.map{ Log.d(AppsFlyerLib.LOG_TAG, "key: ${it.key} Value: ${it.value}") }
        }
    }

    override fun onAttributionFailure(p0: String?) {
        Log.d(AppsFlyerLib.LOG_TAG, "[$TAG] [onAttributionFailure] ${p0}")
    }

    override fun onInstallConversionDataLoaded(conversionData: MutableMap<String, String>?) {
        Log.d(AppsFlyerLib.LOG_TAG, "[$TAG][onInstallConversionDataLoaded]")
        conversionData?.let { data ->
            data.map{ Log.d(AppsFlyerLib.LOG_TAG,"key: ${it.key} Value: ${it.value}") }
        }
    }

    override fun onInstallConversionFailure(p0: String?) {
        Log.d(AppsFlyerLib.LOG_TAG, "[$TAG][onInstallConversionFailure] ${p0}")
    }

}