package de.taschi.bulkgpxviewer.settings.dto

/*-
 * #%L
 * bulkgpxviewer
 * %%
 * Copyright (C) 2021 S. Hillebrand
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */   class Settings constructor() {
    var lastUsedDirectory: String? = null
    var routeColors: List<SettingsColor?>? = null
    var mainWindowSettings: MainWindowSettings? = null
    var unitSystem: UnitSystem? = null
    var unselectedRouteColor: SettingsColor? = null
    var selectedRouteColor: SettingsColor? = null
    public override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (!(o is Settings)) return false
        val other: Settings = o
        if (!other.canEqual(this as Any?)) return false
        val `this$lastUsedDirectory`: Any? = lastUsedDirectory
        val `other$lastUsedDirectory`: Any? = other.lastUsedDirectory
        if (if (`this$lastUsedDirectory` == null) `other$lastUsedDirectory` != null else !(`this$lastUsedDirectory` == `other$lastUsedDirectory`)) return false
        val `this$routeColors`: Any? = routeColors
        val `other$routeColors`: Any? = other.routeColors
        if (if (`this$routeColors` == null) `other$routeColors` != null else !(`this$routeColors` == `other$routeColors`)) return false
        val `this$mainWindowSettings`: Any? = mainWindowSettings
        val `other$mainWindowSettings`: Any? = other.mainWindowSettings
        if (if (`this$mainWindowSettings` == null) `other$mainWindowSettings` != null else !(`this$mainWindowSettings` == `other$mainWindowSettings`)) return false
        val `this$unitSystem`: Any? = unitSystem
        val `other$unitSystem`: Any? = other.unitSystem
        if (if (`this$unitSystem` == null) `other$unitSystem` != null else !(`this$unitSystem` == `other$unitSystem`)) return false
        val `this$unselectedRouteColor`: Any? = unselectedRouteColor
        val `other$unselectedRouteColor`: Any? = other.unselectedRouteColor
        if (if (`this$unselectedRouteColor` == null) `other$unselectedRouteColor` != null else !(`this$unselectedRouteColor` == `other$unselectedRouteColor`)) return false
        val `this$selectedRouteColor`: Any? = selectedRouteColor
        val `other$selectedRouteColor`: Any? = other.selectedRouteColor
        if (if (`this$selectedRouteColor` == null) `other$selectedRouteColor` != null else !(`this$selectedRouteColor` == `other$selectedRouteColor`)) return false
        return true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is Settings
    }

    public override fun hashCode(): Int {
        val PRIME: Int = 59
        var result: Int = 1
        val `$lastUsedDirectory`: Any? = lastUsedDirectory
        result = result * PRIME + (if (`$lastUsedDirectory` == null) 43 else `$lastUsedDirectory`.hashCode())
        val `$routeColors`: Any? = routeColors
        result = result * PRIME + (if (`$routeColors` == null) 43 else `$routeColors`.hashCode())
        val `$mainWindowSettings`: Any? = mainWindowSettings
        result = result * PRIME + (if (`$mainWindowSettings` == null) 43 else `$mainWindowSettings`.hashCode())
        val `$unitSystem`: Any? = unitSystem
        result = result * PRIME + (if (`$unitSystem` == null) 43 else `$unitSystem`.hashCode())
        val `$unselectedRouteColor`: Any? = unselectedRouteColor
        result = result * PRIME + (if (`$unselectedRouteColor` == null) 43 else `$unselectedRouteColor`.hashCode())
        val `$selectedRouteColor`: Any? = selectedRouteColor
        result = result * PRIME + (if (`$selectedRouteColor` == null) 43 else `$selectedRouteColor`.hashCode())
        return result
    }

    public override fun toString(): String {
        return "Settings(lastUsedDirectory=" + lastUsedDirectory + ", routeColors=" + routeColors + ", mainWindowSettings=" + mainWindowSettings + ", unitSystem=" + unitSystem + ", unselectedRouteColor=" + unselectedRouteColor + ", selectedRouteColor=" + selectedRouteColor + ")"
    }
}