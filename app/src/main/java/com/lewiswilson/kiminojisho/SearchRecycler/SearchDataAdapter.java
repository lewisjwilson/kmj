package com.lewiswilson.kiminojisho.SearchRecycler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lewiswilson.kiminojisho.DatabaseHelper;
import com.lewiswilson.kiminojisho.MainActivity;
import com.lewiswilson.kiminojisho.R;
import com.lewiswilson.kiminojisho.SearchPage;

import java.util.ArrayList;

public class SearchDataAdapter extends RecyclerView.Adapter<SearchDataAdapter.SearchDataViewHolder>{
    private Context mContext;
    private ArrayList<SearchDataItem> mSearchList;
    private DatabaseHelper myDB;

    public SearchDataAdapter(Context context, ArrayList<SearchDataItem> searchList, DatabaseHelper db){
        mContext = context;
        mSearchList = searchList;
        myDB = db;
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
        holder.mEnglishView.setText(english);

    }

    @Override
    public int getItemCount() {
        return mSearchList.size();
    }

    public class SearchDataViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        public TextView mKanjiView;
        public TextView mKanaView;
        public TextView mEnglishView;

        public SearchDataViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);

            mKanjiView = itemView.findViewById(R.id.kanjiview);
            mKanaView = itemView.findViewById(R.id.kanaview);
            mEnglishView = itemView.findViewById(R.id.englishview);

        }

        @Override
        public boolean onLongClick(View view) {
            //get data from the clicked item and add to my list
            String kanji = mKanjiView.getText().toString();
            String kana = mKanaView.getText().toString();
            String english = mEnglishView.getText().toString();

            //no examples currently supported on jisho API
            AddData(kanji, kana, english, "");
            return true;
        }

        private void AddData(String word, String kana, String meaning, String example) {
            if (myDB.addData(word, kana, meaning, example)) {
                Toast.makeText(mContext, "Data Inserted", Toast.LENGTH_SHORT).show();
                //call searchpage context and move to mainactivity
                mContext.startActivity(new Intent(mContext, MainActivity.class));
            } else {
                Toast.makeText(mContext, "Insertion Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
