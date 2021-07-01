package com.lewiswilson.kiminojisho

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class ExamplesAdapter(
    private val mContext: Context,
    private var mDataList: ArrayList<ExamplesItem>
) : RecyclerView.Adapter<ExamplesAdapter.MyListViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.examples_item, parent, false)
        return MyListViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyListViewHolder, position: Int) {
        val currentItem = mDataList[position]
        val japanese = currentItem.japanese
        val english = currentItem.english
        holder.mJapaneseView.text = japanese
        holder.mEnglishView.text = english

    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class MyListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mJapaneseView: TextView = itemView.findViewById(R.id.ex_japanese)
        var mEnglishView: TextView = itemView.findViewById(R.id.ex_english)


    }

}