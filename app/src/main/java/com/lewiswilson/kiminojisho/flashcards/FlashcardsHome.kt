package com.lewiswilson.kiminojisho.flashcards

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lewiswilson.kiminojisho.DatabaseHelper
import com.lewiswilson.kiminojisho.HomeScreen
import com.lewiswilson.kiminojisho.R
import kotlinx.android.synthetic.main.flashcards_home.*

class FlashcardsHome : AppCompatActivity() {

    private var myDB: DatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.Nature, true)
        setContentView(R.layout.flashcards_home)
        myDB = DatabaseHelper(this)

        val reviewsDue = myDB?.flashcardCount()
        val noOfFlashcards = "Reviews: $reviewsDue"
        fc_no.text = noOfFlashcards

        // itemcount < 4 prevents any issues regarding multiple choice in Flashcards.kt
        if(reviewsDue!! <= 0){
            btn_start.isEnabled = false
        }
        if(myDB!!.itemCount() < 4){
            btn_start.isEnabled = false
            txt_not_enough_items.visibility = View.VISIBLE
        }

        btn_start.setOnClickListener { v: View? ->
            finish()
            startActivity(Intent(this@FlashcardsHome, Flashcards::class.java)) }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(Intent(this@FlashcardsHome, HomeScreen::class.java))
    }
}