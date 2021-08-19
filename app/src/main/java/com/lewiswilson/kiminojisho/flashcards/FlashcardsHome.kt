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

        // itemcount < 4 prevents any issues regarding multiple choice in Flashcards.kt
        if(reviewsDue!! <= 0){
            btn_start.isEnabled = false
        }
        if(myDB!!.itemCount() < 4){
            btn_start.isEnabled = false
            txt_not_enough_items.visibility = View.VISIBLE
        }

        btn_start.setOnClickListener { v: View? ->
            finish()
            startActivity(Intent(this@FlashcardsHome, Flashcards::class.java)) }



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
        }
        )
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