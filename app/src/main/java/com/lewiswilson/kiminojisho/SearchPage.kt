package com.lewiswilson.kiminojisho

import android.content.Intent
import android.icu.lang.UCharacter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.lewiswilson.kiminojisho.JSON.Japanese
import com.lewiswilson.kiminojisho.JSON.JishoData
import com.lewiswilson.kiminojisho.JSON.RetrofitClient
import com.lewiswilson.kiminojisho.SearchRecycler.SearchDataAdapter
import com.lewiswilson.kiminojisho.SearchRecycler.SearchDataItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlinx.android.synthetic.main.search_page.*

class SearchPage : AppCompatActivity() {

    private var mSearchList: ArrayList<SearchDataItem>? = ArrayList()
    private var mSearchDataAdapter: SearchDataAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_page)
        sp = this
        val myDB = DatabaseHelper(this)

        //initiate recyclerview and set parameters
        rv_searchdata.setHasFixedSize(true)
        rv_searchdata.setLayoutManager(LinearLayoutManager(this))

        search_button.setOnClickListener {
            //if the search adapter has data in it already, clear the recyclerview
            if (mSearchDataAdapter != null) {
                clearData()
            }
            val searchtext = et_searchfield.text.toString()

            //if the searchtext contains any japanese...
            val call: Call<JishoData> = if (containsJapanese(searchtext)) {
                RetrofitClient.getInstance().myApi.getData(searchtext)
            } else {
                //use searchtext to query API (using API interface)
                RetrofitClient.getInstance().myApi.getData("\"" + searchtext + "\"")
            }
            call.enqueue(object : Callback<JishoData> {
                override fun onResponse(call: Call<JishoData>, response: Response<JishoData>) {

                    //At this point we got our word list
                    val data = response.body()!!.data

                    //if no data was found, try a call assuming romaji style
                    if (data.isEmpty()) {
                        Toast.makeText(applicationContext, "No data found", Toast.LENGTH_LONG).show()
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
                            mSearchList!!.add(SearchDataItem(kanji, kana, english, notes))
                        }
                        mSearchDataAdapter = mSearchList?.let { it1 -> SearchDataAdapter(this@SearchPage, it1, myDB) }
                        rv_searchdata.setAdapter(mSearchDataAdapter)
                    } catch (e: Exception) {
                        Log.d("", "Data Retrieval Error: " + e.message)
                        Toast.makeText(applicationContext, "No data found", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<JishoData>, t: Throwable) {
                    //handle error or failure cases here
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    Log.d("", "SearchPage (Error): " + t.message)
                }
            })
        }
        btn_manual.setOnClickListener { startActivity(Intent(this@SearchPage, AddWord::class.java)) }
    }

    private fun containsJapanese(input: String): Boolean {
        val japaneseUnicodeBlocks: HashSet<UCharacter.UnicodeBlock?> = object : HashSet<UCharacter.UnicodeBlock?>() {
            init {
                add(UCharacter.UnicodeBlock.HIRAGANA)
                add(UCharacter.UnicodeBlock.KATAKANA)
                add(UCharacter.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
            }
        }

        //for each character in string, if a japanese character occurs at all, return true
        for (c in input.toCharArray()) {
            if (japaneseUnicodeBlocks.contains(UCharacter.UnicodeBlock.of(c.toInt()))) {
                return true
            }
        }
        return false
    }

    fun clearData() {
        mSearchList!!.clear() // clear list
        mSearchDataAdapter!!.notifyDataSetChanged() // let your adapter know about the changes and reload view.
    }

    companion object {
        var sp: AppCompatActivity? = null
    }
}