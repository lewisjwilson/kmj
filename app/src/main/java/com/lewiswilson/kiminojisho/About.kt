package com.lewiswilson.kiminojisho

import android.app.*
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.about.*
import java.text.DecimalFormat
import java.util.*

class About : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.Nature, true)
        setContentView(R.layout.about)

        val prefsName = "MyPrefs"
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        val alarmUp = PendingIntent.getBroadcast(applicationContext, 0,
            Intent(applicationContext, ReminderBroadcast::class.java), PendingIntent.FLAG_NO_CREATE) != null

        //sets enabled status based on if alarm exists
        switch_notifications.isChecked = alarmUp
        if(switch_notifications.isChecked) {
            txt_status.text = getString(R.string.ON)
            //setting time to value in sharedprefs
            val millis = prefs.getLong("daily_notifications", 0)
            val c = Calendar.getInstance()
            c.timeInMillis = millis
            timepicker1.hour = c[Calendar.HOUR_OF_DAY]
            timepicker1.minute = c[Calendar.MINUTE]
            Log.d(TAG, "Notifications set for: ${c.time} daily.")
        }

        timepicker1.setIs24HourView(true)

        switch_notifications.setOnClickListener {

            if(switch_notifications.isChecked) {
                txt_status.text = getString(R.string.ON)
                val hour = timepicker1.hour
                val min = timepicker1.minute
                createChannel()
                setNotifications(hour, min)

            } else {
                txt_status.text = getString(R.string.OFF)
                cancelNotifications()
            }
        }

        spn_theme!!.onItemSelectedListener
        val spnAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.themes))
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_theme!!.adapter = spnAdapter

    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Daily Notifications"
            val description = "Word of the day"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("wotd", name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // notification functions
    private fun setNotifications(hour: Int, min: Int) {

        val prefsName = "MyPrefs"
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        val c = Calendar.getInstance()

        val currentTime = c.timeInMillis

        c[Calendar.HOUR_OF_DAY] = hour
        c[Calendar.MINUTE] = min
        c[Calendar.SECOND] = 0

        val notifyTime = c.timeInMillis

        // if time is in the past, add a day to prevent notification being shown instantly
        if(notifyTime<currentTime){
            c[Calendar.DAY_OF_MONTH] += 1
        }

        val intent = Intent(applicationContext, ReminderBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)

        // enables notification even after reboot
        val receiver = ComponentName(applicationContext, ReminderBroadcast::class.java)
        applicationContext.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        val dec = DecimalFormat("00")
        val time = "${dec.format(hour)}:${dec.format(min)}"

        //add into shared prefs for if reboot occurs (check ReminderBroadcast.kt)
        prefs.edit().putLong("daily_notifications", c.timeInMillis).apply()

        Toast.makeText(applicationContext, "Notifications set for $time daily", Toast.LENGTH_LONG).show()

    }

    private fun cancelNotifications() {
        //cancel
        PendingIntent.getBroadcast(applicationContext, 0,
            Intent(applicationContext, ReminderBroadcast::class.java), PendingIntent.FLAG_UPDATE_CURRENT).cancel()

        // cancels popping up on device reboot
        val receiver = ComponentName(applicationContext, ReminderBroadcast::class.java)
        applicationContext.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        Toast.makeText(applicationContext, "Notifications stopped", Toast.LENGTH_LONG).show()
    }

    // spinner functions
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


}

