package com.lewiswilson.kiminojisho.mylists

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.lewiswilson.kiminojisho.HomeScreen
import com.lewiswilson.kiminojisho.R
import kotlinx.android.synthetic.main.list_selection.*
import kotlinx.android.synthetic.main.my_list_item.view.*
import kotlinx.android.synthetic.main.search_page.*
import java.util.*


class ListSelection : AppCompatActivity() {
    private var listOfLists: ArrayList<ListSelectionItem>? = ArrayList()
    private var rvAdapter: ListSelectionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.Nature, true)
        setContentView(R.layout.list_selection)

        //initiate recyclerview and set parameters
        rv_list_selection.setHasFixedSize(true)
        rv_list_selection.layoutManager = LinearLayoutManager(this)

        populateRV()

        rv_list_selection.adapter = rvAdapter

        rvAdapter?.setOnItemClickListener(object: ListSelectionAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(applicationContext, MyList::class.java)
                intent.putExtra("adapterPos", position)
                startActivity(intent)
            }
        }
        )

        btn_newlist.setOnClickListener { createList() }

    }

    private fun createList() {
        TODO("Not yet implemented")
    }

    //populate recyclerview with data
    private fun populateRV() {

        val listsArr = resources.getStringArray(R.array.my_lists)
        for (list in listsArr) {
            listOfLists!!.add(ListSelectionItem(list))
            rvAdapter = listOfLists?.let { it -> ListSelectionAdapter(this@ListSelection, it) }
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(Intent(this@ListSelection, HomeScreen::class.java))
    }

}