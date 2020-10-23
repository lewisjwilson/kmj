package com.lewiswilson.kiminojisho;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lewiswilson.kiminojisho.JSON.Datum;
import com.lewiswilson.kiminojisho.JSON.Japanese;
import com.lewiswilson.kiminojisho.JSON.JishoAPI;
import com.lewiswilson.kiminojisho.JSON.JishoData;
import com.lewiswilson.kiminojisho.JSON.RetrofitClient;
import com.lewiswilson.kiminojisho.JSON.Sense;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchPage extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        final EditText et_searchfield = findViewById(R.id.et_searchfield);
        final Button search_button = findViewById(R.id.search_button);
        final TextView tv_kanji_search = findViewById(R.id.tv_kanji_search);
        final TextView tv_kana_search = findViewById(R.id.tv_kana_search);
        final TextView tv_meaning_search = findViewById(R.id.tv_meaning_search);

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
                            List<Sense> sense = data.get(0).getSenses();
                            String english = sense.get(0).getEnglishDefinitions().get(0);

                            tv_kanji_search.setText(kanji);
                            tv_kana_search.setText(kana);
                            tv_meaning_search.setText(english);
                        } catch(Exception e){
                            Log.d("", "Data Retrieval Error: " + e.getMessage());
                            //Toast.makeText(getApplicationContext(), JishoAPI.BASE_URL + " " + searchtext, Toast.LENGTH_LONG).show();
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
