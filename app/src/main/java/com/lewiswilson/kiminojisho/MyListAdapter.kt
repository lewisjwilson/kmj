package com.lewiswilson.kiminojisho

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class MyListAdapter(
    private val mContext: Context,
    private var mDataList: ArrayList<MyListItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<MyListAdapter.MyListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.my_list_item, parent, false)
        return MyListViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyListViewHolder, position: Int) {
        val currentItem = mDataList[position]
        val id = currentItem.id
        val kanji = currentItem.kanji
        val kana = currentItem.kana
        val english = currentItem.english
        holder.mId.text = id.toString()
        holder.mKanjiView.text = kanji
        holder.mKanaView.text = kana
        holder.mEnglishView.text = english

    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class MyListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var mId: TextView = itemView.findViewById(R.id.itemid)
        var mKanjiView: TextView = itemView.findViewById(R.id.kanjiview)
        var mKanaView: TextView = itemView.findViewById(R.id.kanaview)
        var mEnglishView: TextView = itemView.findViewById(R.id.englishview)

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val id = Integer.parseInt(mId.text.toString())
            Log.d("ID: ", id.toString())
            listener.onItemClick(id)
        }

    }

    interface OnItemClickListener {
        fun onItemClick(item_id: Int)
    }

}