package com.vb.qsswapper

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent

class SwipeAccessibilityService : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private var overlay: View? = null

    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        updateOverlay()
    }

    override fun onServiceConnected() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        Prefs.prefs(this).registerOnSharedPreferenceChangeListener(prefListener)
        updateOverlay()
    }

    private fun updateOverlay() {
        if (Prefs.isEnabled(this)) addOverlay() else removeOverlay()
    }

    private fun addOverlay() {
        if (overlay != null) return

        // 5% of screen height — matches the system gesture zone on ColorOS
        val screenHeight = resources.displayMetrics.heightPixels
        val overlayHeight = (screenHeight * 0.05f).toInt()

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            overlayHeight,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        val view = View(this)
        var startX = 0f
        var triggered = false

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    triggered = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!triggered) {
                        triggered = true
                        // Left half = default Notifications zone → open QS (inverted)
                        // Right half = default Quick Settings zone → open Notifications (inverted)
                        if (startX < v.width / 2f) expandQuickSettings() else expandNotifications()
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Required for accessibility: fire performClick on a tap (no swipe triggered)
                    if (!triggered) v.performClick()
                    triggered = false
                    true
                }
                else -> false
            }
        }

        windowManager.addView(view, params)
        overlay = view
    }

    private fun removeOverlay() {
        overlay?.let {
            windowManager.removeView(it)
            overlay = null
        }
    }

    private fun expandNotifications() {
        performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
    }

    private fun expandQuickSettings() {
        performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        removeOverlay()
        Prefs.prefs(this).unregisterOnSharedPreferenceChangeListener(prefListener)
    }
}
