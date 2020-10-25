package com.lewiswilson.kiminojisho.SearchRecycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lewiswilson.kiminojisho.R;

import java.util.ArrayList;

public class SearchDataAdapter extends RecyclerView.Adapter<SearchDataAdapter.SearchDataViewHolder> {
    private Context mContext;
    private ArrayList<SearchDataItem> mSearchList;

    public SearchDataAdapter(Context context, ArrayList<SearchDataItem> searchList){
        mContext = context;
        mSearchList = searchList;
    }

    @NonNull
    @Override
    public SearchDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.search_data_item, parent, false);
        return new SearchDataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchDataViewHolder holder, int position) {
        SearchDataItem currentItem = mSearchList.get(position);

        String kanji = currentItem.getKanji();
        String kana = currentItem.getKana();
        String english = currentItem.getEnglish();

        holder.mKanjiView.setText(kanji);
        holder.mKanaView.setText(kana);
        holder.mEngishView.setText(english);
    }

    @Override
    public int getItemCount() {
        return mSearchList.size();
    }

    public class SearchDataViewHolder extends RecyclerView.ViewHolder {
        public TextView mKanjiView;
        public TextView mKanaView;
        public TextView mEngishView;

        public SearchDataViewHolder(@NonNull View itemView) {
            super(itemView);

            mKanjiView = itemView.findViewById(R.id.kanjiview);
            mKanaView = itemView.findViewById(R.id.kanaview);
            mEngishView = itemView.findViewById(R.id.englishview);

        }
    }
}
