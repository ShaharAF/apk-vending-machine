package com.candyapp.appsflyer

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import com.appsflyer.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_buy_discount.*

class BuyDiscountActivity: Activity(), AppsFlyerConversionListener {
    override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
        data?.map{ Log.d(AppsFlyerLib.LOG_TAG,"key: ${it.key} Value: ${it.value}") }
        discountPercent = data?.get("discount")?.toInt() ?: 0
        updateUI()
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

    val currency = "USD"
    var totalPrice: Float = 0F
    var discountPercent: Int = 0

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
            }
        }?:kotlin.run {
            discountPercent = intent.getIntExtra(PARAM_DISCOUNT_PERCENT, 0)
        }

        quatity = 1
        totalPrice = quatity * item.price * (100 - discountPercent) / 100F
        imageView.setImageResource(item.imageRes)
        textTitle.text = item.name
        textPrice.text = " × ${item.price} ${currency}"
        inputQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quatity = s?.toString().takeUnless { TextUtils.isEmpty(it) }?.toIntOrNull() ?: 1
                totalPrice = quatity * item.price * (100 - discountPercent) / 100F
                textTotalPrice.text = "$totalPrice $currency"
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
            params.put(AFInAppEventParameterName.CURRENCY, currency)
            params.put(AFInAppEventParameterName.QUANTITY, quatity)
            params.put(AFInAppEventParameterName.PRICE, item.price)
            params.put(AFInAppEventParameterName.CONTENT, item.name)
            AppsFlyerLib.getInstance().trackEvent(applicationContext, AFInAppEventType.PURCHASE, params)
            DialogFactory.showEventSent(this@BuyDiscountActivity, AFInAppEventType.PURCHASE, AFHelper.convertToJsonObject(params).toString())
        }
        btnBack.setOnClickListener { finish() }
    }
    fun updateUI(){
        textViewDiscount.text = "-${discountPercent}" + "%"
        totalPrice = quatity * item.price * (100 - discountPercent) / 100F
        textTotalPrice.text = "$totalPrice $currency"
        textViewDiscount.invalidate()
        textTotalPrice.invalidate()
    }
}