package com.lewiswilson.kiminojisho

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class About : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.OverlayTurquoise, true)
        setContentView(R.layout.about)
    }
}