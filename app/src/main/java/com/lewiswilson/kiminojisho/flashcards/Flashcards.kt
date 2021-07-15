package com.lewiswilson.kiminojisho.flashcards

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.lewiswilson.kiminojisho.DatabaseHelper
import com.lewiswilson.kiminojisho.MyListItem
import com.lewiswilson.kiminojisho.R
import kotlinx.android.synthetic.main.flashcards_home.*
import kotlinx.android.synthetic.main.flashcard_back.*
import kotlinx.android.synthetic.main.flashcard_front.*
import kotlinx.android.synthetic.main.flashcards.*
import kotlinx.android.synthetic.main.flashcards_complete.*
import java.util.ArrayList

class Flashcards : AppCompatActivity() {

    private var myDB: DatabaseHelper? = null
    var flashcardList: ArrayList<MyListItem>? = null
    var seen: ArrayList<Int>? = ArrayList()
    var completeReviews: Int = 0
    private var totalReviews: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flashcards)
        myDB = DatabaseHelper(this)

        flashcardList = myDB!!.dueFlashcards()
        totalReviews = flashcardList?.size!!

        txt_noreviews.text = "$completeReviews/$totalReviews"

        flashcardSort()

        btn_correct.setOnClickListener { redirect(true) }
        btn_wrong.setOnClickListener{ redirect(false) }

    }

    private fun redirect(correct: Boolean) {

        btn_correct.isEnabled = false
        btn_wrong.isEnabled = false

        val dbid = flashcardList!!.first().id
        var color = getColor(R.color.flashcard_correct)

        if(!correct) {
            color = getColor(R.color.flashcard_wrong)

            //if the seen arraylist contains the current word
            if( seen?.isNotEmpty() == true && seen?.contains(flashcardList?.first()?.id) == true ) {

                Log.d(TAG, "${flashcardList?.first()?.kanji} wrong, seen")
                //database id, correct = false, seen = true
                myDB?.updateFlashcard(dbid, correct, false)

            } else {
                seen?.add(dbid)
                Log.d(TAG, "${flashcardList?.first()?.kanji} wrong, NOT seen")
                myDB?.updateFlashcard(dbid, correct, true)
            }

        } else {
            //if the seen arraylist contains current word
            if( seen?.isNotEmpty() == true && seen?.contains(flashcardList?.first()?.id) == true ) {
                Log.d(TAG, "${flashcardList?.first()?.kanji} correct, seen")
                myDB?.updateFlashcard(dbid, correct, true)
            } else {
                Log.d(TAG, "${flashcardList?.first()?.kanji} correct, NOT seen")
                myDB?.updateFlashcard(dbid, correct, false)
            }

            flashcardList?.removeAt(0)
            completeReviews++
            txt_noreviews.text = "$completeReviews/$totalReviews"
        }

        cv_back.setCardBackgroundColor(color)
        cv_front.setCardBackgroundColor(color)


        //flip view back to front
        Handler(Looper.getMainLooper()).postDelayed({
            if (flipview.isBackSide) {
                flipview.flipTheView(true)
            }
        }, 1000)

        //delay running by extra 200ms so that answer doesnt show on flip back
        Handler(Looper.getMainLooper()).postDelayed({
            cv_back.setCardBackgroundColor(getColor(R.color.white))
            cv_front.setCardBackgroundColor(getColor(R.color.white))
            if(flashcardList?.isEmpty() == true) {
                //finished
                finish()
                startActivity(Intent(this@Flashcards, FlashcardsComplete::class.java))
            } else {
                flashcardSort()
            }
            btn_correct.isEnabled = true
            btn_wrong.isEnabled = true
        }, 1200)
    }

    private fun flashcardSort() {

        //randomise the order of the reviews
        flashcardList?.shuffle()

        fc_japanese.text = flashcardList?.first()?.kanji
        fc_english.text = flashcardList?.first()?.english
        fc_kana.text = flashcardList?.first()?.kana
    }

}