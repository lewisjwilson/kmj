package com.lewiswilson.kiminojisho.flashcards

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lewiswilson.kiminojisho.DatabaseHelper
import com.lewiswilson.kiminojisho.R
import kotlinx.android.synthetic.main.flashcards_home.*

class FlashcardsHome : AppCompatActivity() {

    private var myDB: DatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flashcards_home)
        myDB = DatabaseHelper(this)

        val reviewsDue = myDB?.flashcardCount()
        fc_no.text = "Reviews: $reviewsDue"

        btn_start.setOnClickListener { v: View? -> startActivity(Intent(this@FlashcardsHome, Flashcards::class.java)) }

    }
}