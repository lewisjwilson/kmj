package com.lewiswilson.kiminojisho.flashcards

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lewiswilson.kiminojisho.DatabaseHelper
import com.lewiswilson.kiminojisho.R
import kotlinx.android.synthetic.main.flashcards_complete.*
import kotlinx.android.synthetic.main.flashcards_home.*

class FlashcardsComplete : AppCompatActivity() {

    private var myDB: DatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flashcards_complete)

        btn_back.setOnClickListener { v: View? ->
            finish()
            startActivity(Intent(this@FlashcardsComplete, FlashcardsHome::class.java)) }

    }
}