package de.taschi.bulkgpxviewer.settings

/**
 * An interface through which objects can be notified about settings changes.
 */
open interface SettingsUpdateListener {
    fun onSettingsUpdated()
}