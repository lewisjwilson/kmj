package com.lewiswilson.kiminojisho.mylists

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lewiswilson.kiminojisho.DatabaseHelper
import com.lewiswilson.kiminojisho.R
import java.util.*
import com.lewiswilson.kiminojisho.search.ViewWordRemote


class ListSelectionAdapter(
    private val mContext: Context,
    private var mDataList: ArrayList<ListSelectionItem>
) : RecyclerView.Adapter<ListSelectionAdapter.ListSelectionViewHolder>(){

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

    inner class ListSelectionViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val myDB = DatabaseHelper(mContext)
        var mNameView: TextView = itemView.findViewById(R.id.list_name)
        var mListDelete: ImageView = itemView.findViewById(R.id.list_delete)

        init {
            itemView.setOnClickListener { listener.onItemClick(myDB.getListIdFromName(mNameView.text.toString())) }
            mListDelete.setOnClickListener { listener.onDeleteClick(myDB.getListIdFromName(mNameView.text.toString()), adapterPosition) }
        }

    }

    private lateinit var mListener : OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(listId : Int)
        fun onDeleteClick(listId: Int, pos: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener

    }

}

