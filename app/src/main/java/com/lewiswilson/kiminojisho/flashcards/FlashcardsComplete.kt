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
        theme.applyStyle(R.style.Nature, true)
        setContentView(R.layout.flashcards_complete)

        val percent = intent.getDoubleExtra("percent", 0.0)
        val statsText = "You got ${percent}% correct."
        txt_stats.text = statsText

        btn_back.setOnClickListener { v: View? ->
            finish()
            startActivity(Intent(this@FlashcardsComplete, FlashcardsHome::class.java)) }

    }
}