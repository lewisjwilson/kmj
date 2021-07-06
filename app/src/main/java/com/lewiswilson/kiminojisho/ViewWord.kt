package com.lewiswilson.kiminojisho

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.my_list.*
import kotlinx.android.synthetic.main.view_word.*
import java.io.BufferedReader
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

        //implementing ads
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val myDB = DatabaseHelper(this)
        val itemId: String = MyList.itemId.toString()
        val itemData = myDB.getData(Integer.parseInt(itemId))

        // coming from Mylist, so default is filled star
        view_star.setImageResource(R.drawable.star_filled)
        var starFilled = true
        view_star.setOnClickListener{
            starFilled = !starFilled
            if(starFilled){
                view_star.setImageResource(R.drawable.star_filled)
            } else {
                view_star.setImageResource(R.drawable.star_empty)
            }
        }

        //parsing from hashmap in DatabaseHelper.kt
        val kanji = itemData["kanji"]
        val kana = itemData["kana"]
        val english = itemData["english"]
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

        val examplesNo = 10
        var count = 0

        while (reader.readLine().also { line = it } != null){
            if (reader.readLine().also { line = it}.contains(filter)) {
                val japanese = line!!.split("\t")[0]
                val english = line!!.split("\t")[1]
                examplesList?.add(ExamplesItem(japanese, english))
                count++
            }
            if (count>=examplesNo)
            {
                break
            }

        }
        rvAdapter = examplesList?.let { it -> ExamplesAdapter(this@ViewWord, it) }
        rv_examples.adapter = rvAdapter

    }

}