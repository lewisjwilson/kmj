package com.lewiswilson.kiminojisho.jishoSearch

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.lewiswilson.kiminojisho.*
import java.util.*

class ViewWordRemoteItemAdapter(
    private val mContext: Context,
    private val mViewWordRemoteList: ArrayList<ViewWordRemoteItem>
) : RecyclerView.Adapter<ViewWordRemoteItemAdapter.ViewWordRemoteViewHolder>() {
    private var starred: Boolean = false
    private var kanji: String = ""
    private var kana: String = ""
    private var english: String = ""
    private var pos: String = ""
    private var notes: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewWordRemoteViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.view_word_remote_item, parent, false)
        return ViewWordRemoteViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewWordRemoteViewHolder, position: Int) {
        val currentItem = mViewWordRemoteList[position]
        kanji = currentItem.kanji
        kana = currentItem.kana
        english = currentItem.english
        pos = currentItem.pos
        notes = currentItem.notes
        starred = currentItem.starFilled

        holder.mKanjiView.text = kanji
        holder.mKanaView.text = kana
        holder.mEnglishView.text = english
        holder.mPosView.text = pos
        holder.mNotesView.isEnabled = false
        holder.mNotesView.setText(notes)
        holder.mNotesView.hint = "None"

        if(!starred){
            holder.mStarBtn.setImageResource(R.drawable.ic_addword)
        }

    }

    override fun getItemCount(): Int {
        return mViewWordRemoteList.size
    }

    inner class ViewWordRemoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val myDB = DatabaseHelper(mContext)

        var mKanjiView: TextView = itemView.findViewById(R.id.vwri_kanji)
        var mKanaView: TextView = itemView.findViewById(R.id.vwri_kana)
        var mEnglishView: TextView = itemView.findViewById(R.id.vwri_english)
        var mPosView: TextView = itemView.findViewById(R.id.vwri_pos)
        var mNotesView: EditText = itemView.findViewById(R.id.vwri_edit_notes)
        var mStarBtn: ImageView = itemView.findViewById(R.id.vwri_btn_star)

        override fun onClick(view: View) {
            // save button
            if (view.id == mStarBtn.id) {
                Toast.makeText(view.context, "Star Button Pressed", Toast.LENGTH_SHORT).show()
                starred = !starred
                if (starred) {
                    mStarBtn.setImageResource(R.drawable.ic_removeword)
                    myDB.addData(0, kanji , kana, english, pos, notes)
                } else {
                    mStarBtn.setImageResource(R.drawable.ic_addword)
                    try {
                        myDB.deleteFromRemote(kanji, english)
                    } catch (e: NullPointerException) {
                        Log.d("Item Not Found", "Item not in dictionary. (Normal)")
                    }
                }
            }

        }

        init {
            mStarBtn.setOnClickListener(this)
        }
    }
}