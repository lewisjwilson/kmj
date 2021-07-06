package com.lewiswilson.kiminojisho.misc

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.lewiswilson.kiminojisho.HomeScreen
import com.lewiswilson.kiminojisho.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreen, HomeScreen::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}