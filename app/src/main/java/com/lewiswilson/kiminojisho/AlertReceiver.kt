package com.lewiswilson.kiminojisho

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = Notifications(context)
        val nb = notificationHelper.channelNotification
        notificationHelper.manager?.notify(1, nb.build())
    }
}