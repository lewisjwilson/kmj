package com.lewiswilson.kiminojisho;

import android.content.Intent;
import android.icu.lang.UCharacter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lewiswilson.kiminojisho.JSON.Datum;
import com.lewiswilson.kiminojisho.JSON.Japanese;
import com.lewiswilson.kiminojisho.JSON.JishoData;
import com.lewiswilson.kiminojisho.JSON.RetrofitClient;
import com.lewiswilson.kiminojisho.JSON.Sense;
import com.lewiswilson.kiminojisho.SearchRecycler.SearchDataAdapter;
import com.lewiswilson.kiminojisho.SearchRecycler.SearchDataItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchPage extends AppCompatActivity{
    private RecyclerView mRecyclerView;
    private SearchDataAdapter mSearchDataAdapter;
    private ArrayList<SearchDataItem> mSearchList;
    private DatabaseHelper myDB;
    public static AppCompatActivity sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);
        sp = this;

        myDB = new DatabaseHelper(this);

        //initiate recyclerview and set parameters
        mRecyclerView = findViewById(R.id.rv_searchdata);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSearchList = new ArrayList<>();

        final EditText et_searchfield = findViewById(R.id.et_searchfield);
        final Button search_button = findViewById(R.id.search_button);
        final Button btn_manual = findViewById(R.id.btn_manual);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if the search adapter has data in it already, clear the recyclerview
                if(mSearchDataAdapter!=null){
                    clearData();
                }

                String searchtext = et_searchfield.getText().toString();
                Call<JishoData> call;

                //if the seatchtext contains any japanese...
                if(containsJapanese(searchtext)){
                    call = RetrofitClient.getInstance().getMyApi().getData(searchtext);
                } else {
                    //use searchtext to query API (using API interface)
                    call = RetrofitClient.getInstance().getMyApi().getData("\"" + searchtext + "\"");
                }

                call.enqueue(new Callback<JishoData>() {
                    @Override
                    public void onResponse(Call<JishoData> call, Response<JishoData> response) {

                        //At this point we got our word list
                        List<Datum> data = response.body().getData();

                        //try to retrieve data from jishoAPI
                        try {

                            List<Japanese> japanese;
                            String kanji = null, kana = null, english;

                            for(int i=0; i<data.size(); i++){
                                japanese = data.get(i).getJapanese();

                                for(int j=0; j<japanese.size(); j++){
                                    kanji = japanese.get(j).getWord();
                                    kana = japanese.get(j).getReading();
                                    
                                    //if the result has no associated kanji
                                    if(kanji == null){
                                        kanji = kana;
                                    }


                                }
                                
                                //get english definitions
                                List<Sense> sense = data.get(i).getSenses();
                                int noOfDefinitions = sense.get(0).getEnglishDefinitions().size();
                                english = sense.get(0).getEnglishDefinitions().get(0);

                                //If there is more than one definition, also display the second definition
                                if(noOfDefinitions>1){
                                    english = english + ", " + sense.get(0).getEnglishDefinitions().get(1);
                                }
                                mSearchList.add(new SearchDataItem(kanji, kana, english));
                            }

                            mSearchDataAdapter = new SearchDataAdapter(SearchPage.this, mSearchList, myDB);
                            mRecyclerView.setAdapter(mSearchDataAdapter);

                        } catch(Exception e){
                            Log.d("", "Data Retrieval Error: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "No data found" , Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<JishoData> call, Throwable t) {
                        //handle error or failure cases here
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("", "SearchPage (Error): " + t.getMessage());
                    }
                });

            }
        });

        btn_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchPage.this, AddWord.class));
            }
        });

    }

    private boolean containsJapanese(String input){
        Set<UCharacter.UnicodeBlock> japaneseUnicodeBlocks = new HashSet<UCharacter.UnicodeBlock>() {{
            add(UCharacter.UnicodeBlock.HIRAGANA);
            add(UCharacter.UnicodeBlock.KATAKANA);
            add(UCharacter.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
        }};

        //for each character in string, if a japanese character occurs at all, return true
        for (char c: input.toCharArray()){
            if(japaneseUnicodeBlocks.contains(UCharacter.UnicodeBlock.of(c))){
                return true;
            }
        }
        return false;
    }

    public void clearData() {
        mSearchList.clear(); // clear list
        mSearchDataAdapter.notifyDataSetChanged(); // let your adapter know about the changes and reload view.
    }
}
