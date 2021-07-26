package com.lewiswilson.kiminojisho

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.add_word.*
import kotlinx.android.synthetic.main.add_word.view_english
import kotlinx.android.synthetic.main.add_word.view_examples
import kotlinx.android.synthetic.main.add_word.view_kana
import kotlinx.android.synthetic.main.add_word.view_notes
import kotlinx.android.synthetic.main.view_word.*

class AddWord : AppCompatActivity() {
    private var myDB: DatabaseHelper? = null

    /* access modifiers changed from: protected */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.Turquoise, true)
        setContentView(R.layout.add_word)
        myDB = DatabaseHelper(this)

        val add = View.OnClickListener { v: View? ->
            val newEntryWord = RemoveSemicolon(edit_word.text.toString().trim { it <= ' ' })
            val newEntryKana = RemoveSemicolon(view_kana.text.toString().trim { it <= ' ' })
            val newEntryMeaning = RemoveSemicolon(view_english.text.toString().trim { it <= ' ' })
            val newEntryExample = RemoveSemicolon(view_examples.text.toString().trim { it <= ' ' })
            val newEntryNotes =  RemoveSemicolon(view_notes.text.toString().trim { it <= ' ' })

            if (newEntryWord.length == 0 || newEntryKana.length == 0 || newEntryMeaning.length == 0) {
                Toast.makeText(this@AddWord, "Fill in Required Fields!", Toast.LENGTH_SHORT).show()
            } else {
                AddData(newEntryWord, newEntryKana, newEntryMeaning, newEntryExample, newEntryNotes)
                val addWord = this@AddWord
                addWord.startActivity(Intent(addWord, MyList::class.java))
                finish()
                MyList.ma!!.finish()
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