package com.candyapp.appsflyer

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import com.appsflyer.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_buy_discount.*
import org.json.JSONObject

class BuyDiscountActivity: Activity(), AppsFlyerConversionListener {
    val TAG: String = BuyDiscountActivity::class.java.simpleName
    override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
        Log.i(AppsFlyerLib.LOG_TAG, "[$TAG][onAppOpenAttribution] >>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        val json = JSONObject()
        data?.map{
            Log.i(AppsFlyerLib.LOG_TAG,"key: ${it.key} Value: ${it.value}")
            json.put(it.key, it.value)
        }
        val sp = getSharedPreferences("retargeting", 0)
        sp.edit().putString("json_data", json.takeIf { it.length()>0 }?.toString()).apply()

        if(productType == null) {
            productType = ProductType.fromAd(data?.get("af_ad"))
            uiHandler.sendMessage(Message().apply {
                this.what = 1

            })
        }
        Log.i(AppsFlyerLib.LOG_TAG, "[$TAG][onAppOpenAttribution] <<<<<<<<<<<<<<<<<<<<<<<<<<<<")

    }

    override fun onAttributionFailure(p0: String?) {
        Log.e(AppsFlyerLib.LOG_TAG, "[$TAG][onAttributionFailure] ${p0}")
    }

    override fun onInstallConversionDataLoaded(data: MutableMap<String, String>?) {
        Log.i(AppsFlyerLib.LOG_TAG, "[$TAG][onInstallConversionDataLoaded] >>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        data?.map{
            Log.i(AppsFlyerLib.LOG_TAG,"key: ${it.key} Value: ${it.value}")
        }
        Log.e(AppsFlyerLib.LOG_TAG, "[$TAG][onInstallConversionDataLoaded] <<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }

    override fun onInstallConversionFailure(p0: String?) {
        Log.e(AppsFlyerLib.LOG_TAG, "[$TAG][onInstallConversionFailure] ${p0}")
    }

    companion object {
        val PARAM_AD = "PARAM_AD"
    }

    var totalPrice: Float = 0F
    var totalOrigPrice: Float = 0F
    var discountPercent: Int = 0
    var uiHandler = Handler(Handler.Callback {
        onProductTypeLoaded()
        updateUI()
        true
    })
    private lateinit var item: ProductItem
    private var productType: ProductType? = null
    private var quatity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_discount)
        AppsFlyerLib.getInstance().registerConversionListener(this@BuyDiscountActivity, this)
        AppsFlyerLib.getInstance().sendDeepLinkData(this)
        intent.data?.let checkLink@{ data ->
            // From Scheme
            if (data.scheme == "candyapp") {
                data.getQueryParameter("af_ad")?.let {
                    Log.i(AppsFlyerLib.LOG_TAG, "Deep Link Data: ${it}")
                    ProductType.fromAd(ad = it)?.let {
                        productType = it
                    } ?: DialogFactory.showAlert(this@BuyDiscountActivity, getString(R.string.wrong_parameter))
                }
            }
            // From OneLink
            else if(data.scheme == "http" || data.scheme == "https") { //
                // Short link
                if(data.queryParameterNames.isNullOrEmpty()) { // If it's a short link
                    DialogFactory.showProgressBar(this@BuyDiscountActivity)
                } else {
                    data.getQueryParameter("af_ad")?.let {
                        Log.i(AppsFlyerLib.LOG_TAG, "Deep Link Data: ${it}")
                        ProductType.fromAd(ad = it)?.let {
                            productType = it
                        }
                    }
                }
            } else {

            }
        }?:kotlin.run {
            productType = ProductType.fromAd(intent.getStringExtra(PARAM_AD))
//            discountPercent = intent.getIntExtra(PARAM_DISCOUNT_PERCENT, 0)
        }
        onProductTypeLoaded()
        updateUI()
        inputQuantity.setText("$quatity")
        inputQuantity.setSelection(0, quatity.toString().length)
        inputQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quatity = s?.toString().takeUnless { it.isNullOrEmpty() }?.toIntOrNull() ?: 1
                updateUI()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
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
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }
    private fun onProductTypeLoaded(){
        when(productType){
            ProductType.MM -> {
                setTitle(R.string.buy_mm_discount)
                discountPercent = 25
                item = ProductItem(getString(R.string.mandm), R.mipmap.mandm, 10)
            }
            ProductType.Skittles -> {
                setTitle(R.string.buy_skittles_discount)
                discountPercent = 20
                item = ProductItem(getString(R.string.skittles), R.mipmap.skittles, 10)
            }
            else -> {
                DialogFactory.showAlert(this, getString(R.string.wrong_parameter))
                return
            }
        }
        imageView.setImageResource(item.imageRes)
        textTitle.text = item.name
        textPrice.text = " × $${item.price}"
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