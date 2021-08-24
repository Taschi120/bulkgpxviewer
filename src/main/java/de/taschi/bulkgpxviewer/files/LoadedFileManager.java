package de.taschi.bulkgpxviewer.files;

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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.taschi.bulkgpxviewer.geo.GpxFile;
import io.jenetics.jpx.GPX;

/**
 * Singleton class for access to the loaded files and GPX tracks
 */
public class LoadedFileManager {
	
	private static final Logger LOG = LogManager.getLogger(LoadedFileManager.class);

	private static LoadedFileManager INSTANCE;

	private final List<GpxFile> loadedTracks = new ArrayList<>();
	
	private final List<LoadedFileChangeListener> changeListeners = new LinkedList<>();
	
	private GpxFile highlightedTrack = null;

	private Path loadedDirectory;

	public static LoadedFileManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LoadedFileManager();
		}
		return INSTANCE;
	}

	/**
	 * Load one GPX file
	 * @param file
	 */
	private void loadFile(Path file) {
		LOG.info("Loading GPX file " + file.toString()); //$NON-NLS-1$
		
		try {
			var gpx = GPX.read(file);		
			var track = new GpxFile(file, gpx);
			loadedTracks.add(track);
			
			LOG.info("GPX file " + file.toString() + " has been successfully loaded."); //$NON-NLS-1$ //$NON-NLS-2$
			
			fireChangeListeners();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void fireChangeListeners() {
		SwingUtilities.invokeLater(() -> {
			for (LoadedFileChangeListener listener: changeListeners) {
				listener.onLoadedFileChange();
			}
		});
	}

	/**
	 * Remove all currently loaded GPX files and load all files from the given directory
	 * @param directory
	 */
	public synchronized void clearAndLoadAllFromDirectory(Path directory) throws IOException {		
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.gpx"); //$NON-NLS-1$

		loadedTracks.clear();
		
		try {
			Files.walk(directory)
				.filter((it) -> matcher.matches(it))
				.forEach((it) -> loadFile(it));

			loadedDirectory = directory;
			new Thread(() -> fireChangeListeners()).start();
		} catch (RuntimeException e) {
			if (e.getCause() != null && e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			} else {
				throw e;
			}
		}
	}
	
	/**
	 * Add a file change listener
	 * @param listener
	 */
	public void addChangeListener(LoadedFileChangeListener listener) {
		changeListeners.add(listener);
	}
	
	/**
	 * Remove a file change listener
	 * @param listener
	 */
	public void removeChangeListener(LoadedFileChangeListener listener) {
		changeListeners.remove(listener);
	}

	/**
	 * get all currently loaded tracks
	 * @return
	 */
	public synchronized List<GpxFile> getLoadedTracks() {
		return Collections.unmodifiableList(loadedTracks);
	}
	
	public synchronized void setHighlightedTrack(GpxFile track) {
		highlightedTrack = track;
	}
	
	public synchronized GpxFile getHighlightedTrack() {
		return highlightedTrack;
	}

	/**
	 * Reloads files from current directory
	 */
	public void refresh() throws IOException {
		clearAndLoadAllFromDirectory(loadedDirectory);
	}
}
