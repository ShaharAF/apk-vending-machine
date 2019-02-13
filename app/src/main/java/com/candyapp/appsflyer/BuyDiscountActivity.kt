package com.candyapp.appsflyer

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import com.appsflyer.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_buy_discount.*
import org.json.JSONObject

class BuyDiscountActivity: Activity(), AppsFlyerConversionListener {
    override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
        val json = JSONObject()
        data?.map{
            Log.d(AppsFlyerLib.LOG_TAG,"key: ${it.key} Value: ${it.value}")
            json.put(it.key, it.value)
        }
        val sp = getSharedPreferences("retargeting", 0)
        sp.edit().putString("json_data", json.takeIf { it.length()>0 }?.toString()).apply()
        discountPercent = data?.get("discount")?.toInt() ?: 0
        uiHandler.sendEmptyMessage(1)

    }

    override fun onAttributionFailure(p0: String?) {
    }

    override fun onInstallConversionDataLoaded(p0: MutableMap<String, String>?) {
    }

    override fun onInstallConversionFailure(p0: String?) {
    }

    companion object {
        val PARAM_DISCOUNT_PERCENT = "DISCOUNT_PERCENT"
    }

    var totalPrice: Float = 0F
    var totalOrigPrice: Float = 0F
    var discountPercent: Int = 0

    var uiHandler = Handler(Handler.Callback {
        updateUI(); true
    })
    private lateinit var item: ProductItem
    private var quatity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_discount)
        setTitle(R.string.buy_skittles)
        item = ProductItem(getString(R.string.skittles), R.mipmap.skittles, 10)
        AppsFlyerLib.getInstance().registerConversionListener(this@BuyDiscountActivity, this)
        AppsFlyerLib.getInstance().sendDeepLinkData(this)
        intent.data?.let checkLink@{ data ->
            // Scheme
            if (data.scheme == "candyapp" || data.scheme == "http" || data.scheme == "https") {
                discountPercent = data.getQueryParameter("discount")?.toInt() ?: 0
                Log.i(AppsFlyerLib.LOG_TAG, "[updateUI] discountPercent: ${discountPercent}")
            }
        }?:kotlin.run {
            discountPercent = intent.getIntExtra(PARAM_DISCOUNT_PERCENT, 0)
        }

//        quatity = 1
//        totalPrice = quatity * item.price * (100 - discountPercent) / 100F
        imageView.setImageResource(item.imageRes)
        textTitle.text = item.name
        textPrice.text = " × $${item.price}"
        inputQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quatity = s?.toString().takeUnless { TextUtils.isEmpty(it) }?.toIntOrNull() ?: 1
                updateUI()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        inputQuantity.setText("$quatity")
        updateUI()
        btnPurchase.setOnClickListener {
            val params = mutableMapOf<String, Any?>()
            params.put(AFInAppEventParameterName.REVENUE, totalPrice)
            params.put(AFInAppEventParameterName.CURRENCY, "USD")
            params.put(AFInAppEventParameterName.QUANTITY, quatity)
            params.put(AFInAppEventParameterName.PRICE, item.price)
            params.put(AFInAppEventParameterName.CONTENT, item.name)
            AppsFlyerLib.getInstance().trackEvent(applicationContext, AFInAppEventType.PURCHASE, params)
            DialogFactory.showEventSent(this@BuyDiscountActivity, AFInAppEventType.PURCHASE, AFHelper.convertToJsonObject(params).toString())
        }
        btnBack.setOnClickListener { finish() }
    }
    fun updateUI(){
        totalOrigPrice = (quatity * item.price).toFloat()
        totalPrice = totalOrigPrice * (100 - discountPercent) / 100F
        textOrigPrice.text = String.format("$%.2f", totalOrigPrice)
        textViewDiscount.text = "-${discountPercent}" + "%"
        textTotalPrice.text = String.format("$%.2f", totalPrice)
        Log.i(AppsFlyerLib.LOG_TAG, "[updateUI] discountPercent: ${discountPercent}, totalPrice: $totalPrice")
    }
}