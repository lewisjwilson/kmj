package com.lewiswilson.kiminojisho;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ViewWord extends AppCompatActivity {

  TextView txt_word_val;
  EditText edit_kana, edit_meaning, edit_example;
  Button btn_delete, btn_update, btn_hideshow;
  DatabaseHelper myDB;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_word);

    String list_selection;
    list_selection = MainActivity.list_selection;
    myDB = new DatabaseHelper(this);

    String unparsed_data = myDB.readData(list_selection);

    //parsing data
    String parsed_word = unparsed_data.split(";")[0];
    final String parsed_kana = unparsed_data.split(";")[1];
    final String parsed_meaning = unparsed_data.split(";")[2];

    //try/catch in case example column is empty...(no array index of 3 after split...)
    String parsed_example;
    try {
      parsed_example = unparsed_data.split(";")[3];
    }catch (Exception e){
      parsed_example = "";
    }

    txt_word_val = (TextView)findViewById(R.id.txt_word_val);
    txt_word_val.setText(parsed_word);
    edit_kana = (EditText) findViewById(R.id.edit_kana);
    edit_meaning = (EditText) findViewById(R.id.edit_meaning);
    edit_example = (EditText) findViewById(R.id.edit_example);
    edit_kana.setText(getString(R.string.Hidden));
    edit_meaning.setText(getString(R.string.Hidden));
    edit_example.setText(getString(R.string.Hidden));
    edit_kana.setEnabled(false);
    edit_meaning.setEnabled(false);
    edit_example.setEnabled(false);

    btn_hideshow = (Button)findViewById(R.id.btn_hideshow);
      final boolean[] button_status = {false};
      final String finalParsed_example = parsed_example;
      btn_hideshow.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!button_status[0]){
                btn_update.setEnabled(true);
                edit_kana.setText(parsed_kana);
                edit_meaning.setText(parsed_meaning);
                edit_example.setText(finalParsed_example);
                edit_kana.setEnabled(true);
                edit_meaning.setEnabled(true);
                edit_example.setEnabled(true);
                button_status[0] = true;
            } else {
                btn_update.setEnabled(false);
                edit_kana.setText(getString(R.string.Hidden));
                edit_meaning.setText(getString(R.string.Hidden));
                edit_example.setText(getString(R.string.Hidden));
                edit_kana.setEnabled(false);
                edit_meaning.setEnabled(false);
                edit_example.setEnabled(false);
                button_status[0] = false;
            }

        }
    });


    btn_delete = (Button)findViewById(R.id.btn_delete);

    final String finalList_selection = list_selection;
    btn_delete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        myDB.deleteData(finalList_selection);
        Toast.makeText(ViewWord.this, "Entry Deleted", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ViewWord.this, MainActivity.class));
        finish();
        MainActivity.ma.finish();
      }
    });

    btn_update = (Button)findViewById(R.id.btn_update);
    btn_update.setEnabled(false);
    btn_update.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String new_kana = edit_kana.getText().toString().trim();
        String new_meaning = edit_meaning.getText().toString().trim();
        String new_example = edit_example.getText().toString().trim();

        if((new_kana.length() != 0)&&(new_meaning.length() != 0)){

          myDB.updateData(finalList_selection, new_kana, new_meaning, new_example);
          Toast.makeText(ViewWord.this, "Entry Updated", Toast.LENGTH_SHORT).show();
          startActivity(new Intent(ViewWord.this, MainActivity.class));
          finish();
          MainActivity.ma.finish();
        }else {
          Toast.makeText(ViewWord.this, "Fill in Required Fields!", Toast.LENGTH_SHORT).show();
        }

      }
    });

      FloatingActionButton flbtn_rand = (FloatingActionButton) findViewById(R.id.flbtn_rand);
      flbtn_rand.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              MainActivity.list_selection = myDB.random(0);
              finish();
              startActivityForResult(getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION),0);
              overridePendingTransition(0,0);
          }
      });

  }
}
