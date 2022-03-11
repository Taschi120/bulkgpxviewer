package de.taschi.bulkgpxviewer.ui.map

import org.jxmapviewer.JXMapViewer
import org.jxmapviewer.painter.Painter
import java.awt.Graphics2D

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
*/   class CompositePainter : Painter<JXMapViewer> {
    private val painters: MutableList<Painter<JXMapViewer>> = ArrayList()
    fun addPainter(painter: Painter<JXMapViewer>) {
        painters.add(painter)
    }

    override fun paint(g: Graphics2D, `object`: JXMapViewer, width: Int, height: Int) {
        for (painter in painters) {
            painter.paint(g, `object`, width, height)
        }
    }
}