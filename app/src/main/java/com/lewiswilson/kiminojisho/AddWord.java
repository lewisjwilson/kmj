package com.lewiswilson.kiminojisho;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddWord extends AppCompatActivity {
    DatabaseHelper myDB;

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.add_word);
        this.myDB = new DatabaseHelper(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        getWindow().setLayout((int) (((double) dm.widthPixels) * 0.8d), (int) (((double) dm.heightPixels) * 0.8d));
        Button btn_add = (Button) findViewById(R.id.btn_add);
        final EditText editText = (EditText) findViewById(R.id.edit_word);
        final EditText editText2 = (EditText) findViewById(R.id.edit_kana);
        final EditText editText3 = (EditText) findViewById(R.id.edit_meaning);
        final EditText editText4 = (EditText) findViewById(R.id.edit_example);
        OnClickListener r0 = new OnClickListener() {
            public void onClick(View v) {
                String newEntryWord = editText.getText().toString().trim();
                String newEntryKana = editText2.getText().toString().trim();
                String newEntryMeaning = editText3.getText().toString().trim();
                String newEntryExample = editText4.getText().toString().trim();
                if (newEntryWord.length() == 0 || newEntryKana.length() == 0 || newEntryMeaning.length() == 0) {
                    Toast.makeText(AddWord.this, "Fill in Required Fields!", Toast.LENGTH_SHORT).show();
                } else {
                    AddWord.this.AddData(newEntryWord, newEntryKana, newEntryMeaning, newEntryExample);
                }
                AddWord addWord = AddWord.this;
                addWord.startActivity(new Intent(addWord, MainActivity.class));
                AddWord.this.finish();
                MainActivity.ma.finish();
            }
        };
        btn_add.setOnClickListener(r0);
    }

    public void AddData(String word, String kana, String meaning, String example) {
        if (this.myDB.addData(word, kana, meaning, example)) {
            Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Insertion Failed", Toast.LENGTH_SHORT).show();
        }
    }
}