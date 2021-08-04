package de.taschi.bulkgpsviewer;

import javax.swing.UIManager;

import de.taschi.bulkgpsviewer.ui.MainWindow;

public class Application {		
	public static void main(String[] args) {
		System.out.println("Application startup");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Error while setting system look and feel");
			e.printStackTrace();
		}
		MainWindow mw = new MainWindow();
		mw.setVisible(true);
	}
}
