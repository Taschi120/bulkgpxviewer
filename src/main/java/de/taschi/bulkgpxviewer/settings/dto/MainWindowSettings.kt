package de.taschi.bulkgpxviewer.settings.dto;

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
