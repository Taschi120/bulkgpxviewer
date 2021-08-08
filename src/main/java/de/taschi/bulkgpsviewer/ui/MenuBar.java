package de.taschi.bulkgpsviewer.ui;

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

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = 3071719473861414914L;

	private MainWindow parent;
	
	public MenuBar(MainWindow parent) {
		this.parent = parent;
		
		{
			JMenu file = new JMenu("Files");
			{
				JMenuItem openFolder = new JMenuItem("Open Folder");
				file.add(openFolder);
				openFolder.addActionListener(evt -> openFolder(evt));
			}
			{
				JMenuItem settings = new JMenuItem("Settings");
				file.add(settings);
				settings.addActionListener(evt -> showSettingsWindow());
			}
			this.add(file);
		}
	}

	private void showSettingsWindow() {
		SettingsWindow s = new SettingsWindow(parent);
		s.setVisible(true);
	}

	private void openFolder(ActionEvent evt) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setVisible(true);
		
		try {
			int rc = chooser.showOpenDialog(parent);
			if (rc == JFileChooser.APPROVE_OPTION) {
				parent.setCrawlDirectory(chooser.getSelectedFile());
			}
		} catch (HeadlessException e) {
			throw new RuntimeException(e);
		}
	}
}
