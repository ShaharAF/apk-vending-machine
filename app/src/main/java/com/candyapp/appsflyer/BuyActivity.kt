package com.candyapp.appsflyer

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_buy.*

class BuyActivity: Activity() {
    companion object {
        val ITEM_TYPE = "ITEM_TYPE"
        val ITEM_TYPE_MM:Int = 1
        val ITEM_TYPE_SKITTLES:Int = 2
    }

    val currency = "USD"
    var totalPrice = 0
    private var type: Int = ITEM_TYPE_MM

    private lateinit var item: ProductItem
    private var quatity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)
        type = intent.getIntExtra(ITEM_TYPE, ITEM_TYPE_MM)
        when(type) {
            ITEM_TYPE_SKITTLES -> { item = ProductItem(getString(R.string.skittles), R.mipmap.skittles, 10); setTitle(R.string.buy_skittles)}
            else -> { item = ProductItem(getString(R.string.mandm), R.mipmap.mandm, 5); setTitle(R.string.buy_mm) }
        }
        quatity = 1
        totalPrice = quatity * item.price
        imageView.setImageResource(item.imageRes)
        textPrice.text = "${item.price} ${currency}"
        textTitle.text = item.name
        textTotalPrice.text = "${item.price} ${currency}"
        inputQuantity.setText("$quatity")
        inputQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quatity = s?.toString().takeUnless { TextUtils.isEmpty(it) }?.toIntOrNull() ?: 1
                totalPrice = quatity * item.price
                textTotalPrice.text = "$totalPrice $currency"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        btnPurchase.setOnClickListener {
            val params = mutableMapOf<String, Any?>()
            params.put(AFInAppEventParameterName.REVENUE, totalPrice)
            params.put(AFInAppEventParameterName.CURRENCY, currency)
            params.put(AFInAppEventParameterName.QUANTITY, quatity)
            params.put(AFInAppEventParameterName.PRICE, item.price)
            params.put(AFInAppEventParameterName.CONTENT, item.name)
            AppsFlyerLib.getInstance().trackEvent(applicationContext, AFInAppEventType.PURCHASE, params)
            AlertDialog.Builder(this@BuyActivity)
                    .setPositiveButton(android.R.string.ok, {dialog, which -> dialog.dismiss() })
                    .setMessage("Event Sent: ${AFInAppEventType.PURCHASE} \n ${Gson().toJson(params)}")
                    .create()
                    .show()
        }
        btnBack.setOnClickListener { finish() }
    }
}