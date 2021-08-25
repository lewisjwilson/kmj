package com.lewiswilson.kiminojisho.flashcards

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.lewiswilson.kiminojisho.DatabaseHelper
import com.lewiswilson.kiminojisho.R
import java.util.*


class FCListAdapter(
    private val mContext: Context,
    private var mDataList: ArrayList<FCListItem>
) : RecyclerView.Adapter<FCListAdapter.FCListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FCListViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.fc_list_item, parent, false)
        return FCListViewHolder(v, mListener)
    }

    override fun onBindViewHolder(holder: FCListViewHolder, position: Int) {
        val currentItem = mDataList[position]
        val name = currentItem.name
        holder.mNameView.text = name
        val reviewsText = "Reviews: ${currentItem.reviews}"
        holder.mReviewsText.text = reviewsText

        if (currentItem.reviews <= 0) {
            holder.mReviewBtn.text = mContext.getString(R.string.Complete)
            holder.mReviewBtn.isEnabled = false
        }

    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class FCListViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val myDB = DatabaseHelper(mContext)
        var mNameView: TextView = itemView.findViewById(R.id.fc_list_name)
        var mReviewsText: TextView = itemView.findViewById(R.id.txt_list_reviews)
        var mReviewBtn: Button = itemView.findViewById(R.id.btn_review_list)

        init {
            itemView.setOnClickListener { listener.onItemClick(myDB.getListIdFromName(mNameView.text.toString())) }
            mReviewBtn.setOnClickListener {
                val listId = myDB.getListIdFromName(mNameView.text.toString())
                if (myDB.itemCount(listId) < 4) {
                    Toast.makeText(mContext, "Not enough words in list (min = 4)", Toast.LENGTH_LONG).show()
                } else {
                    listener.onButtonClick(listId)
                }
            }
        }

    }

    private lateinit var mListener : OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(listId : Int)
        fun onButtonClick(listId: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener

    }

}

