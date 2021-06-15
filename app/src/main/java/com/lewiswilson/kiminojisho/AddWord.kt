package com.lewiswilson.kiminojisho

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.add_word.*

class AddWord : AppCompatActivity() {
    private var myDB: DatabaseHelper? = null
    private var ToggleKanji = true

    /* access modifiers changed from: protected */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_word)
        myDB = DatabaseHelper(this)

        val toggle = View.OnClickListener { v: View? ->
            if (ToggleKanji) {
                ToggleKanji = false
                txt_word.setText(R.string.Kana)
                txt_kana.visibility = View.GONE
                edit_kana.visibility = View.GONE
            } else {
                ToggleKanji = true
                txt_word.setText(R.string.Kanji)
                txt_kana.visibility = View.VISIBLE
                edit_kana.visibility = View.VISIBLE
            }
        }
        btn_togglekanji.setOnClickListener(toggle)
        val add = View.OnClickListener { v: View? ->
            val newEntryWord = RemoveSemicolon(edit_word.text.toString().trim { it <= ' ' })
            val newEntryKana = RemoveSemicolon(edit_kana.text.toString().trim { it <= ' ' })
            val newEntryMeaning = RemoveSemicolon(edit_meaning.text.toString().trim { it <= ' ' })
            val newEntryExample = RemoveSemicolon(edit_example.text.toString().trim { it <= ' ' })
            val newEntryNotes = "[ " + RemoveSemicolon(edit_notes.text.toString().trim { it <= ' ' }) + " ]"
            if (ToggleKanji) {
                if (newEntryWord.length == 0 || newEntryKana.length == 0 || newEntryMeaning.length == 0) {
                    Toast.makeText(this@AddWord, "Fill in Required Fields!", Toast.LENGTH_SHORT).show()
                } else {
                    AddData(newEntryWord, newEntryKana, newEntryMeaning, newEntryExample, newEntryNotes)
                    val addWord = this@AddWord
                    addWord.startActivity(Intent(addWord, MainActivity::class.java))
                    finish()
                    MainActivity.ma!!.finish()
                }
            } else {
                if (newEntryWord.length == 0 || newEntryMeaning.length == 0) {
                    Toast.makeText(this@AddWord, "Fill in Required Fields!", Toast.LENGTH_SHORT).show()
                } else {
                    AddData(newEntryWord, newEntryWord, newEntryMeaning, newEntryExample, newEntryNotes)
                    val addWord = this@AddWord
                    addWord.startActivity(Intent(addWord, MainActivity::class.java))
                    finish()
                    MainActivity.ma!!.finish()
                }
            }

        }
        btn_add.setOnClickListener(add)
    }

    private fun AddData(word: String, kana: String, meaning: String, example: String, notes: String) {
        if (myDB!!.addData(word, kana, meaning, example, notes)) {
            Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "This word is already in your list!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun RemoveSemicolon(input: String): String {
        return if (input.contains(";")) {
            input.replace(";", ",")
        } else {
            input
        }
    }
}