package eu.rtech.appusagelimiter

import android.app.AlarmManager
import android.app.AppOpsManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.rtech.appusagelimiter.ui.theme.AppUsageLimiterTheme
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (!hasUsageStatsPermission(this)) {
            requestUsageStatsPermission()
        }

        // Check and request Exact Alarm permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!isExactAlarmPermissionGranted()) {
                requestExactAlarmPermission()
            }
        }

        setContent {
            AppUsageLimiterTheme {
                AppUsageLimiterScreen()
            }
        }
    }


    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        Toast.makeText(
            this,
            "Please grant usage access for the app to track usage",
            Toast.LENGTH_LONG
        ).show()
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun isExactAlarmPermissionGranted(): Boolean {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestExactAlarmPermission() {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        startActivity(intent)
        Toast.makeText(
            this,
            "Please grant exact alarm permission for break scheduling.",
            Toast.LENGTH_LONG
        ).show()
    }
}

@Composable
fun AppUsageLimiterScreen() {
    var usageTime by remember { mutableStateOf(0L) }
    val usageLimit = TimeUnit.MINUTES.toMillis(30) // 30 minutes usage limit
    val breakTime = TimeUnit.MINUTES.toMillis(10)  // 10 minutes break
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        usageTime = getYouTubeUsageTime(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "YouTube Usage Time: ${(usageTime / 1000 / 60)} minutes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Display Usage Status
        Text(
            text = "Usage: ${(usageTime / 1000 / 60)} minutes",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Button to Check Usage and Set Limit
        Button(onClick = {
            // Logic to refresh usage time or set limits can go here
            usageTime = getYouTubeUsageTime(context)
        }) {
            Text(text = "Refresh Usage Time")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Display Limit Reached or Remaining Time
        if (usageTime >= usageLimit) {
            Text(
                text = "Time limit reached! Taking a break.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.error
            )
        } else {
            val remainingTime = (usageLimit - usageTime) / 1000 / 60
            Text(
                text = "Remaining Time: $remainingTime min",
                fontSize = 16.sp
            )
        }
    }
}

fun scheduleBreak(context: Context, breakDuration: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, YourReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    alarmManager.setExact(
        AlarmManager.RTC_WAKEUP,
        System.currentTimeMillis() + breakDuration,
        pendingIntent
    )
}

