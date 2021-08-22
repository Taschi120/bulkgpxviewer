package de.taschi.bulkgpxviewer.ui.map;

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
