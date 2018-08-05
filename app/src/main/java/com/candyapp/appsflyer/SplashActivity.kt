package com.candyapp.appsflyer

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler

class SplashActivity: Activity() {
    private lateinit var timerHandler: Handler

    var count: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        timerHandler = Handler(Handler.Callback({
            gotoMainActivity()
            return@Callback true
        }))
        timerHandler.sendEmptyMessageDelayed(count, 400)
    }

    private fun gotoMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}