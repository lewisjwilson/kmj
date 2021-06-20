package com.lewiswilson.kiminojisho

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.view_word.*

class ViewWord : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_word)

        val myDB = DatabaseHelper(this)
        val itemId: String = MainActivity.item_id.toString()
        val itemData = myDB.getData(Integer.parseInt(itemId))

        Log.d("ViewWord.kt, item_id: ", itemId)

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

        flbtn_rand.setOnClickListener { v: View? ->
            //MainActivity.list_index = myDB.random(0)
            finish()
            startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), 0)
            overridePendingTransition(0, 0)
        }
    }
}