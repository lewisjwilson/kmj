package com.lewiswilson.kiminojisho

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class MyListAdapter(private val mContext: Context,
                    private val mDataList: ArrayList<MyListItem>,
                    private val myDB: DatabaseHelper,
                    private val listener: onItemClickListener) : RecyclerView.Adapter<MyListAdapter.MyListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.my_list_item, parent, false)
        return MyListViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyListViewHolder, position: Int) {
        val currentItem = mDataList[position]
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
        return mDataList.size
    }

    inner class MyListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var mKanjiView: TextView = itemView.findViewById(R.id.kanjiview)
        var mKanaView: TextView = itemView.findViewById(R.id.kanaview)
        var mEnglishView: TextView = itemView.findViewById(R.id.englishview)
        var mNotesView: TextView = itemView.findViewById(R.id.notesview)

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val kanji  = mKanjiView.text.toString()
            listener.onItemClick(kanji)
        }

    }

    interface onItemClickListener {
        fun onItemClick(kanji: String)
    }

}