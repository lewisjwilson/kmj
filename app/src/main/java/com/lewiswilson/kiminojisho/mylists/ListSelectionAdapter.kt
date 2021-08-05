package com.lewiswilson.kiminojisho.mylists

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lewiswilson.kiminojisho.R
import java.util.*


class ListSelectionAdapter(
    private val mContext: Context,
    private var mDataList: ArrayList<ListSelectionItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ListSelectionAdapter.ListSelectionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListSelectionViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.list_selection_item, parent, false)
        return ListSelectionViewHolder(v)
    }

    override fun onBindViewHolder(holder: ListSelectionViewHolder, position: Int) {
        val currentItem = mDataList[position]
        val name = currentItem.name
        holder.mNameView.text = name

    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class ListSelectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mNameView: TextView = itemView.findViewById(R.id.list_name)

    }

    interface OnItemClickListener {
        fun onItemClick(itemId: Int)
    }

}