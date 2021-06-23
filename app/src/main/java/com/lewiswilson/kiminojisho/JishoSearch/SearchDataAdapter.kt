package com.lewiswilson.kiminojisho.JishoSearch

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lewiswilson.kiminojisho.*
import com.lewiswilson.kiminojisho.JishoSearch.SearchDataAdapter.SearchDataViewHolder
import java.util.*

class SearchDataAdapter(
    private val mContext: Context,
    private val mSearchList: ArrayList<SearchDataItem>
) : RecyclerView.Adapter<SearchDataViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchDataViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.search_data_item, parent, false)
        return SearchDataViewHolder(v)
    }

    override fun onBindViewHolder(holder: SearchDataViewHolder, position: Int) {
        val currentItem = mSearchList[position]
        val kanji = currentItem.kanji
        val kana = currentItem.kana
        val english = currentItem.english
        val example = currentItem.example
        val notes = currentItem.notes
        val star_filled = currentItem.star_filled
        holder.mKanjiView.text = kanji
        holder.mKanaView.text = kana
        holder.mEnglishView.text = english
        holder.mExampleView.text = example
        holder.mNotesView.text = notes
        if(!star_filled){
            holder.mStar.visibility = GONE
        }

    }

    override fun getItemCount(): Int {
        return mSearchList.size
    }

    inner class SearchDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnLongClickListener {
        var mKanjiView: TextView
        var mKanaView: TextView
        var mEnglishView: TextView
        var mExampleView: TextView
        var mNotesView: TextView
        var mStar: ImageView
        override fun onLongClick(view: View): Boolean {
            //get data from the clicked item and add to my list
            val kanji = mKanjiView.text.toString()
            val kana = mKanaView.text.toString()
            val english = mEnglishView.text.toString()
            val example = mExampleView.text.toString()
            val notes = mNotesView.text.toString()

            //no examples currently supported on jisho API
            val intent = Intent(mContext, ViewWordRemote::class.java)
            intent.putExtra("kanji", kanji)
            intent.putExtra("kana", kana)
            intent.putExtra("english",english)
            intent.putExtra("example", example)
            intent.putExtra("notes", notes)
            mContext.startActivity(intent)
            return true
        }

        init {
            itemView.setOnLongClickListener(this)
            mKanjiView = itemView.findViewById(R.id.kanjiview)
            mKanaView = itemView.findViewById(R.id.kanaview)
            mEnglishView = itemView.findViewById(R.id.englishview)
            mExampleView = itemView.findViewById(R.id.examples)
            mNotesView = itemView.findViewById(R.id.notes)
            mStar = itemView.findViewById(R.id.star)
        }
    }
}