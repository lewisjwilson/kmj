package com.lewiswilson.kiminojisho.search

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lewiswilson.kiminojisho.*
import com.lewiswilson.kiminojisho.search.SearchDataAdapter.SearchDataViewHolder
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
        val starFilled = currentItem.starFilled
        holder.mKanjiView.text = kanji
        holder.mKanaView.text = kana
        holder.mEnglishView.text = english
        if(!starFilled){
            holder.mStar.visibility = GONE
        }
    }

    override fun getItemCount(): Int {
        return mSearchList.size
    }

    inner class SearchDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var mKanjiView: TextView = itemView.findViewById(R.id.kanjiview)
        var mKanaView: TextView = itemView.findViewById(R.id.kanaview)
        var mEnglishView: TextView = itemView.findViewById(R.id.englishview)
        var mStar: ImageView = itemView.findViewById(R.id.star)

        override fun onClick(view: View) {
            //get data from the clicked item and add to my list
            var starFilled = false

            if(mStar.visibility != GONE) {
                starFilled = true
            }

            val intent = Intent(mContext, ViewWordRemote::class.java)
            intent.putExtra("adapterPos", adapterPosition)
            intent.putExtra("star_filled", starFilled)
            mContext.startActivity(intent)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
}