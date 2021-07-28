package com.lewiswilson.kiminojisho

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Toast
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
    private val myDB = DatabaseHelper(this)
    private var inList = true
    val itemId: String = MyList.clickedItemId.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.Nature, true)
        setContentView(R.layout.view_word)

        //implementing ads
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val itemData = myDB.getData(Integer.parseInt(itemId))

        // coming from Mylist, so default is filled star
        btn_star.setImageResource(R.drawable.ic_removeword)

        btn_star.setOnClickListener{
            inList = !inList
            if(inList){
                btn_star.setImageResource(R.drawable.ic_removeword)
            } else {
                btn_star.setImageResource(R.drawable.ic_addword)
            }
        }

        //parsing from hashmap in DatabaseHelper.kt
        val id = itemData["id"]
        val kanji = itemData["kanji"]
        val kana = itemData["kana"]
        val english = itemData["english"]
        val pos = itemData["pos"]
        val notes = itemData["notes"]

        view_kanji.text = kanji
        view_kana.text = kana
        view_english.text = english
        view_pos.text = pos
        view_edit_notes.setText(notes)

        //initiate recyclerview and set parameters
        rv_examples.setHasFixedSize(true)
        rv_examples.setLayoutManager(LinearLayoutManager(this))


        view_edit_notes.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                btn_save_notes.visibility = View.VISIBLE
            }
        }

        btn_save_notes.setOnClickListener {
            btn_save_notes.visibility = View.GONE
            view_edit_notes.clearFocus()
            val notesText = view_edit_notes.text.toString()
            myDB.editNotes(id!!, notesText)
        }

        // search word and add matching examples to recycler
        if (kanji != null) {
            readExamples(kanji)
        } else {
            if (kana != null) readExamples(kana)
        }

    }

    override fun onPause(){
        super.onPause()

        if (!inList) {
            myDB.deleteData(itemId)
        }

        //go to MyList
        finish()
        startActivity(Intent(this@ViewWord, MyList::class.java))

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