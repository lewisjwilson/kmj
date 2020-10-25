package com.lewiswilson.kiminojisho;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


public class AddWord extends AppCompatActivity {
    private DatabaseHelper myDB;
    private Boolean ToggleKanji = true;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_word);

        this.myDB = new DatabaseHelper(this);

        final Button btn_toggleKanji = findViewById(R.id.btn_togglekanji);
        final Button btn_add = findViewById(R.id.btn_add);
        final TextView txt_word = findViewById(R.id.txt_word);
        final EditText wordEdit = findViewById(R.id.edit_word);
        final TextView txt_kana = findViewById(R.id.txt_kana);
        final EditText kanaEdit = findViewById(R.id.edit_kana);
        final EditText meaningEdit = findViewById(R.id.edit_meaning);
        final EditText exampleEdit = findViewById(R.id.edit_example);


        OnClickListener toggle = v -> {
            if (ToggleKanji) {
                ToggleKanji = false;
                txt_word.setText(R.string.Kana);
                txt_kana.setVisibility(View.GONE);
                kanaEdit.setVisibility(View.GONE);
            } else {
                ToggleKanji = true;
                txt_word.setText(R.string.Kanji);
                txt_kana.setVisibility(View.VISIBLE);
                kanaEdit.setVisibility(View.VISIBLE);
            }
        };
        btn_toggleKanji.setOnClickListener(toggle);

        OnClickListener add = v -> {
            String newEntryWord = wordEdit.getText().toString().trim();
            String newEntryKana = kanaEdit.getText().toString().trim();
            String newEntryMeaning = meaningEdit.getText().toString().trim();
            String newEntryExample = exampleEdit.getText().toString().trim();

            if (ToggleKanji) {
                if (newEntryWord.length() == 0 || newEntryKana.length() == 0 || newEntryMeaning.length() == 0) {
                    Toast.makeText(AddWord.this, "Fill in Required Fields!", Toast.LENGTH_SHORT).show();
                } else {
                    AddWord.this.AddData(newEntryWord, newEntryKana, newEntryMeaning, newEntryExample);
                }
            } else {
                if (newEntryWord.length() == 0 || newEntryMeaning.length() == 0) {
                    Toast.makeText(AddWord.this, "Fill in Required Fields!", Toast.LENGTH_SHORT).show();
                } else {
                    AddWord.this.AddData(newEntryWord, newEntryWord, newEntryMeaning, newEntryExample);
                }
            }

            AddWord addWord = AddWord.this;
            addWord.startActivity(new Intent(addWord, MainActivity.class));
            AddWord.this.finish();
            MainActivity.ma.finish();
        };
        btn_add.setOnClickListener(add);
    }

    private void AddData(String word, String kana, String meaning, String example) {
        if (this.myDB.addData(word, kana, meaning, example)) {
            Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Insertion Failed", Toast.LENGTH_SHORT).show();
        }
    }
}