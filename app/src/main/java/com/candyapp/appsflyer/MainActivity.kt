package com.candyapp.appsflyer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    val onButtonClick: View.OnClickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {
            with(Intent(this@MainActivity, BuyActivity::class.java)) {
                if(v!!.id == R.id.btnBuyMM)
                    putExtra(BuyActivity.ITEM_TYPE,  BuyActivity.ITEM_TYPE_MM)
                else
                    putExtra(BuyActivity.ITEM_TYPE,  BuyActivity.ITEM_TYPE_SKITTLES)
                startActivity(this@with)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnBuyMM.setOnClickListener(onButtonClick)
        btnBuySkittles.setOnClickListener(onButtonClick)
        btnRegister.setOnClickListener { startActivity(Intent(this@MainActivity, RegisterActivity::class.java)) }
    }
}
