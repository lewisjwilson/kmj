package com.lewiswilson.kiminojisho.SearchRecycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.lewiswilson.kiminojisho.DatabaseHelper
import com.lewiswilson.kiminojisho.MainActivity
import com.lewiswilson.kiminojisho.R
import com.lewiswilson.kiminojisho.SearchRecycler.SearchDataAdapter.SearchDataViewHolder
import java.util.*

class SearchDataAdapter(private val mContext: Context, private val mSearchList: ArrayList<SearchDataItem>, private val myDB: DatabaseHelper) : RecyclerView.Adapter<SearchDataViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchDataViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.search_data_item, parent, false)
        return SearchDataViewHolder(v)
    }

    override fun onBindViewHolder(holder: SearchDataViewHolder, position: Int) {
        val currentItem = mSearchList[position]
        val kanji = currentItem.kanji
        val kana = currentItem.kana
        val english = currentItem.english
        val notes = currentItem.notes
        holder.mKanjiView.text = kanji
        holder.mKanaView.text = kana
        holder.mEnglishView.text = english
        holder.mNotesView.text = notes
        if (notes == null) {
            holder.mNotesView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mSearchList.size
    }

    inner class SearchDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnLongClickListener {
        var mKanjiView: TextView
        var mKanaView: TextView
        var mEnglishView: TextView
        var mNotesView: TextView
        override fun onLongClick(view: View): Boolean {
            //get data from the clicked item and add to my list
            val kanji = mKanjiView.text.toString()
            val kana = mKanaView.text.toString()
            val english = mEnglishView.text.toString()
            val notes = mNotesView.text.toString()

            //no examples currently supported on jisho API
            AddData(kanji, kana, english, "", notes)
            return true
        }

        private fun AddData(word: String, kana: String, meaning: String, example: String, notes: String) {
            if (myDB.addData(word, kana, meaning, example, notes)) {
                Toast.makeText(mContext, "Data Inserted", Toast.LENGTH_SHORT).show()
                //call searchpage context and move to mainactivity
                mContext.startActivity(Intent(mContext, MainActivity::class.java))
            } else {
                Toast.makeText(mContext, "Insertion Failed", Toast.LENGTH_SHORT).show()
            }
        }

        init {
            itemView.setOnLongClickListener(this)
            mKanjiView = itemView.findViewById(R.id.kanjiview)
            mKanaView = itemView.findViewById(R.id.kanaview)
            mEnglishView = itemView.findViewById(R.id.englishview)
            mNotesView = itemView.findViewById(R.id.notesview)
        }
    }
}