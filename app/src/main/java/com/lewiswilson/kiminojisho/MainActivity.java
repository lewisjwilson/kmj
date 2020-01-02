package com.lewiswilson.kiminojisho;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private static final int REQUEST_CODE = 10;
    public static String list_selection;//use to collect the "WORD" value and display data in ViewWord
    public static Uri fileUri;
    DatabaseHelper myDB;
    public static Activity ma;
    final String PREFS_NAME = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        ma=this;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        //Check if it is a first time launch
        if (prefs.getBoolean("first_launch", true)) {
            firstLaunch();
            prefs.edit().putBoolean("first_launch", false).apply();
            prefs.edit().putBoolean("notifications_on", false).apply();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView listView = findViewById(R.id.list_jisho);
        listView.setEmptyView(findViewById(R.id.txt_listempty));
        FloatingActionButton flbtn_add = findViewById(R.id.flbtn_add);
        FloatingActionButton flbtn_rand = findViewById(R.id.flbtn_rand);
        myDB = new DatabaseHelper(this);

        ArrayList<String> jishoList = new ArrayList<>();
        Cursor data = myDB.getListContents();

        //Checks if database is empty and lists entries if not
        if(data.getCount() == 0){
            flbtn_rand.setEnabled(false);
            Toast.makeText(MainActivity.this, "The Database is Empty", Toast.LENGTH_SHORT).show();
        }else{
            flbtn_rand.setEnabled(true);
            while(data.moveToNext()){
                //ListView Data Layout
                if (data.getString(1).equals(data.getString(2))){
                    jishoList.add(data.getString(1) + " ; " +
                            data.getString(3));
                } else {
                    jishoList.add(data.getString(1) + " ; " +
                            data.getString(2) + " ; " +
                            data.getString(3));
                }
                ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, jishoList);
                listView.setAdapter(listAdapter);
            }
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get "Word" value from listview for using to select db record
                list_selection = (String) listView.getItemAtPosition(position);
                list_selection = list_selection.split(" ;")[0];
                startActivity(new Intent(MainActivity.this, ViewWord.class));
            }
    });

        flbtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddWord.class));
            }
        });
        flbtn_rand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_selection = myDB.random(0);
                startActivity(new Intent(MainActivity.this, ViewWord.class));
            }
        });
    }

    private void firstLaunch() {
        final FancyShowCaseView fscv1 = new FancyShowCaseView.Builder(this)
                .title("Welcome to KimiNoJisho, the custom Japanese dictionary app! This tutorial will help to get you started.")
                .backgroundColor(Color.parseColor("#DD008577"))
                .titleStyle(R.style.HelpScreenTitle, Gravity.CENTER)
                .build();

        final FancyShowCaseView fscv2 = new FancyShowCaseView.Builder(this)
                .title("This is the main screen. This shows you dictionary entries.")
                .backgroundColor(Color.parseColor("#DD008577"))
                .titleStyle(R.style.HelpScreenTitle, Gravity.CENTER)
                .build();

        final FancyShowCaseView fscv3= new FancyShowCaseView.Builder(this)
                .title("To create your first dictionary entry, use this button.")
                .focusOn(findViewById(R.id.flbtn_add))
                .backgroundColor(Color.parseColor("#DD008577"))
                .titleStyle(R.style.HelpScreenTitle, Gravity.CENTER)
                .build();

        final FancyShowCaseView fscv4= new FancyShowCaseView.Builder(this)
                .title("To test yourself, let the app choose a random word from your dictionary!")
                .focusOn(findViewById(R.id.flbtn_rand))
                .backgroundColor(Color.parseColor("#DD008577"))
                .titleStyle(R.style.HelpScreenTitle, Gravity.CENTER)
                .build();

        FancyShowCaseQueue fscvQueue = new FancyShowCaseQueue()
                .add(fscv1)
                .add(fscv2)
                .add(fscv3)
                .add(fscv4);

        fscvQueue.show();
    }
    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //Toolbar Menu Option Activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_alarm:
                notificationSetter();
                return true;
            case R.id.action_import:
                AlertDialog diaBox = importWarning();
                diaBox.show();
                return true;
            case R.id.action_export:
                exportDatabase();
                return true;
            case R.id.action_help:
                firstLaunch();
                return true;
            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, About.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AlertDialog importWarning()
    {
        return new AlertDialog.Builder(this)
                .setTitle("Import")
                .setMessage("Select your previously exported 'kiminojisho.db' file. " +
                        "IMPORTANT: All data will be completely overwritten. Are you SURE you want to overwrite everything?")
                .setPositiveButton("Import", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.setType("*/*");
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    public void importDatabase() {
        try {
            myDB.createDatabase();
            Toast.makeText(this, "Import Successful", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
        } catch (IOException e) {
            throw new Error("Unable to create Database");
        }
    }

    public void exportDatabase(){
        try {
            String fileToWrite = this.getDatabasePath("kiminojisho.db").toString();

            FileOutputStream output = openFileOutput("kiminojisho.db", Context.MODE_PRIVATE);
            FileInputStream input = new FileInputStream(fileToWrite);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.write(fileToWrite.getBytes());
            output.flush();
            output.close();
            input.close();

            //exporting
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "kiminojisho.db");
            Uri path = FileProvider.getUriForFile(context, "com.lewiswilson.kiminojisho.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("application/x-sqlite3");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "kiminojisho.db");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Export Database"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void notificationSetter(){
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean("notifications_on", true)) {
            cancelAlarm();
        } else {
            Toast.makeText(MainActivity.this, "Select a time for daily notifications to appear", Toast.LENGTH_LONG).show();
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker");
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        startAlarm(c);
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean("notifications_on", true).apply();
        Toast.makeText(MainActivity.this, "Daily notifications set", Toast.LENGTH_LONG).show();

    }

    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean("notifications_on", false).apply();
        Toast.makeText(MainActivity.this, "Daily notifications stopped", Toast.LENGTH_LONG).show();
    }

    //Request Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            if(!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(MainActivity.this, "Permission denied to read External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            if(data!=null){
                fileUri = data.getData();

                importDatabase();
            }
        }
    }
}
