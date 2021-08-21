package com.lewiswilson.kiminojisho.search

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
import com.lewiswilson.kiminojisho.mylists.MyListItem
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

    override fun onResume() {
        super.onResume()
        dataItems!!.clear()
        mSearchList!!.clear() // clear list

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    delay(250)
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
                val data = response.body()?.data

                //if no data was found, try a call assuming romaji style
                if (data?.isEmpty() == true) {
                    tv_info.visibility = View.VISIBLE
                    tv_info.text = getString(R.string.no_results)
                }

                //try to retrieve data from jishoAPI
                try {
                    var japanese: List<Japanese>
                    var kanji: String?
                    var kana: String?
                    var english: String?
                    for (i in data?.indices!!) {
                        japanese = data[i].japanese
                        kanji = japanese[0].word
                        kana = japanese[0].reading

                        //if the result has no associated kanji
                        if (kanji == null) {
                            kanji = kana
                        }

                        //get english definitions
                        val senses = data[i].senses

                        english = ""
                        var pos = ""
                        var notes = ""
                        for (item in senses) {
                            val currentDef = item.englishDefinitions.toString().replace("[", "").replace("]", "")
                            english += "$currentDef@@@"

                            val currentPos = item.partsOfSpeech.toString().replace("[", "").replace("]", "")
                            pos += "$currentPos@@@"

                            val currentNotes = item.tags.toString().replace("[", "").replace("]", "")
                            notes += "$currentNotes@@@"

                        }
                        english = removeAts(english)
                        pos = removeAts(pos)
                        notes = removeAts(notes)

                        starFilled = myDB.checkStarred(kanji, english.split("@@@")[0])

                        // items to view in searchpage activity
                        mSearchList!!.add(SearchDataItem(kanji, kana, english.split("@@@")[0], starFilled))

                        // items to view in viewwordremote and viewword
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

    fun removeAts(input: String): String {
        return input.substring(0, input.length - 3)
    }

    companion object {
        var sp: AppCompatActivity? = null
        //items for carrying over to viewwordremote
        var dataItems: ArrayList<MyListItem>? = ArrayList()
        var starFilled: Boolean = false
    }

}

