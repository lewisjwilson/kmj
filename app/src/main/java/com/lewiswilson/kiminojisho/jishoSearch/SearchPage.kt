package com.lewiswilson.kiminojisho.jishoSearch

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.lewiswilson.kiminojisho.*
import com.lewiswilson.kiminojisho.json.Japanese
import com.lewiswilson.kiminojisho.json.JishoData
import com.lewiswilson.kiminojisho.json.RetrofitClient
import kotlinx.android.synthetic.main.search_data_item.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlinx.android.synthetic.main.search_page.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.NullPointerException
import kotlin.collections.ArrayList

class SearchPage : AppCompatActivity(), CoroutineScope {

    private var mSearchList: ArrayList<SearchDataItem>? = ArrayList()
    private var mSearchDataAdapter: SearchDataAdapter? = null
    private val myDB = DatabaseHelper(this)
    var queryTextChangedJob: Job? = null

    private val job = Job()
    override val coroutineContext = job + Dispatchers.Main

    override fun onDestroy() {
        queryTextChangedJob?.cancel()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.Nature, true)
        setContentView(R.layout.search_page)
        sp = this

        // setting autofocus on searchview when activity is started
        sv_searchfield.isIconifiedByDefault = false
        sv_searchfield.isFocusable = true
        sv_searchfield.requestFocusFromTouch()

        //initiate recyclerview and set parameters
        rv_searchdata.setHasFixedSize(true)
        rv_searchdata.setLayoutManager(LinearLayoutManager(this))

        btn_manual.setOnClickListener { startActivity(Intent(this@SearchPage, AddWord::class.java)) }

        //reload datafromnetwork on text input
        sv_searchfield.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchtext: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {

                try {
                    mSearchList?.let{
                        queryTextChangedJob?.cancel()
                        clearData()
                    }
                } catch (e: NullPointerException) {
                    Log.d(TAG, "onQueryTextChange (usual on first search): ${e.printStackTrace()}")
                }

                queryTextChangedJob = launch(Dispatchers.Main) {
                    delay(1000)
                    if (newText != null) {
                        dataFromNetwork(newText)
                    }
                }

                return true
            }
        })

    }

    //get API data
    private fun dataFromNetwork(query: String) {

        tv_info.visibility = View.INVISIBLE
        //if the search adapter has data in it already, clear the recyclerview
        rv_searchdata.adapter = mSearchDataAdapter
        if (mSearchDataAdapter != null) {
            clearData()
        }

        //if the searchtext contains any japanese...
        val call: Call<JishoData> = RetrofitClient.getInstance().myApi.getData(query)

        call.enqueue(object : Callback<JishoData> {
            override fun onResponse(call: Call<JishoData>, response: Response<JishoData>) {

                //At this point we got our word list
                val data = response.body()!!.data

                //if no data was found, try a call assuming romaji style
                if (data.isEmpty()) {
                    tv_info.visibility = View.VISIBLE
                    tv_info.text = getString(R.string.no_results)
                }

                //try to retrieve data from jishoAPI
                try {
                    var japanese: List<Japanese>
                    var kanji: String?
                    var kana: String?
                    var english: String
                    for (i in data.indices) {
                        japanese = data[i].japanese
                        kanji = japanese[0].word
                        kana = japanese[0].reading

                        //if the result has no associated kanji
                        if (kanji == null) {
                            kanji = kana
                        }

                        //get english definitions
                        val sense = data[i].senses
                        val noOfDefinitions = sense[0].englishDefinitions.size
                        english = sense[0].englishDefinitions[0]

                        //If there is more than one definition, also display the second definition
                        if (noOfDefinitions > 1) {
                            english = english + ", " + sense[0].englishDefinitions[1]
                        }

                        val posArray = sense[0].partsOfSpeech
                        var pos = ""
                        for (item in posArray) {
                            pos += "$item, "
                        }
                        pos = pos.replace(", $".toRegex(), "")

                        var notes = ""
                        if (sense[0].tags.size > 0){
                            notes = sense[0].tags[0]
                        }

                        val starFilled = myDB.checkStarred(kanji, english)

                        // items to view in searchpage activity
                        mSearchList!!.add(SearchDataItem(kanji, kana, english, starFilled))

                        // refer to this arraylist in viewwordremote (id is index of recycler)
                        dataItems!!.add(MyListItem(i, kanji, kana, english, pos, notes))

                        mSearchDataAdapter = mSearchList?.let { it ->
                            SearchDataAdapter(this@SearchPage, it)
                        }
                        rv_searchdata.adapter = mSearchDataAdapter
                    }

                } catch (e: Exception) {
                    Log.d("", "Data Retrieval Error: " + e.message)
                }
            }

            override fun onFailure(call: Call<JishoData>, t: Throwable) {
                //handle error or failure cases here
                Log.d("", "SearchPage (Error): " + t.message)
            }
        })
    }

    fun clearData() {
        dataItems!!.clear()
        mSearchList!!.clear() // clear list
        mSearchDataAdapter!!.notifyDataSetChanged() // let your adapter know about the changes and reload view.
    }

    companion object {
        var sp: AppCompatActivity? = null
        //items for carrying over to viewwordremote
        var dataItems: ArrayList<MyListItem>? = ArrayList()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(Intent(this, MyList::class.java))
    }
}

