package de.taschi.bulkgpsviewer.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

public class ColorListItemRenderer extends JLabel implements ListCellRenderer<Color> {

	private static final long serialVersionUID = -5909430684119887421L;

	Border active = BorderFactory.createEtchedBorder();
	Border inactive = BorderFactory.createEmptyBorder();
	
	@Override
	public Component getListCellRendererComponent(JList<? extends Color> list, Color value, int index,
			boolean isSelected, boolean cellHasFocus) {
		
		setText(value.getRed() + ", " + value.getGreen() +  ", " + value.getBlue());
		setForeground(value);
		
		if (cellHasFocus) {
			setBorder(active);
		} else {
			setBorder(inactive);
		}
		
		return this;
	}
}
