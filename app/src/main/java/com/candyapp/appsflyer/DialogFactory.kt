package com.candyapp.appsflyer

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import java.util.*
import android.widget.Toast
import android.content.Context.CLIPBOARD_SERVICE
import android.view.View
import android.view.ViewGroup
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AppsFlyerProperties


object DialogFactory {
    fun showEventSent(context: Context, name: String, value: String?) {
        val adb = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_evt_sent, null, false)
        dialogView.findViewById<TextView>(R.id.tvEventName).text = name
        dialogView.findViewById<TextView>(R.id.tvEventValue).text = value
        adb.setView(dialogView)
        adb.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }.create().show()
    }
    fun showGoogleReferrer(context: Context){
        val adb = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_gp_referrer, null, false)
        val referrer = AppsFlyerProperties.getInstance().getReferrer(context)
        if(referrer?.length ?: 0 > 0) {
            dialogView.findViewById<TextView>(R.id.tvGoogleReferrer).text = referrer
            dialogView.findViewById<ViewGroup>(R.id.layoutGoogleReferrer).visibility = View.VISIBLE
            dialogView.findViewById<TextView>(R.id.tvGoogleReferrerNotSet).visibility = View.GONE
        }
        adb.setView(dialogView)
        adb.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }.create().show()
    }
    fun showAppsFlyerId(context: Context) {
        val adb = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_appsflyer_id, null, false)
        dialogView.findViewById<TextView>(R.id.tvAppsFlyerId).text = AppsFlyerLib.getInstance().getAppsFlyerUID(context)
        adb.setView(dialogView)
        adb.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }.create().show()
    }
    fun showCustomerUserId(context: Context) {
        val adb = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_user_id, null, false)
        val customerUserId = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.APP_USER_ID)
        if(customerUserId?.length ?: 0 > 0) {
            dialogView.findViewById<TextView>(R.id.tvAppsFlyerId).text = customerUserId
            dialogView.findViewById<ViewGroup>(R.id.layoutCustomerUserId).visibility = View.VISIBLE
            dialogView.findViewById<TextView>(R.id.tvCustomerUserIdNotSet).visibility = View.GONE
        }
        adb.setView(dialogView)
        adb.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }.create().show()
    }
}