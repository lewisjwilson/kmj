package com.lewiswilson.kiminojisho

import android.app.*
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lewiswilson.kiminojisho.JishoSearch.SearchPage
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.my_list_item.view.*
import kotlinx.android.synthetic.main.search_page.*
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import java.io.File
import java.io.FileInputStream
import java.util.*


class MainActivity : AppCompatActivity(),
    MyListAdapter.OnItemClickListener {
    private val PREFS_NAME = "MyPrefs"
    private var myDB: DatabaseHelper? = null
    private var jishoList: ArrayList<MyListItem>? = ArrayList()
    private var searchList: ArrayList<MyListItem>? = ArrayList()
    private var rvAdapter: MyListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
        ma = this
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


        //Check if it is a first time launch
        if (prefs.getBoolean("first_launch", true)) {
            firstLaunch()
            prefs.edit().putBoolean("first_launch", false).apply()
            prefs.edit().putBoolean("notifications_on", false).apply()
            prefs.edit().putString("sortby_col", "MEANING").apply()
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val flbtn_add = findViewById<FloatingActionButton>(R.id.flbtn_add)
        val flbtn_rand = findViewById<FloatingActionButton>(R.id.flbtn_rand)
        myDB = DatabaseHelper(this)

        //initiate recyclerview and set parameters
        rv_mylist.setHasFixedSize(true)
        rv_mylist.setLayoutManager(LinearLayoutManager(this))

        populateRV()
        //displaylist shows when searching in searchview
        searchList!!.addAll(jishoList!!)

        flbtn_add.setOnClickListener { v: View? -> startActivity(Intent(this@MainActivity, SearchPage::class.java)) }
        flbtn_rand.setOnClickListener { v: View? ->
            //list_index = myDB!!.random(0)
            startActivity(Intent(this@MainActivity, ViewWord::class.java))
        }
    }

    //populate recyclerview with data
    fun populateRV() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val column = prefs.getString("sortby_col", "MEANING")
        val data = myDB!!.listContents(column!!)

        //Checks if database is empty and lists entries if not
        if (data.count == 0) {
            flbtn_rand.isEnabled = false
        } else {
            flbtn_rand.isEnabled = true
            while (data.moveToNext()) {
                //ListView Data Layout
                if (data.getString(1) == data.getString(2)) {
                    jishoList!!.add(
                        MyListItem(data.getInt(0),
                            data.getString(1),
                            data.getString(1),
                            data.getString(3),
                            ""
                        )
                    )
                } else {
                   jishoList!!.add(
                        MyListItem(data.getInt(0),
                            data.getString(1),
                            data.getString(2),
                            data.getString(3),
                            data.getString(5)
                        )
                    )
                }
                rvAdapter = jishoList?.let { it -> MyListAdapter(this@MainActivity, it, this) }
                rv_mylist.adapter = rvAdapter

            }

        }

    }

    // recyclerview item click
    override fun onItemClick(id: Int) {
        item_id = id
        startActivity(Intent(this@MainActivity, ViewWord::class.java))
    }

    fun clearData() {
        jishoList!!.clear() // clear list
        rvAdapter!!.notifyDataSetChanged() // let your adapter know about the changes and reload view.
        populateRV()
    }

    // Menu icons are inflated just as they were with actionbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)

        //find search menuitem
        val menuItem = menu.findItem(R.id.searchView)
        //Initialize searchview
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchtext: String): Boolean {
                return false
            }

            override fun onQueryTextChange(searchtext: String?): Boolean {
                // populate the recyclerview on search query change
                if(searchtext!!.isNotEmpty()){
                    jishoList!!.clear()
                    val search = searchtext.toLowerCase(Locale.getDefault())
                    searchList!!.forEach {
                        if(it.english.toLowerCase(Locale.getDefault()).contains(search) or
                            it.kana.toLowerCase(Locale.getDefault()).contains(search) or
                            it.kanji.toLowerCase(Locale.getDefault()).contains(search)){

                            jishoList!!.add(it)
                        }
                    }

                    rvAdapter!!.notifyDataSetChanged()

                } else {
                    clearData()
                }

                return false
            }
        })
        return true
    }

    //Toolbar Menu Option Activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return when (item.itemId) {
            R.id.meaning -> {
                prefs.edit().putString("sortby_col", "MEANING").apply()
                prefs.edit().putString("sort_style", "ASC").apply()
                clearData()
                true
            }

            R.id.kana -> {
                prefs.edit().putString("sortby_col", "KANA").apply()
                prefs.edit().putString("sort_style", "ASC").apply()
                clearData()
                true
            }

            R.id.action_alarm -> {
                setupNotifications()
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
            R.id.action_help -> {
                firstLaunch()
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this@MainActivity, About::class.java))
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
                    startActivityForResult(intent, REQUEST_CODE)
                }
                .setNegativeButton("Cancel") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
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
        try {
            val fileToWrite = getDatabasePath("kiminojisho.db").toString()
            val output = openFileOutput("kiminojisho.db", Context.MODE_PRIVATE)
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
            val filelocation = File(filesDir, "kiminojisho.db")
            val path = FileProvider.getUriForFile(context, "com.lewiswilson.kiminojisho.fileprovider", filelocation)
            val fileIntent = Intent(Intent.ACTION_SEND)
            fileIntent.type = "application/x-sqlite3"
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "kiminojisho.db")
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            fileIntent.putExtra(Intent.EXTRA_STREAM, path)
            startActivity(Intent.createChooser(fileIntent, "Export Database"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Request Permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (!(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this@MainActivity, "Permission denied to read External storage", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                fileUri = data.data
                importDatabase()
            }
        }
    }

    private fun setupNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Daily Notifications"
            val description = "Word of the day"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("wotd", name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        timePicker()
    }

    private fun timePicker() {

        //boolean to check if alarm is active currently
        val alarmUp = PendingIntent.getBroadcast(applicationContext, 0,
                Intent(applicationContext, ReminderBroadcast::class.java), PendingIntent.FLAG_NO_CREATE) != null
        if (alarmUp) {
            //cancel
            PendingIntent.getBroadcast(applicationContext, 0,
                    Intent(applicationContext, ReminderBroadcast::class.java), PendingIntent.FLAG_UPDATE_CURRENT).cancel()
            Toast.makeText(applicationContext, "Notifications Stopped", Toast.LENGTH_LONG).show()
        } else {
            // Get Current Time
            val c = Calendar.getInstance()
            val currenthour = c[Calendar.HOUR_OF_DAY]
            val currentminute = c[Calendar.MINUTE]

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(this,
                    OnTimeSetListener { view, hourOfDay, minute ->
                        c[Calendar.HOUR_OF_DAY] = hourOfDay
                        c[Calendar.MINUTE] = minute
                        c[Calendar.SECOND] = 0
                        val intent = Intent(applicationContext, ReminderBroadcast::class.java)
                        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
                        Toast.makeText(applicationContext, "Daily Notifications Set for " +
                                c[Calendar.HOUR_OF_DAY] + ":" + c[Calendar.MINUTE], Toast.LENGTH_LONG).show()
                    }, currenthour, currentminute, true)
            timePickerDialog.show()
        }
    }

    companion object {
        private const val REQUEST_CODE = 10
        @JvmField
        var item_id //use item_id to get and display database data
                : Int? = null
        @JvmField
        var fileUri: Uri? = null
        @JvmField
        var ma: AppCompatActivity? = null
    }

    private fun firstLaunch() {
        val fscv1 = FancyShowCaseView.Builder(this)
            .title("Welcome to KimiNoJisho, the custom Japanese dictionary app! This tutorial will help to get you started.")
            .backgroundColor(Color.parseColor("#DD008577"))
            .titleStyle(R.style.HelpScreenTitle, Gravity.TOP or Gravity.CENTER)
            .build()
        val fscv2 = FancyShowCaseView.Builder(this)
            .title("This is the main screen. This shows you dictionary entries.")
            .backgroundColor(Color.parseColor("#DD008577"))
            .titleStyle(R.style.HelpScreenTitle, Gravity.CENTER)
            .build()
        val fscv3 = FancyShowCaseView.Builder(this)
            .title("To create your first dictionary entry, use this button.")
            .focusOn(findViewById(R.id.flbtn_add))
            .backgroundColor(Color.parseColor("#DD008577"))
            .titleStyle(R.style.HelpScreenTitle, Gravity.CENTER)
            .build()
        val fscv4 = FancyShowCaseView.Builder(this)
            .title("To test yourself, let the app choose a random word from your dictionary!")
            .focusOn(findViewById(R.id.flbtn_rand))
            .backgroundColor(Color.parseColor("#DD008577"))
            .titleStyle(R.style.HelpScreenTitle, Gravity.CENTER)
            .build()
        val fscvQueue = FancyShowCaseQueue()
            .add(fscv1)
            .add(fscv2)
            .add(fscv3)
            .add(fscv4)
        fscvQueue.show()
    }
}