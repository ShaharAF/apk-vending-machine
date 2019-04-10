package com.candyapp.appsflyer

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.appsflyer.*
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

        if(isShortOneLink) {
            afAd = data?.get("af_ad")
            data?.get("discount")?.toInt()?.let { discountPercent = it }
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
        val PARAM_DISCOUNT = "PARAM_DISCOUNT"
    }

    var totalPrice: Float = 0F
    var totalOrigPrice: Float = 0F
    var discountPercent: Int? = 0
    var uiHandler = Handler(Handler.Callback {
        onProductTypeLoaded()
        updateUI()
        true
    })
    private var item: ProductItem? = null
//    private var productType: ProductType? = null
    private var afAd: String? = null
    private var quatity: Int = 1
    private var isShortOneLink = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_discount)
        btnPurchase.setOnClickListener {
            val params = mutableMapOf<String, Any?>()
            params.put(AFInAppEventParameterName.REVENUE, totalPrice)
            params.put(AFInAppEventParameterName.CURRENCY, "USD")
            params.put(AFInAppEventParameterName.QUANTITY, quatity)
            params.put(AFInAppEventParameterName.PRICE, item?.price)
            params.put(AFInAppEventParameterName.CONTENT, item?.name)
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
        inputQuantity.setText("$quatity")
//        inputQuantity.setSelection(0, quatity.toString().length)
        inputQuantity.clearFocus()
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
        AppsFlyerLib.getInstance().registerConversionListener(this@BuyDiscountActivity, this)
        AppsFlyerLib.getInstance().sendDeepLinkData(this)
        intent.data?.let { data ->
            Log.i(AppsFlyerLib.LOG_TAG, "Deep Link Data: $data")
            // Short link
            if (data.scheme == "https" && data.queryParameterNames.isNullOrEmpty()) { //
                isShortOneLink = true
                DialogFactory.showProgressBar(this@BuyDiscountActivity, onCanceled = DialogInterface.OnClickListener { d, _ -> d.dismiss() })
                return@onCreate
            }
            // From Scheme
            if (data.scheme == "candyapp" || (data.scheme == "https" && data.queryParameterNames.isNotEmpty())) {
                readProductType(data.getQueryParameter("af_ad"), data.getQueryParameter("discount"))
            }
        } ?: kotlin.run {
            readProductType(intent.getStringExtra(PARAM_AD), intent.getStringExtra(PARAM_DISCOUNT))
        }
        if (!isShortOneLink) {
            onProductTypeLoaded()
        }
    }
    private fun readProductType(ad: String?, discount: String?) {
        afAd = ad
        try {
            discountPercent = discount?.toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }
    private fun onProductTypeLoaded(){
        afAd?.let { ad ->
            ProductType.fromAd(ad)?.let { productType ->
                when (productType) {
                    ProductType.MM -> {
                        discountPercent = discountPercent ?: 25
                        item = ProductItem(getString(R.string.mandm), R.mipmap.mandm, 10)
                    }
                    ProductType.Skittles -> {
                        discountPercent = discountPercent ?: 20
                        item = ProductItem(getString(R.string.skittles), R.mipmap.skittles, 10)
                    }
                    ProductType.iPhone -> {
                        discountPercent = discountPercent ?: 5
                        item = ProductItem(getString(R.string.iphone), R.mipmap.iphone, 999)
                    }
                    ProductType.Xiaomi -> {
                        discountPercent = discountPercent ?: 20
                        item = ProductItem(getString(R.string.xiaomi), R.mipmap.xiaomi, 699)
                    }
                    ProductType.BlackShoes -> {
                        discountPercent = discountPercent ?: 60
                        item = ProductItem(getString(R.string.black_shoes), R.mipmap.black_shoes, 100)
                    }
                    ProductType.RedShoes -> {
                        discountPercent = discountPercent ?: 40
                        item = ProductItem(getString(R.string.red_shoes), R.mipmap.red_shoes, 100)
                    }
                    ProductType.WhiteShoes -> {
                        discountPercent = discountPercent ?: 30
                        item = ProductItem(getString(R.string.white_shoes), R.mipmap.white_shoes, 100)
                    }
                    else -> {
                        showError(getString(R.string.unsupport_product))
                        return
                    }
                }
                item?.let {prodItem ->
                    discountPercent?.let { percent ->
                        title = getString(R.string.buy_prod_discount, prodItem.name, "$percent%")
                    }
                    imageView.setImageResource(prodItem.imageRes)
                    textTitle.text = prodItem.name
                    textPrice.text = " × $${prodItem.price}"
                }
                updateUI()
            }?: showError(getString(R.string.error_invalid_af_ad, ad))
        } ?: showError(getString(R.string.error_no_af_ad))
    }

    private var errDialog: AlertDialog? = null

    override fun onDestroy() {
        errDialog=null
        super.onDestroy()
    }
    fun showError(message: String) {
        errDialog?.dismiss()
        errDialog = DialogFactory.showAlert(this@BuyDiscountActivity, message) {
            setPositiveButton(android.R.string.ok) {
                d,_ -> d.dismiss()
                errDialog=null
                finish()
            }
        }
    }
    fun updateUI(){
        totalOrigPrice = (quatity * (item?.price ?: 0)).toFloat()
        totalPrice = totalOrigPrice * (100 - (discountPercent ?: 0)) / 100F
        textOrigPrice.text = String.format("$%.2f", totalOrigPrice)
        textViewDiscount.text = "-${discountPercent}" + "%"
        textTotalPrice.text = String.format("$%.2f", totalPrice)
        Log.i(AppsFlyerLib.LOG_TAG, "[updateUI] discountPercent: ${discountPercent}, totalPrice: $totalPrice")
    }
}