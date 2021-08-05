package com.lewiswilson.kiminojisho.mylists

import android.app.*
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.lewiswilson.kiminojisho.R
import kotlinx.android.synthetic.main.list_selection.*
import kotlinx.android.synthetic.main.my_list_item.view.*
import kotlinx.android.synthetic.main.search_page.*
import java.util.*


class ListSelection : AppCompatActivity(),
    ListSelectionAdapter.OnItemClickListener {
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

    }

    //populate recyclerview with data
    private fun populateRV() {
        val lists = arrayOf("examplelist1", "examplelist2")

        for (list in lists) {
            listOfLists!!.add(ListSelectionItem(list))
            rvAdapter = listOfLists?.let { it -> ListSelectionAdapter(this@ListSelection, it, this) }
            rv_list_selection.adapter = rvAdapter
        }

    }

    override fun onItemClick(itemId: Int) {
        //TODO("Not yet implemented")
    }

}