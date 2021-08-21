package com.lewiswilson.kiminojisho.flashcards

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lewiswilson.kiminojisho.R
import kotlinx.android.synthetic.main.flashcards_complete.*
import kotlinx.android.synthetic.main.flashcards_home.*

class FlashcardsComplete : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flashcards_complete)

        val percent = String.format("%.2f", intent.getDoubleExtra("percent", 0.0))
        val statsText = "Review Accuracy: ${percent}%"
        txt_stats.text = statsText

        btn_back.setOnClickListener { v: View? ->
            finish()
            startActivity(Intent(this@FlashcardsComplete, FlashcardsHome::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

    }
}