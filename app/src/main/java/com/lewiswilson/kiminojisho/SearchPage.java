package com.lewiswilson.kiminojisho;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lewiswilson.kiminojisho.JSON.Datum;
import com.lewiswilson.kiminojisho.JSON.JishoData;
import com.lewiswilson.kiminojisho.JSON.RetrofitClient;

import java.text.DateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchPage extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        Call<JishoData> call = RetrofitClient.getInstance().getMyApi().getData();

        call.enqueue(new Callback<JishoData>() {
            @Override
            public void onResponse(Call<JishoData> call, Response<JishoData> response) {

                //At this point we got our word list
                List<Datum> data = response.body().getData();
                int arr_size = data.size();

                String slug = "Slug is: " + data.get(0).getSlug();

                String content = "Success, array size is: " + arr_size;

                Toast.makeText(getApplicationContext(), slug, Toast.LENGTH_LONG).show();
                Log.d("", content);

            }

            @Override
            public void onFailure(Call<JishoData> call, Throwable t) {
                //handle error or failure cases here
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("", "SearchPage (Error): " + t.getMessage());
            }
        });

    }
}
