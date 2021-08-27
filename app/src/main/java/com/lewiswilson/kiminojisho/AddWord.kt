package com.lewiswilson.kiminojisho

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.lewiswilson.kiminojisho.databinding.AddWordBinding
import com.lewiswilson.kiminojisho.databinding.MyListBinding
import com.lewiswilson.kiminojisho.mylists.ListSelection
import com.lewiswilson.kiminojisho.mylists.MyList

class AddWord : AppCompatActivity() {

    private lateinit var addWordBind: AddWordBinding

    private var myDB: DatabaseHelper? = null

    /* access modifiers changed from: protected */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addWordBind = AddWordBinding.inflate(layoutInflater)
        setContentView(addWordBind.root)
        myDB = DatabaseHelper(this)

        val listArray = myDB!!.getLists()

        addWordBind.spnAddwordLists.onItemSelectedListener
        val spnAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listArray)
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        addWordBind.spnAddwordLists.adapter = spnAdapter

        val add = View.OnClickListener { _: View? ->
            val newEntryList = myDB!!.getListIdFromName(addWordBind.spnAddwordLists.selectedItem.toString())
            val newEntryKanji = addWordBind.editWord.text.toString()
            val newEntryKana = addWordBind.txtKana.text.toString()
            val newEntryMeaning = addWordBind.viewEnglish.text.toString()
            val newEntryPos = addWordBind.txtAddwordPos.text.toString()
            val newEntryNotes =  addWordBind.viewEditNotes.text.toString()

            if (newEntryKanji.isEmpty() || newEntryKana.isEmpty() || newEntryMeaning.isEmpty()) {
                Toast.makeText(this@AddWord, "Fill in Required Fields!", Toast.LENGTH_SHORT).show()
            } else {
                addData(newEntryList, newEntryKanji, newEntryKana, newEntryMeaning, newEntryPos, newEntryNotes)
                val addWord = this@AddWord
                addWord.startActivity(Intent(addWord, ListSelection::class.java))
                finish()
                MyList.ma?.finish()
            }

        }
        addWordBind.btnAdd.setOnClickListener(add)

    }

    private fun addData(list: Int, kanji: String, kana: String, english: String, pos: String, notes: String) {
        if (myDB!!.addData(list, kanji, kana, english, pos, notes)) {
            Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "This word is already in your list!", Toast.LENGTH_SHORT).show()
        }
    }
}