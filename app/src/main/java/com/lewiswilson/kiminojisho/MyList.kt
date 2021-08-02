package com.lewiswilson.kiminojisho

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lewiswilson.kiminojisho.searchAndViewWords.SearchPage
import com.lewiswilson.kiminojisho.searchAndViewWords.ViewWord
import kotlinx.android.synthetic.main.my_list.*
import kotlinx.android.synthetic.main.my_list_item.view.*
import kotlinx.android.synthetic.main.search_page.*
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import java.util.*


class MyList : AppCompatActivity(),
    MyListAdapter.OnItemClickListener {
    private val prefsName = "MyPrefs"
    private var myDB: DatabaseHelper? = null
    private var jishoList: ArrayList<MyListItem>? = ArrayList()
    private var searchList: ArrayList<MyListItem>? = ArrayList()
    private var rvAdapter: MyListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.Nature, true)
        setContentView(R.layout.my_list)
        myDB = DatabaseHelper(this)
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        //Check if it is a first time launch
        if (prefs.getBoolean("first_launch", true)) {
            firstLaunch()
            prefs.edit().putBoolean("first_launch", false).apply()
            prefs.edit().putBoolean("notifications_on", false).apply()
            prefs.edit().putString("sortby_col", "english").apply()
        }

        val flbtnAdd = findViewById<FloatingActionButton>(R.id.flbtn_add)


        //initiate recyclerview and set parameters
        rv_mylist.setHasFixedSize(true)
        rv_mylist.layoutManager = LinearLayoutManager(this)

        populateRV()
        //displaylist shows when searching in searchview
        searchList!!.addAll(jishoList!!)

        flbtnAdd.setOnClickListener { v: View? ->
            finish()
            startActivity(Intent(this@MyList, SearchPage::class.java)) }

    }

    //populate recyclerview with data
    private fun populateRV() {
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val column = prefs.getString("sortby_col", "english")
        val data = myDB!!.listContents(column!!)

        //Checks if database is empty and lists entries if not
        while (data.moveToNext()) {

            //ListView Data Layout
            jishoList!!.add(
                MyListItem(data.getInt(0), //id
                    data.getString(1), //kanji
                    data.getString(2), //kana
                    data.getString(3).split("@@@")[0], //english
                    data.getString(4), //pos
                    data.getString(5), //notes
                    false // unimportant here
                )
            )

            rvAdapter = jishoList?.let { it -> MyListAdapter(this@MyList, it, this) }
            rv_mylist.adapter = rvAdapter

        }

    }

    // recyclerview item click
    override fun onItemClick(itemId: Int) {
        clickedItemId = itemId
        finish()
        startActivity(Intent(this@MyList, ViewWord::class.java))
    }

    fun clearData() {
        jishoList!!.clear() // clear list
        rvAdapter!!.notifyDataSetChanged() // let your adapter know about the changes and reload view.
        populateRV()
    }

    // Menu icons are inflated just as they were with actionbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.mylist_menu, menu)

        //find search menuitem
        val menuItem = menu.findItem(R.id.searchView)
        //Initialize searchview
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchtext: String): Boolean {
                return false
            }

            override fun onQueryTextChange(searchtext: String?): Boolean {
                // populate the recyclerview on search query change
                if(searchtext!!.isNotEmpty()){
                    jishoList!!.clear()
                    val search = searchtext.toLowerCase(Locale.getDefault())
                    searchList!!.forEach {
                        if(it.english.toLowerCase(Locale.getDefault()).contains(search) or
                            it.kana.toLowerCase(Locale.getDefault()).contains(search) or
                            it.kanji.toLowerCase(Locale.getDefault()).contains(search)){

                            jishoList!!.add(it)
                        }
                    }

                    rvAdapter!!.notifyDataSetChanged()

                } else {
                    clearData()
                }

                return false
            }
        })
        return true
    }

    //Toolbar Menu Option Activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        return when (item.itemId) {
            R.id.english -> {
                prefs.edit().putString("sortby_col", "english").apply()
                prefs.edit().putString("sort_style", "ASC").apply()
                clearData()
                true
            }

            R.id.kana -> {
                prefs.edit().putString("sortby_col", "kana").apply()
                prefs.edit().putString("sort_style", "ASC").apply()
                clearData()
                true
            }

            R.id.action_help -> {
                firstLaunch()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val REQUEST_CODE = 10
        @JvmField
        var clickedItemId //use item_id to get and display database data
                : Int? = null
        @JvmField
        var fileUri: Uri? = null
        @JvmField
        var ma: AppCompatActivity? = null
    }

    override fun onResume() {
        super.onResume()
        clearData()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, HomeScreen::class.java))
    }

    private fun firstLaunch() {
        val color = "#DD008577"
        val fscv1 = FancyShowCaseView.Builder(this)
            .title("Welcome to KimiNoJisho, the custom Japanese dictionary app! This tutorial will help to get you started.")
            .backgroundColor(Color.parseColor(color))
            .titleStyle(R.style.HelpScreenTitle, Gravity.TOP or Gravity.CENTER)
            .build()
        val fscv2 = FancyShowCaseView.Builder(this)
            .title("This is the main screen. This shows you dictionary entries.")
            .backgroundColor(Color.parseColor(color))
            .titleStyle(R.style.HelpScreenTitle, Gravity.CENTER)
            .build()
        val fscv3 = FancyShowCaseView.Builder(this)
            .title("To create your first dictionary entry, use this button.")
            .focusOn(findViewById(R.id.flbtn_add))
            .backgroundColor(Color.parseColor(color))
            .titleStyle(R.style.HelpScreenTitle, Gravity.CENTER)
            .build()
        val fscvQueue = FancyShowCaseQueue()
            .add(fscv1)
            .add(fscv2)
            .add(fscv3)
        fscvQueue.show()
    }

}