package com.lewiswilson.kiminojisho.flashcards

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lewiswilson.kiminojisho.DatabaseHelper
import com.lewiswilson.kiminojisho.MyListItem
import com.lewiswilson.kiminojisho.R
import kotlinx.android.synthetic.main.flashcards.*
import java.util.ArrayList

class Flashcards : AppCompatActivity() {

    private var myDB: DatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flashcards)
        myDB = DatabaseHelper(this)

        var flashcardList: ArrayList<MyListItem>? = ArrayList()

        flashcardList = myDB!!.dueFlashcards()

        if (flashcardList != null) {
            for (fc in flashcardList) {
                textView.text = "${fc.kanji} ${fc.kana} ${fc.english}"
            }
        }

    }
}