package com.lewiswilson.kiminojisho.flashcards

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.lewiswilson.kiminojisho.DatabaseHelper
import com.lewiswilson.kiminojisho.HomeScreen
import com.lewiswilson.kiminojisho.R
import com.lewiswilson.kiminojisho.mylists.ListSelectionAdapter
import com.lewiswilson.kiminojisho.mylists.ListSelectionItem
import com.lewiswilson.kiminojisho.mylists.MyList
import kotlinx.android.synthetic.main.flashcards_home.*
import kotlinx.android.synthetic.main.list_selection.*
import java.util.ArrayList

class FlashcardsHome : AppCompatActivity() {
    private var listOfLists: ArrayList<FCListItem>? = ArrayList()
    private var rvAdapter: FCListAdapter? = null
    private var myDB: DatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flashcards_home)
        myDB = DatabaseHelper(this)

        val reviewsDue = myDB?.flashcardCount(0)
        val noOfFlashcards = "Total Reviews: $reviewsDue"
        fc_no.text = noOfFlashcards


        btn_start.setOnClickListener { v: View? ->
            if (myDB!!.itemCount(0) < 4) {
                Toast.makeText(this, "Not enough words to start reviewing! Add some new words to get started!", Toast.LENGTH_LONG).show()
            } else {
                finish()
                startActivity(Intent(this@FlashcardsHome, Flashcards::class.java))
            }
        }

        //initiate recyclerview and set parameters
        rv_flashcards_home.setHasFixedSize(true)
        rv_flashcards_home.layoutManager = GridLayoutManager(this, 2)

        populateRV()

        rv_flashcards_home.adapter = rvAdapter

        rvAdapter?.setOnItemClickListener(object: FCListAdapter.OnItemClickListener{
            override fun onItemClick(listId: Int) {
                val intent = Intent(applicationContext, MyList::class.java)
                intent.putExtra("listID", listId)
                startActivity(intent)
            }

            override fun onButtonClick(listId: Int) {
                val intent = Intent(applicationContext, Flashcards::class.java)
                intent.putExtra("listID", listId)
                startActivity(intent)
            }
        }
        )
    }

    override fun onRestart() {
        super.onRestart()
        listOfLists?.clear()
        rvAdapter?.notifyDataSetChanged()
        populateRV()
    }

    //populate recyclerview with data
    private fun populateRV() {

        val listArray = myDB!!.getLists()

        for (list in listArray) {
            val listId = myDB?.getListIdFromName(list)
            val reviews = myDB?.flashcardCount(listId!!)
            listOfLists!!.add(FCListItem(list, reviews!!))
            rvAdapter = listOfLists?.let { it -> FCListAdapter(this@FlashcardsHome, it) }
        }
    }
}