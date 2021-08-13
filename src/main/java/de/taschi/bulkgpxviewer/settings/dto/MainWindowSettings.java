package de.taschi.bulkgpxviewer.settings.dto;

/**
 * Persistance model for main window's state (size, position, maximized/regular)
 *
 */
public class MainWindowSettings {

	private int x;
	private int y;
	private int height;
	private int width;
	private boolean maximized;
	
	/**
	 * Initialize the object with reasonable default values.
	 */
	public MainWindowSettings() {
		x = 0;
		y = 0;
		width = 800;
		height = 600;
		maximized = false;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public boolean isMaximized() {
		return maximized;
	}
	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}
		
}
