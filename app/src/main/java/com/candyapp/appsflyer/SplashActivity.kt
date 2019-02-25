package com.candyapp.appsflyer

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.appsflyer.AppsFlyerLib
import kotlinx.android.synthetic.main.activity_buy.*

class SplashActivity: Activity() {
    private lateinit var timerHandler: Handler
    private var devModeCountDown = 5
    private var count: Int = 1
    private var msgNext: Int = 2
    private var msgTimeout: Int = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        timerHandler = Handler(Handler.Callback {
            when(it.what) {
                msgTimeout -> {
                    Log.d(AppsFlyerLib.LOG_TAG,"[TimeOut] Goto Main!")
                    gotoMainActivity()
                }
                msgNext -> {
                    Log.d(AppsFlyerLib.LOG_TAG,"[TimeTik] isDeferredDeepLink=${CandyApplication.isDeferredDeepLink} isAttributed=${CandyApplication.isAttributed}")
                    if (CandyApplication.isDeferredDeepLink) {
                        finish()
                    } else if (CandyApplication.isAttributed) {
                        gotoMainActivity()
                    } else if (count < 20){
                        count++
                        timerHandler.sendEmptyMessageDelayed(msgNext, 200)
                    }
                }
            }
            return@Callback true
        })
        timerHandler.sendEmptyMessageDelayed(msgNext, 500)
        timerHandler.sendEmptyMessageDelayed(msgTimeout, 5000)
        imageView.setOnClickListener {
            timerHandler.removeMessages(msgNext)
            if(--devModeCountDown==0) {
                CandyApplication.devMode = true
                Toast.makeText(this, R.string.dev_mode_on, Toast.LENGTH_LONG).show()
                timerHandler.sendEmptyMessageDelayed(msgNext, 500)
            }
        }
    }
    override fun onStop() {
        super.onStop()
        Log.d(AppsFlyerLib.LOG_TAG,"[onStop]")
        timerHandler.removeMessages(msgNext)
        timerHandler.removeMessages(msgTimeout)
        if(CandyApplication.isDeferredDeepLink)
            finish()
    }
    override fun onResume() {
        super.onResume()
        Log.d(AppsFlyerLib.LOG_TAG,"[onResume]")
        if(CandyApplication.isDeferredDeepLink || CandyApplication.isAttributed){
            timerHandler.removeMessages(msgNext)
            timerHandler.sendEmptyMessageDelayed(msgNext, 500)
        }
    }

    private fun gotoMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}