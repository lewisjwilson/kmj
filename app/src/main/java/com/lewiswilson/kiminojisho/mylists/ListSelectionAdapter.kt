package com.lewiswilson.kiminojisho.mylists

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lewiswilson.kiminojisho.R
import java.util.*
import com.lewiswilson.kiminojisho.search.ViewWordRemote


class ListSelectionAdapter(
    private val mContext: Context,
    private var mDataList: ArrayList<ListSelectionItem>
) : RecyclerView.Adapter<ListSelectionAdapter.ListSelectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListSelectionViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.list_selection_item, parent, false)
        return ListSelectionViewHolder(v, mListener)
    }

    override fun onBindViewHolder(holder: ListSelectionViewHolder, position: Int) {
        val currentItem = mDataList[position]
        val name = currentItem.name
        holder.mNameView.text = name
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class ListSelectionViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        var mNameView: TextView = itemView.findViewById(R.id.list_name)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }

    private lateinit var mListener : onItemClickListener


    interface onItemClickListener {
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }





}

