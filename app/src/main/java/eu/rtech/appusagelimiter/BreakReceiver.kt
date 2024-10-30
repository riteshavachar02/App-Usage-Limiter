package eu.rtech.appusagelimiter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class YourReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val usageTime = getYouTubeUsageTime(context)
        Log.d("YouTubeUsage", "YouTube usage time: ${usageTime / 1000 / 60} minutes")
        // Optionally, you can notify the user or perform additional actions
    }
}

