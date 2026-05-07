package com.vb.qsswapper

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * Minimal onboarding activity — appears as a dialog, closes immediately after interaction.
 * Its only job is to guide the user to enable the Accessibility Service.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isAccessibilityServiceRunning()) showReadyDialog() else showSetupDialog()
    }

    private fun showSetupDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_setup_title)
            .setMessage(R.string.dialog_setup_message)
            .setPositiveButton(R.string.dialog_setup_positive) { _, _ ->
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                finish()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> finish() }
            .setOnCancelListener { finish() }
            .show()
    }

    private fun showReadyDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_ready_title)
            .setMessage(R.string.dialog_ready_message)
            .setPositiveButton(android.R.string.ok) { _, _ -> finish() }
            .setOnCancelListener { finish() }
            .show()
    }

    private fun isAccessibilityServiceRunning(): Boolean {
        val am = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val target = "${packageName}/${SwipeAccessibilityService::class.java.name}"
        return am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .any { it.id == target }
    }
}
