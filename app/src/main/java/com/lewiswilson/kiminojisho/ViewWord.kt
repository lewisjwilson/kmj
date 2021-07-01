package com.lewiswilson.kiminojisho

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.view_word.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*
import java.util.ArrayList
import kotlin.collections.*

class ViewWord : AppCompatActivity() {

    private var examplesList: ArrayList<ExamplesItem>? = ArrayList()
    private var rvAdapter: ExamplesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_word)

        val myDB = DatabaseHelper(this)
        val itemId: String = MainActivity.item_id.toString()
        val itemData = myDB.getData(Integer.parseInt(itemId))

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
        //view_examples.text = example
        view_notes.text = notes

        //initiate recyclerview and set parameters
        rv_examples.setHasFixedSize(true)
        rv_examples.setLayoutManager(LinearLayoutManager(this))

        // search word and add matching examples to recycler
        if (kanji != null) {
            readExamples(kanji)
        }


    }

    fun readExamples(filter: String) {

        val minput = InputStreamReader(assets.open("examples.tsv"))
        val reader = BufferedReader(minput)

        var line : String?

        val examples_no = 10
        var count = 0

        while (reader.readLine().also { line = it } != null){
            if (reader.readLine().also { line = it}.contains(filter)) {
                val japanese = line!!.split("\t")[0]
                val english = line!!.split("\t")[1]
                examplesList?.add(ExamplesItem(japanese, english))
                count++
            }
            if (count>=examples_no)
            {
                break
            }

        }
        rvAdapter = examplesList?.let { it -> ExamplesAdapter(this@ViewWord, it) }
        rv_examples.adapter = rvAdapter

    }

}