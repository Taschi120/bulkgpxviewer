package de.taschi.bulkgpxviewer.ui.map;

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
 */

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;

public class CompositePainter implements Painter<JXMapViewer> {
	
	private List<Painter<JXMapViewer>> painters = new ArrayList<>();
	
	public void addPainter(Painter<JXMapViewer> painter) {
		painters.add(painter);
	}
	
	@Override
	public void paint(Graphics2D g, JXMapViewer object, int width, int height) {
		for(Painter<JXMapViewer> painter: painters) {
			painter.paint(g, object, width, height);
		}
	}

}
