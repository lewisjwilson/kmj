package com.lewiswilson.kiminojisho.search

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.lewiswilson.kiminojisho.*
import kotlinx.android.synthetic.main.my_list.*
import kotlinx.android.synthetic.main.view_word.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.util.*
import java.util.ArrayList
import kotlin.collections.*


class ViewWord : AppCompatActivity() {

    private var mItemList: ArrayList<ViewWordItem>? = ArrayList()
    private var mItemAdapter: ViewWordItemAdapter? = null
    private var examplesList: ArrayList<ExamplesItem>? = ArrayList()
    private var rvAdapter: ExamplesAdapter? = null
    private val myDB = DatabaseHelper(this)
    private var starred = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.Nature, true)
        setContentView(R.layout.view_word)

        //implementing ads
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        vw_adView.loadAd(adRequest)

        val itemId = MyList.clickedItemId.toString()
        val itemData = myDB.getData(Integer.parseInt(itemId))

        Log.d(TAG, "ITEM: $itemId")

        //parsing from hashmap in DatabaseHelper.kt
        val kanji = itemData["kanji"]
        val kana = itemData["kana"]
        val english = itemData["english"]
        val pos = itemData["pos"]
        val notes = itemData["notes"]

        vw_kanji.text = kanji
        vw_kana.text = kana

        // initialte word_recycler
        vw_rv_definitions.setHasFixedSize(true)
        vw_rv_definitions.setLayoutManager(LinearLayoutManager(this))

        val defArray = english!!.split("@@@").toTypedArray()
        val posArray = pos?.split("@@@")?.toTypedArray()
        val notesArray = notes?.split("@@@")?.toTypedArray()

        var defCount = 1
        for (i in defArray.indices) {
            // items to view in searchpage activity
            val engText = "$defCount. ${defArray[i]}"
            mItemList!!.add(
                ViewWordItem(
                    "not_required", kanji!!, kana!!, engText,
                    posArray?.get(i).toString(), notesArray?.get(i).toString(), starred
                )
            )
            defCount++
        }

        mItemAdapter = mItemList?.let { it ->
            ViewWordItemAdapter(this@ViewWord, it)
        }
        vw_rv_definitions.adapter = mItemAdapter
        mItemAdapter?.notifyDataSetChanged()

        vw_btn_star.setOnClickListener {
            starred = !starred
            if (!starred) {
                warningDialog(itemId)
            } else {
                Toast.makeText(this, "Added to My List", Toast.LENGTH_SHORT).show()
                vw_btn_star.setImageResource(R.drawable.ic_removeword)
                myDB.addData(0, kanji!!, kana, english, pos, notes)
            }
        }

        //initiate recyclerview and set parameters
        vw_rv_examples.setHasFixedSize(true)
        vw_rv_examples.setLayoutManager(LinearLayoutManager(this))

        // search word and add matching examples to recycler
        readExamples(kanji!!)

    }

    private fun readExamples(filter: String) {

        val minput = InputStreamReader(assets.open("examples.tsv"))
        val reader = BufferedReader(minput)

        var line: String?

        val examplesNo = 5
        var count = 0

        while (reader.readLine().also { line = it } != null) {
            if (reader.readLine().also { line = it }.contains(filter)) {
                val japanese = line!!.split("\t")[0]
                val english = line!!.split("\t")[1]
                examplesList?.add(ExamplesItem(japanese, english))
                count++
            }
            if (count >= examplesNo) {
                break
            }

        }
        rvAdapter = examplesList?.let { it -> ExamplesAdapter(this@ViewWord, it) }
        vw_rv_examples.adapter = rvAdapter

    }

    private fun warningDialog(itemId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete entry")
            .setMessage("Removing this word from your list will also reset any flashcard progress for this word. Are you sure you want to remove the item?")
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(
                getString(R.string.Delete_Entry)
            ) { _, _ ->
                vw_btn_star.setImageResource(R.drawable.ic_addword)
                try {
                    myDB.deleteData(itemId)
                    Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.d(TAG, "Could not delete item from list: ${e.printStackTrace()}")
                }
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(getString(R.string.Cancel)) { _, _ ->
                starred = !starred //if cancelled, the starred status reverts
            }
            .setIcon(getDrawable(R.drawable.ic_info))
            .show()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        //go to MyList
        finish()
        startActivity(Intent(this@ViewWord, MyList::class.java))

    }
}


