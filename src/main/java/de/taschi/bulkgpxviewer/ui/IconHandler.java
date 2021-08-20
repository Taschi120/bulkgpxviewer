package de.taschi.bulkgpxviewer.ui;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class IconHandler {
	public static ImageIcon loadIcon(String name) {
		try {
			ImageIcon result = new ImageIcon(ImageIO.read(ClassLoader.getSystemResource("icons/" + name + ".png")));
			return result;
		} catch (Exception e) {
			log.error("Error while loading icon " + name, e);
			return null;
		}
	}
}
