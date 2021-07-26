package com.lewiswilson.kiminojisho.flashcards

import android.animation.ObjectAnimator
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
    private var completeReviews: Int = 0
    private var totalReviews: Int = 0
    private var totalCorrect: Int = 0
    private var totalTries: Int = 0
    private var correctBtn: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.Turquoise, true)
        setContentView(R.layout.flashcards)
        myDB = DatabaseHelper(this)

        flashcardList = myDB!!.dueFlashcards()
        totalReviews = flashcardList?.size!!
        progressBar.progress = 0

        flashcardSort()

        option1.setOnClickListener{ redirect(1) }
        option2.setOnClickListener{ redirect(2) }
        option3.setOnClickListener{ redirect(3) }
        option4.setOnClickListener{ redirect(4) }

    }

    private fun redirect(selectedBtn: Int) {
        var correct = false

        //if the button selected is the correct value, set the boolean to true
        when(selectedBtn){correctBtn -> correct = true}

        option1.isEnabled = false
        option2.isEnabled = false
        option3.isEnabled = false
        option4.isEnabled = false

        totalTries += 1

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
            totalCorrect += 1
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
            val progress = ((completeReviews/totalReviews.toDouble())*100).toInt()
            setProgressBar(progress)
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
                val percentCorrect = (totalCorrect.toDouble()/totalTries)*100
                finish()
                val intent = Intent(this@Flashcards, FlashcardsComplete::class.java)
                intent.putExtra("percent", percentCorrect)
                startActivity(intent)
            } else {
                flashcardSort()
            }
            option1.isEnabled = true
            option2.isEnabled = true
            option3.isEnabled = true
            option4.isEnabled = true
        }, 1200)
    }

    private fun flashcardSort() {

        //randomise the order of the reviews
        flashcardList?.shuffle()

        //populating flashcard
        fc_japanese.text = flashcardList?.first()?.kanji
        fc_english.text = flashcardList?.first()?.english
        fc_kana.text = flashcardList?.first()?.kana


        val validateArray = listOf(1, 0, 0, 0).shuffled()

        for (item in validateArray){
            if(item==1){
                correctBtn = validateArray.indexOf(item) + 1 //assigns the correct button
                Log.d(TAG, "correct item: $item , index: ${validateArray.indexOf(item) + 1}") //correct
            }
        }

        val incorrectItems = myDB?.randomThreeWrong(flashcardList?.first()?.kanji!!)

        var correctItemText: String
        var wrongItemText1: String
        var wrongItemText2: String
        var wrongItemText3: String

        // if entry has no kanji
        if((flashcardList?.first()?.kana).equals(flashcardList?.first()?.kanji)){
            correctItemText = "${flashcardList?.first()?.english}"
            wrongItemText1 = "${incorrectItems?.elementAt(0)?.english}"
            wrongItemText2 = "${incorrectItems?.elementAt(1)?.english}"
            wrongItemText3 = "${incorrectItems?.elementAt(2)?.english}"
        } else {
            correctItemText = "${flashcardList?.first()?.kana}\n${flashcardList?.first()?.english}"
            wrongItemText1 = "${incorrectItems?.elementAt(0)?.kana}\n${incorrectItems?.elementAt(0)?.english}"
            wrongItemText2 = "${incorrectItems?.elementAt(1)?.kana}\n${incorrectItems?.elementAt(1)?.english}"
            wrongItemText3 = "${incorrectItems?.elementAt(2)?.kana}\n${incorrectItems?.elementAt(2)?.english}"
        }


        Log.d(TAG, "wrongitemtext1: $wrongItemText1")
        Log.d(TAG, "wrongitemtext2: $wrongItemText2")
        Log.d(TAG, "wrongitemtext3: $wrongItemText3")


        when (correctBtn) {
            1 -> {
                txt_option1.text = correctItemText
                txt_option2.text = wrongItemText1
                txt_option3.text = wrongItemText2
                txt_option4.text = wrongItemText3
            }
            2 -> {
                txt_option1.text = wrongItemText1
                txt_option2.text = correctItemText
                txt_option3.text = wrongItemText2
                txt_option4.text = wrongItemText3
            }
            3 -> {
                txt_option1.text = wrongItemText1
                txt_option2.text = wrongItemText2
                txt_option3.text = correctItemText
                txt_option4.text = wrongItemText3
            }
            4 -> {
                txt_option1.text = wrongItemText1
                txt_option2.text = wrongItemText2
                txt_option3.text = wrongItemText3
                txt_option4.text = correctItemText
            }
        }

    }

    private fun setProgressBar(progress: Int) {
        ObjectAnimator.ofInt(progressBar, "progress", progress)
            .setDuration(300)
            .start()
    }

}