package com.lewiswilson.kiminojisho

import android.app.*
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence
import com.lewiswilson.kiminojisho.flashcards.FlashcardsHome
import com.lewiswilson.kiminojisho.mylists.ListSelection
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
        setContentView(R.layout.home_screen)

        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        //Check if it is a first time launch
        if (prefs.getBoolean("first_launch", true)) {
            helpDisplay()
            prefs.edit().putBoolean("first_launch", false).apply()
            prefs.edit().putBoolean("notifications_on", false).apply()
            prefs.edit().putString("sortby_col", "english").apply()
            prefs.edit().putStringSet("list_names", hashSetOf("Main List")).apply()
        }

        item_flashcards.setOnClickListener { startActivity(Intent(this@HomeScreen, FlashcardsHome::class.java)) }
        item_search.setOnClickListener { startActivity(Intent(this@HomeScreen, SearchPage::class.java)) }
        item_mylists.setOnClickListener { startActivity(Intent(this@HomeScreen, ListSelection::class.java)) }
        item_settings.setOnClickListener { startActivity(Intent(this@HomeScreen, Settings::class.java)) }

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
                helpDisplay()
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

    private fun helpDisplay() {
        val title = BubbleShowCaseBuilder(this)
            .title("Welcome to KiminoJisho!")
            .description("This app will help you learn Japanese by using spaced repitition (SRS) style flashcards on the Japanese words YOU choose!")
            .targetView(homescreen_blank_view)

        val flashcards = BubbleShowCaseBuilder(this)
            .title("Flashcards")
            .description("Here, you can test yourself using flashcards on the words in your lists. (You will need to add some words first!)")
            .targetView(item_flashcards)

        val search = BubbleShowCaseBuilder(this)
            .title("Search")
            .description("Search for a words to add to one of your lists.")
            .targetView(item_search)

        val myLists = BubbleShowCaseBuilder(this)
            .title("My Lists")
            .description("Check out and review the words you've added.")
            .targetView(item_mylists)

        val settings = BubbleShowCaseBuilder(this)
            .title("Settings")
            .description("Enable daily notifications etc.")
            .targetView(item_settings)


        val final = BubbleShowCaseBuilder(this)
            .title("That's all there is to it!")
            .description("We hope you enjoy Kiminojisho. To see this help display again, click the icon in the toolbar.")
            .targetView(homescreen_blank_view)

        BubbleShowCaseSequence()
            .addShowCase(title)
            .addShowCase(flashcards)
            .addShowCase(search)
            .addShowCase(myLists)
            .addShowCase(settings)
            .addShowCase(final)
            .show()
    }

    private fun importWarning(): AlertDialog {
        return AlertDialog.Builder(this)
            .setTitle("Import")
            .setMessage("Select your previously exported 'kiminojisho.db' file. " +
                    "IMPORTANT: All data will be completely overwritten. Are you SURE you want to overwrite everything?")
            .setPositiveButton("Import") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "*/*"
                startActivityForResult(intent, REQUEST_CODE)
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .create()
    }

    private fun importDatabase() {
        try {
            myDB!!.createDatabase()
            finish()
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun exportDatabase() {

        var errorThrown = false
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

            val chooser = Intent.createChooser(fileIntent, "Export Database")
            val resInfoList = this.packageManager.queryIntentActivities(
                chooser,
                PackageManager.MATCH_DEFAULT_ONLY
            )

            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                grantUriPermission(
                    packageName,
                    path,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            startActivity(chooser)
        } catch (e: Exception) {
            errorThrown = true
            e.printStackTrace()
        } finally {
            if (!errorThrown) {
                Log.d(TAG, "Database export sucessful.")
            }
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
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK || data != null) {
            if (data != null) {
                fileUri = data.data
            }
            importDatabase()
        }
    }

    companion object{
        const val REQUEST_CODE = 10
        @JvmField
        var fileUri: Uri? = null
    }

}