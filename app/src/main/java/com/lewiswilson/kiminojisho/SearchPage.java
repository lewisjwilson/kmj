package com.lewiswilson.kiminojisho;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lewiswilson.kiminojisho.JSON.Datum;
import com.lewiswilson.kiminojisho.JSON.Japanese;
import com.lewiswilson.kiminojisho.JSON.JishoAPI;
import com.lewiswilson.kiminojisho.JSON.JishoData;
import com.lewiswilson.kiminojisho.JSON.RetrofitClient;
import com.lewiswilson.kiminojisho.JSON.Sense;
import com.lewiswilson.kiminojisho.SearchRecycler.SearchDataAdapter;
import com.lewiswilson.kiminojisho.SearchRecycler.SearchDataItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchPage extends AppCompatActivity{
    private RecyclerView mRecyclerView;
    private SearchDataAdapter mSearchDataAdapter;
    private ArrayList<SearchDataItem> mSearchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        //initiate recyclerview and set parameters
        mRecyclerView = findViewById(R.id.rv_searchdata);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSearchList = new ArrayList<>();

        final EditText et_searchfield = findViewById(R.id.et_searchfield);
        final Button search_button = findViewById(R.id.search_button);

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchtext = et_searchfield.getText().toString();

                //use searchtext to query API (using API interface)
                Call<JishoData> call = RetrofitClient.getInstance().getMyApi().getData("\"" + searchtext + "\"");

                call.enqueue(new Callback<JishoData>() {
                    @Override
                    public void onResponse(Call<JishoData> call, Response<JishoData> response) {

                        //At this point we got our word list
                        List<Datum> data = response.body().getData();

                        try {
                            //get first japanese word in list and first word and reading
                            List<Japanese> japanese = data.get(0).getJapanese();
                            String kanji = japanese.get(0).getWord();
                            String kana = japanese.get(0).getReading();

                            //get first english definition
                            //List<Sense> sense = data.get(0).getSenses();
                            //String english = sense.get(0).getEnglishDefinitions().get(0);

                            for(int i=0; i<data.size(); i++){

                                japanese = data.get(i).getJapanese();

                                for(int j=0; j<japanese.size(); j++){
                                    kanji = japanese.get(j).getWord();
                                    kana = japanese.get(j).getReading();

                                    //get english definitions
                                    List<Sense> sense = data.get(j).getSenses();

                                    String english = sense.get(0).getEnglishDefinitions().get(0);

                                    if(sense.get(0).getEnglishDefinitions().size()>1){
                                        english = english + " ; " + sense.get(0).getEnglishDefinitions().get(1);
                                    }

                                    //if the result has no associated kanji
                                    if(kanji == null){
                                        kanji = kana;
                                    }

                                    mSearchList.add(new SearchDataItem(kanji, kana, english));
                                }
                            }


                            mSearchDataAdapter = new SearchDataAdapter(SearchPage.this, mSearchList);
                            mRecyclerView.setAdapter(mSearchDataAdapter);

                        } catch(Exception e){
                            Log.d("", "Data Retrieval Error: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "Data could not be retrieved: " + e.getMessage() , Toast.LENGTH_LONG).show();
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

    }
}
