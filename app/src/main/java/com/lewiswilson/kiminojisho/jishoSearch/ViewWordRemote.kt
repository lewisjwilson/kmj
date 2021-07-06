package com.lewiswilson.kiminojisho.jishoSearch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.lewiswilson.kiminojisho.*
import kotlinx.android.synthetic.main.search_data_item.*
import kotlinx.android.synthetic.main.view_word.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList

class ViewWordRemote : AppCompatActivity() {

    private var kanji: String? = ""
    private var kana: String? = ""
    private var english: String? = ""
    private var example: String? = ""
    private var notes: String? = ""
    private var starFilled: Boolean = false
    private var starFilledInitial: Boolean = false
    private val myDB = DatabaseHelper(this)
    private var examplesList: ArrayList<ExamplesItem>? = ArrayList()
    private var rvAdapter: ExamplesAdapter? = null

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
        //view_examples.text = example
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

        //initiate recyclerview and set parameters
        rv_examples.setHasFixedSize(true)
        rv_examples.setLayoutManager(LinearLayoutManager(this))

        // search word and add matching examples to recycler
        kanji?.let { readExamples(it) }


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
            startActivity(Intent(this@ViewWordRemote, MyList::class.java))
            finish()
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
        rvAdapter = examplesList?.let { it -> ExamplesAdapter(this@ViewWordRemote, it) }
        rv_examples.adapter = rvAdapter

    }



}
