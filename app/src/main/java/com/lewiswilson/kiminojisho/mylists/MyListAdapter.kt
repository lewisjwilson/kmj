package com.lewiswilson.kiminojisho.mylists

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lewiswilson.kiminojisho.R
import java.util.*


class MyListAdapter(
    private val mContext: Context,
    private var mDataList: ArrayList<MyListItem>,
    private val listener: OnItemClickListener,
    private val longListener: OnItemLongClickListener
) : RecyclerView.Adapter<MyListAdapter.MyListViewHolder>() {

    companion object {
        var multiSelectMode = false
        var selectedIds = arrayListOf<Int>()
    }

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

        //changes the background color for items when they come into view in rv
        if (selectedIds.contains(id)) {
            holder.itemView.setBackgroundColor(mContext.getColor(R.color.magic_mint))
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE)
        }

    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class MyListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        var mId: TextView = itemView.findViewById(R.id.itemid)
        var mKanjiView: TextView = itemView.findViewById(R.id.kanjiview)
        var mKanaView: TextView = itemView.findViewById(R.id.kanaview)
        var mEnglishView: TextView = itemView.findViewById(R.id.englishview)


        init{
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)

        }

        override fun onClick(v: View?) {
            val id = Integer.parseInt(mId.text.toString())
            if (multiSelectMode) {
                selectMultiple(itemView, id)
                listener.onItemClick(id, false)
            } else {
                listener.onItemClick(id, true)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            multiSelectMode = true
            val id = Integer.parseInt(mId.text.toString())
            selectMultiple(itemView, id)
            longListener.onItemLongClick(id)
            return true
        }

    }

    interface OnItemClickListener {
        fun onItemClick(itemId: Int, ready: Boolean)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(itemId: Int)
    }

    fun selectMultiple(itemView: View, id: Int) {
        if(selectedIds.contains(id)) {
            itemView.setBackgroundColor(Color.WHITE)
            selectedIds.remove(id)
        } else {
            itemView.setBackgroundColor(mContext.getColor(R.color.magic_mint))
            selectedIds.add(id)
        }
        if(selectedIds.isEmpty()) {
            multiSelectMode = false
        }
    }

}

