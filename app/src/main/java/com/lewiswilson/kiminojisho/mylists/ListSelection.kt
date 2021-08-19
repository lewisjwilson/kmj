package com.lewiswilson.kiminojisho.mylists

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.lewiswilson.kiminojisho.DatabaseHelper
import com.lewiswilson.kiminojisho.HomeScreen
import com.lewiswilson.kiminojisho.R
import kotlinx.android.synthetic.main.list_selection.*
import kotlinx.android.synthetic.main.list_selection_item.*
import kotlinx.android.synthetic.main.my_list_item.view.*
import kotlinx.android.synthetic.main.search_page.*
import java.util.*
import kotlin.collections.HashSet


class ListSelection : AppCompatActivity() {
    private var listOfLists: ArrayList<ListSelectionItem>? = ArrayList()
    private var rvAdapter: ListSelectionAdapter? = null
    private var myDB: DatabaseHelper? = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_selection)

        //initiate recyclerview and set parameters
        rv_list_selection.setHasFixedSize(true)
        rv_list_selection.layoutManager = GridLayoutManager(this, 2)

        populateRV()

        rv_list_selection.adapter = rvAdapter

        rvAdapter?.setOnItemClickListener(object: ListSelectionAdapter.OnItemClickListener{
            override fun onItemClick(listId: Int) {
                val intent = Intent(applicationContext, MyList::class.java)
                intent.putExtra("listID", listId)
                startActivity(intent)
            }
            }
        )

        btn_newlist.setOnClickListener { createList() }

    }

    private fun createList() {

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Create a new list")
        val input = EditText(this)
        input.setHint("list name")
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("Create") { _, _ ->
            if(input.text.toString().isNotEmpty()) {
                val listName = input.text.toString()
                myDB?.addList(listName)
                listOfLists!!.add(ListSelectionItem(listName))
                rvAdapter?.notifyItemInserted(listOfLists?.size!!)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    //populate recyclerview with data
    private fun populateRV() {

        val listArray = myDB!!.getLists()

        for (list in listArray) {
            listOfLists!!.add(ListSelectionItem(list))
            rvAdapter = listOfLists?.let { it -> ListSelectionAdapter(this@ListSelection, it) }
        }
    }

}