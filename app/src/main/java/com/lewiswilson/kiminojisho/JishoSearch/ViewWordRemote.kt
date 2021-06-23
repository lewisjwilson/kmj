package com.lewiswilson.kiminojisho.JishoSearch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.lewiswilson.kiminojisho.DatabaseHelper
import com.lewiswilson.kiminojisho.MainActivity
import com.lewiswilson.kiminojisho.R
import kotlinx.android.synthetic.main.view_word.*

class ViewWordRemote : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_word)

        //implementing ads
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)


        view_star.setImageResource(R.drawable.star_empty)
        var star_filled = false
        view_star.setOnClickListener{
            star_filled = !star_filled
            if(star_filled){
                view_star.setImageResource(R.drawable.star_filled)
            } else {
                view_star.setImageResource(R.drawable.star_empty)
            }
        }

        //parsing from hashmap in DatabaseHelper.kt
        val kanji = intent.getStringExtra("kanji")
        val kana = intent.getStringExtra("kana")
        val english = intent.getStringExtra("english")
        val example = intent.getStringExtra("example")
        val notes = intent.getStringExtra("notes")

        view_kanji.text = kanji
        view_kana.text = kana
        view_english.text = english
        view_examples.text = example
        view_notes.text = notes

        btn_delete.setOnClickListener { v: View? ->
            //myDB.deleteData(itemId)
            Toast.makeText(this@ViewWordRemote, "Entry Deleted", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@ViewWordRemote, MainActivity::class.java))
            finish()
            MainActivity.ma!!.finish()
        }

    }
}