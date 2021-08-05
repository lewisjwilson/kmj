package com.lewiswilson.kiminojisho

import android.app.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.lewiswilson.kiminojisho.flashcards.FlashcardsHome
import com.lewiswilson.kiminojisho.mylists.ListSelection
import com.lewiswilson.kiminojisho.mylists.MyList
import com.lewiswilson.kiminojisho.search.SearchPage
import kotlinx.android.synthetic.main.home_screen.*
import java.io.File
import java.io.FileInputStream
import java.util.*


class HomeScreen : AppCompatActivity() {

    private val prefsName = "MyPrefs"
    private var myDB: DatabaseHelper? = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.Nature, true)
        setContentView(R.layout.home_screen)

        item_flashcards.setOnClickListener { v: View? -> startActivity(Intent(this@HomeScreen, FlashcardsHome::class.java)) }
        item_search.setOnClickListener { v: View? -> startActivity(Intent(this@HomeScreen, SearchPage::class.java)) }
        item_mylists.setOnClickListener { v: View? -> startActivity(Intent(this@HomeScreen, MyList::class.java)) }
        item_settings.setOnClickListener { v: View? -> startActivity(Intent(this@HomeScreen, About::class.java)) }

    }

    // Menu icons are inflated just as they were with actionbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_screen_menu, menu)
        return true
    }

    //Toolbar Menu Option Activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_help -> {
                Toast.makeText(this, "Help display", Toast.LENGTH_LONG).show()
                true
            }
            R.id.action_import -> {
                val diaBox = importWarning()
                diaBox.show()
                true
            }
            R.id.action_export -> {
                exportDatabase()
                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun importWarning(): AlertDialog {
        return AlertDialog.Builder(this)
            .setTitle("Import")
            .setMessage("Select your previously exported 'kiminojisho.db' file. " +
                    "IMPORTANT: All data will be completely overwritten. Are you SURE you want to overwrite everything?")
            .setPositiveButton("Import") { dialog: DialogInterface, whichButton: Int ->
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "*/*"
                startActivityForResult(intent, MyList.REQUEST_CODE)
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .create()
    }

    private fun importDatabase() {
        try {
            myDB!!.createDatabase()
            finish()
            startActivity(intent)
            Log.d(TAG, "Database export sucessful.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun exportDatabase() {
        try {
            val fileToWrite = getDatabasePath(myDB?.databaseName).toString()
            val output = openFileOutput(myDB?.databaseName, Context.MODE_PRIVATE)
            val input = FileInputStream(fileToWrite)
            val buffer = ByteArray(1024)
            var length: Int
            while (input.read(buffer).also { length = it } > 0) {
                output.write(buffer, 0, length)
            }
            output.write(fileToWrite.toByteArray())
            output.flush()
            output.close()
            input.close()

            //exporting
            val context = applicationContext
            val filelocation = File(filesDir, myDB!!.databaseName)
            val path = FileProvider.getUriForFile(context, "com.lewiswilson.kiminojisho.fileprovider", filelocation)
            val fileIntent = Intent(Intent.ACTION_SEND)
            fileIntent.type = "application/x-sqlite3"
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, myDB?.databaseName)
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            fileIntent.putExtra(Intent.EXTRA_STREAM, path)
            startActivity(Intent.createChooser(fileIntent, "Export Database"))
            Log.d(TAG, "Database export sucessful.")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //Request Permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 || !(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "Permission denied to read External storage", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MyList.REQUEST_CODE && resultCode == Activity.RESULT_OK || data != null) {
            if (data != null) {
                MyList.fileUri = data.data
            }
            importDatabase()
        }
    }
}