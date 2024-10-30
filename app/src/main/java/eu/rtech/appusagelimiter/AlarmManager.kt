package eu.rtech.appusagelimiter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

fun scheduleUsageCheck(context: Context, intervalMillis: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, YourReceiver::class.java) // Create a BroadcastReceiver for alarms
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    // Set a repeating alarm
    alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        System.currentTimeMillis() + intervalMillis,
        intervalMillis,
        pendingIntent
    )
}
