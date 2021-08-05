package com.lewiswilson.kiminojisho

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lewiswilson.kiminojisho.mylists.MyList
import kotlinx.android.synthetic.main.add_word.*
import kotlinx.android.synthetic.main.add_word.view_english
import kotlinx.android.synthetic.main.add_word.view_kana
import kotlinx.android.synthetic.main.add_word.view_edit_notes
import kotlinx.android.synthetic.main.view_word.*

class AddWord : AppCompatActivity() {
    private var myDB: DatabaseHelper? = null

    /* access modifiers changed from: protected */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.Nature, true)
        setContentView(R.layout.add_word)
        myDB = DatabaseHelper(this)

        val add = View.OnClickListener { v: View? ->
            val list = 0
            val newEntryKanji = edit_word.text.toString()
            val newEntryKana = view_kana.text.toString()
            val newEntryMeaning = view_english.text.toString()
            val pos = ""
            val newEntryNotes =  view_edit_notes.text.toString()

            if (newEntryKanji.isEmpty() || newEntryKana.isEmpty() || newEntryMeaning.isEmpty()) {
                Toast.makeText(this@AddWord, "Fill in Required Fields!", Toast.LENGTH_SHORT).show()
            } else {
                addData(list, newEntryKanji, newEntryKana, newEntryMeaning, pos, newEntryNotes)
                val addWord = this@AddWord
                addWord.startActivity(Intent(addWord, MyList::class.java))
                finish()
                MyList.ma!!.finish()
            }

        }
        btn_add.setOnClickListener(add)
    }

    private fun addData(list: Int, kanji: String, kana: String, english: String, pos: String, notes: String) {
        if (myDB!!.addData(list, kanji, kana, english, pos, notes)) {
            Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "This word is already in your list!", Toast.LENGTH_SHORT).show()
        }
    }
}