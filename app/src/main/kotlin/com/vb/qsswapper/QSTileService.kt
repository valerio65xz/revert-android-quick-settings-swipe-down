package com.vb.qsswapper

import android.accessibilityservice.AccessibilityServiceInfo
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.view.accessibility.AccessibilityManager

class QSTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    // STATE_UNAVAILABLE makes the tile unclickable; onClick only fires when the service is running.
    override fun onClick() {
        super.onClick()
        Prefs.setEnabled(this, !Prefs.isEnabled(this))
        updateTile()
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        if (!isAccessibilityServiceRunning()) {
            tile.state = Tile.STATE_UNAVAILABLE
            tile.subtitle = null
        } else if (Prefs.isEnabled(this)) {
            tile.state = Tile.STATE_ACTIVE
            tile.subtitle = getString(R.string.tile_subtitle_on)
        } else {
            tile.state = Tile.STATE_INACTIVE
            tile.subtitle = getString(R.string.tile_subtitle_off)
        }
        tile.updateTile()
    }

    private fun isAccessibilityServiceRunning(): Boolean {
        val am = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val target = "${packageName}/${SwipeAccessibilityService::class.java.name}"
        return am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .any { it.id == target }
    }
}
