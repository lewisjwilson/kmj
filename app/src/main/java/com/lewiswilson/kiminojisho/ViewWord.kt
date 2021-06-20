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
        val list_selection: String = MainActivity.list_index.toString()
        val unparsed_data = myDB.readData(list_selection)

        Log.d("ViewWord.kt, unparsed data: ", unparsed_data)

        //parsing data
        val parsed_word = unparsed_data.split(";").toTypedArray()[0]
        val parsed_kana = unparsed_data.split(";").toTypedArray()[1]
        val parsed_meaning = unparsed_data.split(";").toTypedArray()[2]

        //try/catch in case example column is empty...(no array index of 3 after split...)
        val parsed_example: String
        parsed_example = try {
            unparsed_data.split(";").toTypedArray()[3]
        } catch (e: Exception) {
            ""
        }
        val parsed_notes: String
        parsed_notes = try {
            unparsed_data.split(";").toTypedArray()[4]
        } catch (e: Exception) {
            ""
        }

        view_kanji.text = parsed_word
        view_kana.text = parsed_kana
        view_english.text = parsed_meaning
        view_examples.text = parsed_example
        view_notes.text = parsed_notes

        btn_delete.setOnClickListener { v: View? ->
            myDB.deleteData(list_selection)
            Toast.makeText(this@ViewWord, "Entry Deleted", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@ViewWord, MainActivity::class.java))
            finish()
            MainActivity.ma!!.finish()
        }

        flbtn_rand.setOnClickListener { v: View? ->
            MainActivity.list_index = myDB.random(0)
            finish()
            startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), 0)
            overridePendingTransition(0, 0)
        }
    }
}