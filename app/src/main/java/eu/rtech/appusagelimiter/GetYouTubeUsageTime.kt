package eu.rtech.appusagelimiter

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.*

fun getYouTubeUsageTime(context: Context): Long {
    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    // Get the usage stats for today
    val usageStatsList: List<UsageStats> = usageStatsManager.queryUsageStats(
        UsageStatsManager.INTERVAL_DAILY,
        calendar.timeInMillis,
        System.currentTimeMillis()
    )

    var totalTimeInForeground = 0L

    // Loop through the usage stats to find YouTube
    for (usageStats in usageStatsList) {
        if (usageStats.packageName == "com.google.android.youtube") {
            totalTimeInForeground += usageStats.totalTimeInForeground
        }
    }

    return totalTimeInForeground
}
