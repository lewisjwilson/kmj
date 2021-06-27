package com.lewiswilson.kiminojisho.JishoSearch

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.lewiswilson.kiminojisho.DatabaseHelper
import com.lewiswilson.kiminojisho.MainActivity
import com.lewiswilson.kiminojisho.R
import kotlinx.android.synthetic.main.search_data_item.*
import kotlinx.android.synthetic.main.view_word.*
import kotlin.math.log

class ViewWordRemote : AppCompatActivity() {

    private var kanji: String? = ""
    private var kana: String? = ""
    private var english: String? = ""
    private var example: String? = ""
    private var notes: String? = ""
    private var starFilled: Boolean = false
    private var starFilledInitial: Boolean = false
    private val myDB = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_word)

        //implementing ads
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        //parsing from hashmap in DatabaseHelper.kt
        kanji = intent.getStringExtra("kanji")
        kana = intent.getStringExtra("kana")
        english = intent.getStringExtra("english")
        example = intent.getStringExtra("example")
        notes = intent.getStringExtra("notes")
        starFilled = intent.getBooleanExtra("star_filled", false)
        starFilledInitial = starFilled

        view_kanji.text = kanji
        view_kana.text = kana
        view_english.text = english
        view_examples.text = example
        view_notes.text = notes

        if(starFilled){
            view_star.setImageResource(R.drawable.star_filled)
        }

        view_star.setOnClickListener{
            starFilled = !starFilled
            if(starFilled){
                view_star.setImageResource(R.drawable.star_filled)
            } else {
                view_star.setImageResource(R.drawable.star_empty)
            }
        }

    }

    override fun onPause(){
        super.onPause()

        if(starFilled!=starFilledInitial) {
            if (starFilled) {
                myDB.addData(kanji!!, kana, english, example, notes)
            } else {
                try {
                    myDB.deleteFromRemote(kanji!!)
                } catch (e: NullPointerException) {
                    Log.d("Item Not Found", "Item not in dictionary. (Normal)")
                }
            }
            //go to mainactivity
            startActivity(Intent(this@ViewWordRemote, MainActivity::class.java))
            finish()
        }


    }



}
