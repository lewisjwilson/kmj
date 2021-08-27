package com.lewiswilson.kiminojisho

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lewiswilson.kiminojisho.flashcards.FlashcardsHome

class ReminderBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        //on system boot, restart the repeating alarm
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {

            val prefsName = "MyPrefs"
            val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

            var alarmTime = Long.MIN_VALUE

            try {
                alarmTime = prefs.getLong("daily_notifications", 0)
            } catch (e: Exception) {
                Log.d(TAG, "Get Notifications time: Value ($alarmTime)")
            }

            val alarmIntent = Intent(context, ReminderBroadcast::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val alarmManager = context.applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmTime,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )

        } else {

            val myDB = DatabaseHelper(context)

            val wordArr = myDB.random()
            val kanji = wordArr[0]
            val kana = wordArr[1]
            val english = wordArr[2]

            val reviewsDue = myDB.flashcardCount(0)

            var bigTextContent = "${kanji}\n${kana}\n${english}\n\n$reviewsDue reviews remaining!"
            if (reviewsDue == 0) {
                bigTextContent = "Start adding some words to your list!"
            }




            val notificationIntent = Intent(context, FlashcardsHome::class.java)
            notificationIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val builder = NotificationCompat.Builder(context, "wotd")
                .setSmallIcon(R.drawable.ic_check_circle_black_24dp)
                .setContentTitle("KimiNoJisho")
                .setSubText("Word of the Day")
                .setContentText(kanji)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(bigTextContent))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(100, builder.build())
        }
    }
}