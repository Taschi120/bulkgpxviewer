package de.taschi.bulkgpxviewer.ui

import java.awt.Color
import java.awt.Component
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.border.Border

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
 */ /**
 * Renderer to display a [JList] of [Color]s
 */
class ColorListItemRenderer constructor() : JLabel(), ListCellRenderer<Color?> {
    public override fun getListCellRendererComponent(
        list: JList<out Color?>?,
        value: Color?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component? {
        setText(
            value!!.getRed()
                .toString() + Messages.getString("ColorListItemRenderer.Comma") + value.getGreen() + Messages.getString(
                "ColorListItemRenderer.Comma"
            ) + value.getBlue()
        ) //$NON-NLS-1$ //$NON-NLS-2$
        setForeground(value)
        if (cellHasFocus) {
            setBorder(active)
        } else {
            setBorder(inactive)
        }
        return this
    }

    companion object {
        private val serialVersionUID: Long = -5909430684119887421L
        private val active: Border = BorderFactory.createEtchedBorder()
        private val inactive: Border = BorderFactory.createEmptyBorder()
    }
}