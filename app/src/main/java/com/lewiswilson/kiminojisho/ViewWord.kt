package com.lewiswilson.kiminojisho

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.view_word.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.*

class ViewWord : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_word)

        val myDB = DatabaseHelper(this)
        val itemId: String = MainActivity.item_id.toString()
        val itemData = myDB.getData(Integer.parseInt(itemId))

        readExamples()

        // coming from Mylist, so default is filled star
        view_star.setImageResource(R.drawable.star_filled)
        var star_filled = true
        view_star.setOnClickListener{
            star_filled = !star_filled
            if(star_filled){
                view_star.setImageResource(R.drawable.star_filled)
            } else {
                view_star.setImageResource(R.drawable.star_empty)
            }
        }

        //parsing from hashmap in DatabaseHelper.kt
        val kanji = itemData["kanji"]
        val kana = itemData["kana"]
        val english = itemData["english"]
        val example = itemData["example"]
        val notes = itemData["notes"]

        view_kanji.text = kanji
        view_kana.text = kana
        view_english.text = english
        view_examples.text = example
        view_notes.text = notes

        btn_delete.setOnClickListener { v: View? ->
            myDB.deleteData(itemId)
            Toast.makeText(this@ViewWord, "Entry Deleted", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@ViewWord, MainActivity::class.java))
            finish()
            MainActivity.ma!!.finish()
        }

    }

    fun readExamples(){

        val minput = InputStreamReader(assets.open("examples.tsv"))
        val reader = BufferedReader(minput)

        var line : String?
        Log.d(TAG, "HERE")
        var last = "NONE"

        while (reader.readLine().also { line = it } != null){
            val row : List<String> = line!!.split("\t")
            last = "${row[0]} | ${row[1]}"
        }

        Log.d(TAG, last)

    }

}