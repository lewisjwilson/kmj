package com.lewiswilson.kiminojisho.search

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.lewiswilson.kiminojisho.*
import com.lewiswilson.kiminojisho.databinding.MyListBinding
import com.lewiswilson.kiminojisho.databinding.ViewWordBinding
import com.lewiswilson.kiminojisho.mylists.MyList
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.util.*
import java.util.ArrayList
import kotlin.collections.*


class ViewWord : AppCompatActivity() {

    private lateinit var viewWordBind: ViewWordBinding

    private var mItemList: ArrayList<ViewWordItem>? = ArrayList()
    private var mItemAdapter: ViewWordItemAdapter? = null
    private var examplesList: ArrayList<ExamplesItem>? = ArrayList()
    private var rvAdapter: ExamplesAdapter? = null
    private val myDB = DatabaseHelper(this)
    private var starred = true

    var kanji = ""
    var kana = ""
    var english = ""
    var pos = ""
    var notes = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewWordBind = ViewWordBinding.inflate(layoutInflater)
        setContentView(viewWordBind.root)

        //implementing ads
        MobileAds.initialize(this)
        val testDeviceIds = Arrays.asList("3EE9A91017FEA4E8E9F0996A4775B406")
        val config = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(config)
        val adRequest = AdRequest.Builder().build()
        viewWordBind.vwAdView.loadAd(adRequest)

        val itemId = MyList.clickedItemId.toString()
        val itemData = myDB.getData(Integer.parseInt(itemId))

        Log.d(TAG, "ITEM: $itemId")

        //parsing from hashmap in DatabaseHelper.kt
        kanji = itemData["kanji"].toString()
        kana = itemData["kana"].toString()
        english = itemData["english"].toString()
        pos = itemData["pos"].toString()
        notes = itemData["notes"].toString()

        viewWordBind.vwKanji.text = kanji
        viewWordBind.vwKana.text = kana

        // initialte word_recycler
        viewWordBind.vwRvDefinitions.setHasFixedSize(true)
        viewWordBind.vwRvDefinitions.setLayoutManager(LinearLayoutManager(this))

        val defArray = english.split("@@@").toTypedArray()
        val posArray = pos.split("@@@").toTypedArray()
        val notesArray = notes.split("@@@").toTypedArray()

        var defCount = 1
        for (i in defArray.indices) {
            // items to view in searchpage activity
            val engText = "$defCount. ${defArray[i]}"
            mItemList!!.add(
                ViewWordItem("not_required", kanji, kana, engText, posArray[i], notesArray[i], starred)
            )
            defCount++
        }

        mItemAdapter = mItemList?.let { it ->
            ViewWordItemAdapter(this@ViewWord, it)
        }
        viewWordBind.vwRvDefinitions.adapter = mItemAdapter
        mItemAdapter?.notifyDataSetChanged()

        viewWordBind.vwBtnStar.setOnClickListener {
            if (starred) {
                warningDialog(itemId)
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
        if (count == 0) {
            viewWordBind.vwTxtExamples.visibility = View.GONE
            viewWordBind.vwRvExamples.visibility = View.GONE
        } else {
            rvAdapter = examplesList?.let { it -> ExamplesAdapter(this@ViewWord, it) }
            viewWordBind.vwRvExamples.adapter = rvAdapter
        }

    }

    private fun warningDialog(itemId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete entry")
            .setMessage("Removing this word from your list will also reset any flashcard progress for this word. Are you sure you want to remove the item?")
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(
                getString(R.string.Delete_Entry)
            ) { _, _ ->
                try {
                    myDB.deleteData(itemId)
                    MyList.deleted = true
                    finish()
                    Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.d(TAG, "Could not delete item from list: ${e.printStackTrace()}")
                }
                starred = !starred
            } // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(getString(R.string.Cancel)) { _, _ ->
            }
            .setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_info))
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
                Toast.makeText(this, "Added to list: $selected", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

}


