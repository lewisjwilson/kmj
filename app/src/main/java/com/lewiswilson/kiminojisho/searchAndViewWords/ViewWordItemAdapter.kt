package com.lewiswilson.kiminojisho.searchAndViewWords

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.lewiswilson.kiminojisho.*
import kotlinx.android.synthetic.main.view_word_item.view.*
import java.util.*

class ViewWordItemAdapter(
    private val mContext: Context,
    private val mViewWordList: ArrayList<ViewWordItem>
) : RecyclerView.Adapter<ViewWordItemAdapter.ViewWordRemoteViewHolder>() {
    private var remote: Boolean = false
    private var id: String = ""
    private var kanji: String = ""
    private var kana: String = ""
    private var english: String = ""
    private var pos: String = ""
    private var notes: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewWordRemoteViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.view_word_item, parent, false)
        return ViewWordRemoteViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewWordRemoteViewHolder, position: Int) {
        val currentItem = mViewWordList[position]
        id = currentItem.id
        kanji = currentItem.kanji
        kana = currentItem.kana
        english = currentItem.english
        pos = currentItem.pos
        notes = currentItem.notes

        holder.mEnglishView.text = english
        holder.mPosView.text = pos


        holder.mNotesView.text = notes
        if (notes.isEmpty()) {
            holder.mNotesView.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return mViewWordList.size
    }

    inner class ViewWordRemoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mEnglishView: TextView = itemView.findViewById(R.id.vwi_english)
        var mPosView: TextView = itemView.findViewById(R.id.vwi_pos)
        var mNotesView: TextView = itemView.findViewById(R.id.vwi_jisho_notes)


    }
}