package com.lewiswilson.kiminojisho.jishoSearch

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.lewiswilson.kiminojisho.*
import kotlinx.android.synthetic.main.search_data_item.*
import kotlinx.android.synthetic.main.search_page.*
import kotlinx.android.synthetic.main.view_word.*
import kotlinx.android.synthetic.main.view_word.adView
import kotlinx.android.synthetic.main.view_word.rv_examples
import kotlinx.android.synthetic.main.view_word_remote.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList

class ViewWordRemote : AppCompatActivity() {

    private var kanji = ""
    private var kana = ""
    private var english = ""
    private var pos = ""
    private var notes = ""

    // testing recycler
    private var mRemoteItemList: ArrayList<ViewWordRemoteItem>? = ArrayList()
    private var mRemoteItemAdapter: ViewWordRemoteItemAdapter? = null

    private var inList: Boolean = false
    private var starFilled: Boolean = false
    private val myDB = DatabaseHelper(this)
    private var examplesList: ArrayList<ExamplesItem>? = ArrayList()
    private var rvAdapter: ExamplesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.Nature, true)
        setContentView(R.layout.view_word_remote)

        //implementing ads
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val adapterPos = intent.getIntExtra("adapterPos", 0)
        Log.d(TAG, "position: $adapterPos")
        inList = intent.getBooleanExtra("star_filled", false)
        starFilled = inList

        kanji = SearchPage.dataItems!![adapterPos].kanji
        kana = SearchPage.dataItems!![adapterPos].kana
        english = SearchPage.dataItems!![adapterPos].english
        pos = SearchPage.dataItems!![adapterPos].pos
        notes = SearchPage.dataItems!![adapterPos].notes.toString()


        // initialte word_recycler
        word_recycler.setHasFixedSize(true)
        word_recycler.setLayoutManager(LinearLayoutManager(this))

        // items to view in searchpage activity
        mRemoteItemList!!.add(ViewWordRemoteItem(kanji, kana, english, pos, notes, starFilled))

        //Toast.makeText(this, "WORD LIST SIZE: ${mRemoteItemList!!.size}", Toast.LENGTH_LONG).show()

        mRemoteItemAdapter = mRemoteItemList?.let { it ->
            ViewWordRemoteItemAdapter(this@ViewWordRemote, it)
        }
        word_recycler.adapter = mRemoteItemAdapter
        mRemoteItemAdapter?.notifyDataSetChanged()


        //initiate recyclerview and set parameters
        rv_examples.setHasFixedSize(true)
        rv_examples.setLayoutManager(LinearLayoutManager(this))

        // search word and add matching examples to recycler
        readExamples(kanji)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(Intent(this@ViewWordRemote, HomeScreen::class.java))
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
