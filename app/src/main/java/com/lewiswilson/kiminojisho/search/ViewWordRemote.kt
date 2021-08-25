package com.lewiswilson.kiminojisho.search

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.lewiswilson.kiminojisho.*
import com.lewiswilson.kiminojisho.databinding.MyListBinding
import com.lewiswilson.kiminojisho.databinding.ViewWordBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList

class ViewWordRemote : AppCompatActivity() {

    private lateinit var viewWordBind: ViewWordBinding

    private var mItemList: ArrayList<ViewWordItem>? = ArrayList()
    private var mItemAdapter: ViewWordItemAdapter? = null
    private var starred: Boolean = false
    private var examplesList: ArrayList<ExamplesItem>? = ArrayList()
    private var rvAdapter: ExamplesAdapter? = null

    private var kanji = ""
    private var kana = ""
    private var english = ""
    private var pos = ""
    private var notes = ""

    private val myDB = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewWordBind = ViewWordBinding.inflate(layoutInflater)
        setContentView(viewWordBind.root)

        //implementing ads
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        viewWordBind.vwAdView.loadAd(adRequest)

        val adapterPos = intent.getIntExtra("adapterPos", 0)
        Log.d(TAG, "position: $adapterPos")
        starred = intent.getBooleanExtra("star_filled", false)

        if(!starred) {
            viewWordBind.vwBtnStar.setImageResource(R.drawable.ic_addword)
        }

        kanji = SearchPage.dataItems!![adapterPos].kanji
        kana = SearchPage.dataItems!![adapterPos].kana
        english = SearchPage.dataItems!![adapterPos].english
        pos = SearchPage.dataItems!![adapterPos].pos
        notes = SearchPage.dataItems!![adapterPos].notes.toString()

        viewWordBind.vwKanji.text = kanji
        viewWordBind.vwKana.text = kana

        val defArray = english.split("@@@").toTypedArray()
        val posArray = pos.split("@@@").toTypedArray()
        val notesArray = notes.split("@@@").toTypedArray()

        var defCount = 1
        for (i in defArray.indices) {
            // items to view in searchpage activity
            val engText = "$defCount. ${defArray[i]}"
            mItemList!!.add(ViewWordItem("not_required", kanji, kana, engText, posArray[i], notesArray[i], starred))
            defCount++
        }

        // initialte word_recycler
        viewWordBind.vwRvDefinitions.setHasFixedSize(true)
        viewWordBind.vwRvDefinitions.setLayoutManager(LinearLayoutManager(this))


        mItemAdapter = mItemList?.let { it ->
            ViewWordItemAdapter(this@ViewWordRemote, it)
        }
        viewWordBind.vwRvDefinitions.adapter = mItemAdapter
        mItemAdapter?.notifyDataSetChanged()


        viewWordBind.vwBtnStar.setOnClickListener{
            if (starred) {
                warningDialog(kanji, english)
            } else {
               listSelectDialog()
            }
        }


        //initiate recyclerview and set parameters
        viewWordBind.vwRvExamples.setHasFixedSize(true)
        viewWordBind.vwRvExamples.setLayoutManager(LinearLayoutManager(this))

        // search word and add matching examples to recycler
        readExamples(kanji)
    }

    fun readExamples(filter: String) {

        val minput = InputStreamReader(assets.open("examples.tsv"))
        val reader = BufferedReader(minput)

        var line : String?

        val examplesNo = 5
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
        viewWordBind.vwRvExamples.adapter = rvAdapter

    }

    private fun warningDialog(kanji: String, english: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete entry")
            .setMessage("Removing this word from your list will also reset any flashcard progress for this word. Are you sure you want to remove the item?")
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(
                getString(R.string.Delete_Entry)
            ) { _, _ ->
                try {
                    Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT)
                        .show()
                    myDB.deleteFromRemote(kanji, english)
                    viewWordBind.vwBtnStar.setImageResource(R.drawable.ic_addword)
                    starred = !starred
                    SearchPage.starFilled = false
                } catch (e: NullPointerException) {
                    Log.d(TAG, "Could not delete item from list (normal): ${e.printStackTrace()}")
                }
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(getString(R.string.Cancel)) { _, _ ->
            }
            .setIcon(getDrawable(R.drawable.ic_info))
            .show()
    }

    private fun listSelectDialog() {

        val listArray = myDB.getLists().toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select List to Add To")
            .setItems(listArray) { _, which ->
                val selected = listArray[which]
                val listId = myDB.getListIdFromName(selected)
                viewWordBind.vwBtnStar.setImageResource(R.drawable.ic_removeword)
                myDB.addData(listId, kanji, kana, english, pos, notes)
                starred = !starred
                SearchPage.starFilled = true
                Toast.makeText(this, "Added to list: $selected", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
