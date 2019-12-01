package com.example.travelon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.travelon.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val splashTime = 2000L // 2 seconds
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler = Handler()

        handler.postDelayed({
            goToMainActivity()
        }, splashTime)
    }

    private fun goToMainActivity() {
        val mainActivityIntent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
    }
}
