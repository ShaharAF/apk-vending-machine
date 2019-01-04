package com.candyapp.appsflyer

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_buy.*

class SplashActivity: Activity() {
    private lateinit var timerHandler: Handler
    var devModeCountDown = 5
    var msgNext: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        timerHandler = Handler(Handler.Callback {
            gotoMainActivity()
            return@Callback true
        })
        timerHandler.sendEmptyMessageDelayed(msgNext, 1000)
        imageView.setOnClickListener {
            timerHandler.removeMessages(msgNext)
            if(--devModeCountDown==0) {
                CandyApplication.devMode = true
                Toast.makeText(this, R.string.dev_mode_on, Toast.LENGTH_LONG).show()
                timerHandler.sendEmptyMessageDelayed(msgNext, 200)
            }else {
                timerHandler.sendEmptyMessageDelayed(msgNext, 500)
            }
        }
    }

    private fun gotoMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}