package com.lewiswilson.kiminojisho

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ViewWord : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_word)

        val edit_kana: EditText = findViewById(R.id.edit_kana)
        val edit_meaning: EditText = findViewById(R.id.edit_meaning)
        val edit_example: EditText = findViewById(R.id.edit_example)
        val edit_notes: EditText = findViewById(R.id.edit_notes)
        var btn_update: Button = findViewById(R.id.btn_update)
        val myDB: DatabaseHelper = DatabaseHelper(this)
        val list_selection: String? = MainActivity.list_selection
        val unparsed_data = myDB.readData(list_selection)

        //parsing data
        val parsed_word = unparsed_data.split(";").toTypedArray()[0]
        val parsed_kana = unparsed_data.split(";").toTypedArray()[1]
        val parsed_meaning = unparsed_data.split(";").toTypedArray()[2]

        //try/catch in case example column is empty...(no array index of 3 after split...)
        val parsed_example: String
        parsed_example = try {
            unparsed_data.split(";").toTypedArray()[3]
        } catch (e: Exception) {
            ""
        }
        val parsed_notes: String
        parsed_notes = try {
            unparsed_data.split(";").toTypedArray()[4]
        } catch (e: Exception) {
            ""
        }
        val txt_word_val = findViewById<TextView>(R.id.txt_word_val)
        txt_word_val.text = parsed_word
        edit_kana.setText(getString(R.string.Hidden))
        edit_meaning.setText(getString(R.string.Hidden))
        edit_example.setText(getString(R.string.Hidden))
        edit_notes.setText(getString(R.string.Hidden))
        edit_kana.setEnabled(false)
        edit_meaning.setEnabled(false)
        edit_example.setEnabled(false)
        edit_notes.setEnabled(false)
        val btn_hideshow = findViewById<Button>(R.id.btn_hideshow)
        val button_status = booleanArrayOf(false)
        btn_hideshow.setOnClickListener { v: View? ->
            if (!button_status[0]) {
                btn_update.isEnabled = true
                edit_kana.setText(parsed_kana)
                edit_meaning.setText(parsed_meaning)
                edit_example.setText(parsed_example)
                edit_notes.setText(parsed_notes)
                edit_kana.setEnabled(true)
                edit_meaning.setEnabled(true)
                edit_example.setEnabled(true)
                edit_notes.setEnabled(true)
                button_status[0] = true
            } else {
                btn_update.isEnabled = false
                edit_kana.setText(getString(R.string.Hidden))
                edit_meaning.setText(getString(R.string.Hidden))
                edit_example.setText(getString(R.string.Hidden))
                edit_notes.setText(getString(R.string.Hidden))
                edit_kana.setEnabled(false)
                edit_meaning.setEnabled(false)
                edit_example.setEnabled(false)
                edit_notes.setEnabled(false)
                button_status[0] = false
            }
        }
        val btn_delete = findViewById<Button>(R.id.btn_delete)
        btn_delete.setOnClickListener { v: View? ->
            myDB.deleteData(list_selection)
            Toast.makeText(this@ViewWord, "Entry Deleted", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@ViewWord, MainActivity::class.java))
            finish()
            MainActivity.ma!!.finish()
        }
        btn_update = findViewById(R.id.btn_update)
        btn_update.setEnabled(false)
        btn_update.setOnClickListener(View.OnClickListener { v: View? ->
            val new_kana = edit_kana.getText().toString().trim { it <= ' ' }
            val new_meaning = edit_meaning.getText().toString().trim { it <= ' ' }
            val new_example = edit_example.getText().toString().trim { it <= ' ' }
            val new_notes = edit_notes.getText().toString().trim { it <= ' ' }
            if (new_kana.length != 0 && new_meaning.length != 0) {
                myDB.updateData(list_selection, new_kana, new_meaning, new_example, new_notes)
                Toast.makeText(this@ViewWord, "Entry Updated", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@ViewWord, MainActivity::class.java))
                finish()
                MainActivity.ma!!.finish()
            } else {
                Toast.makeText(this@ViewWord, "Fill in Required Fields!", Toast.LENGTH_SHORT).show()
            }
        })
        val flbtn_rand = findViewById<FloatingActionButton>(R.id.flbtn_rand)
        flbtn_rand.setOnClickListener { v: View? ->
            MainActivity.list_selection = myDB.random(0)
            finish()
            startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), 0)
            overridePendingTransition(0, 0)
        }
    }
}