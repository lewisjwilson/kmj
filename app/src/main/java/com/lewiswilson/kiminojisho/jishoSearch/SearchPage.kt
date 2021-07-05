package com.lewiswilson.kiminojisho.jishoSearch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.lewiswilson.kiminojisho.AddWord
import com.lewiswilson.kiminojisho.DatabaseHelper
import com.lewiswilson.kiminojisho.json.Japanese
import com.lewiswilson.kiminojisho.json.JishoData
import com.lewiswilson.kiminojisho.json.RetrofitClient
import com.lewiswilson.kiminojisho.R
import kotlinx.android.synthetic.main.search_data_item.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlinx.android.synthetic.main.search_page.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

class SearchPage : AppCompatActivity(), CoroutineScope {

    private var mSearchList: ArrayList<SearchDataItem>? = ArrayList()
    private var mSearchDataAdapter: SearchDataAdapter? = null
    private val myDB = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_page)
        job = Job()
        setUpSearchStateFlow()
        sp = this

        // setting autofocus on searchview when activity is started
        sv_searchfield.isIconifiedByDefault = false
        sv_searchfield.isFocusable = true
        sv_searchfield.requestFocusFromTouch()

        //initiate recyclerview and set parameters
        rv_searchdata.setHasFixedSize(true)
        rv_searchdata.setLayoutManager(LinearLayoutManager(this))

       btn_manual.setOnClickListener { startActivity(Intent(this@SearchPage, AddWord::class.java)) }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun setUpSearchStateFlow() {
        launch {
            sv_searchfield.getQueryTextChangeStateFlow()
                .debounce(500)
                .filter { query ->
                    return@filter !query.isEmpty()
                }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    dataFromNetwork(query)
                        .catch {
                            emitAll(flowOf(""))
                        }
                }
                .flowOn(Dispatchers.Main)
                .collect{}
        }
    }

    private fun SearchView.getQueryTextChangeStateFlow(): StateFlow<String> {

        val query = MutableStateFlow("")

        setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                query.value = newText
                return true
            }
        })

        return query
    }

    //get API data
    private fun dataFromNetwork(query: String): Flow<String> {
        return flow {
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
                        tv_info.setText("No results found")
                    }

                    //try to retrieve data from jishoAPI
                    try {
                        var japanese: List<Japanese>
                        var kanji: String?
                        var kana: String?
                        var english: String
                        var notes: String? = null
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

                            //get first tag in entry
                            val noOfTags = sense[0].tags.size
                            if (noOfTags > 0) {
                                notes = "[ " + sense[0].tags[0] + " ]"
                            }

                            //If there is more than one definition, also display the second definition
                            if (noOfDefinitions > 1) {
                                english = english + ", " + sense[0].englishDefinitions[1]
                            }

                           val  starFilled = myDB.checkStarred(kanji)

                            mSearchList!!.add(SearchDataItem(kanji, kana, english, "", "", starFilled))
                        }
                        mSearchDataAdapter = mSearchList?.let { it -> SearchDataAdapter(
                            this@SearchPage,
                            it
                        ) }
                        rv_searchdata.adapter = mSearchDataAdapter
                    } catch (e: Exception) {
                        Log.d("", "Data Retrieval Error: " + e.message)
                    }
                }

                override fun onFailure(call: Call<JishoData>, t: Throwable) {
                    //handle error or failure cases here
                    Log.d("", "SearchPage (Error): " + t.message)
                }
            })
            emit(query)
        }
    }

    fun clearData() {
        mSearchList!!.clear() // clear list
        mSearchDataAdapter!!.notifyDataSetChanged() // let your adapter know about the changes and reload view.
    }

    companion object {
        var sp: AppCompatActivity? = null
    }
}
