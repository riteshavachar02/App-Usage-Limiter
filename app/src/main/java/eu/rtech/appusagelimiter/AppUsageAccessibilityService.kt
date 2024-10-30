package eu.rtech.appusagelimiter

// AppUsageAccessibilityService.kt
import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class AppUsageAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.packageName == "com.instagram.android") { // Replace with target app package
            Toast.makeText(this, "App usage limit reached!", Toast.LENGTH_SHORT).show()
            // Optional: Close or block app
        }
    }

    override fun onInterrupt() {}
}
