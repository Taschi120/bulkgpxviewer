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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxmapviewer.viewer.GeoPosition;

import de.taschi.bulkgpxviewer.gpx.GpxToJxMapper;
import de.taschi.bulkgpxviewer.gpx.GpxViewerTrack;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

/**
 * Singleton class for access to the loaded files and GPX tracks
 */
public class LoadedFileManager {
	
	private static final Logger LOG = LogManager.getLogger(LoadedFileManager.class);

	private static LoadedFileManager INSTANCE;

	private final List<GpxViewerTrack> loadedTracks = new ArrayList<>();
	
	private final List<LoadedFileChangeListener> changeListeners = new LinkedList<>();

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
			List<WayPoint> waypoints = GPX.read(file).tracks()
					.flatMap(Track::segments)
					.flatMap(TrackSegment::points)
					.collect(Collectors.toList());
			
			List<GeoPosition> positions = GpxToJxMapper.getInstance().waypointsToGeoPositions(waypoints);
			
			GpxViewerTrack track = new GpxViewerTrack(positions);
			track.setFileName(file);
			if (!waypoints.isEmpty()) {
				track.setStartedAt(waypoints.get(0).getInstant().orElse(null));
			}
			
			loadedTracks.add(track);
			
			LOG.info("GPX file " + file.toString() + " has been successfully loaded."); //$NON-NLS-1$ //$NON-NLS-2$
			
			new Thread(() -> fireChangeListeners()).start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void fireChangeListeners() {
		for (LoadedFileChangeListener listener: changeListeners) {
			listener.onLoadedFileChange();
		}
	}

	/**
	 * Remove all currently loaded GPX files and load all files from the given directory
	 * @param directory
	 */
	public void clearAndLoadAllFromDirectory(Path directory) throws IOException {
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.gpx"); //$NON-NLS-1$

		loadedTracks.clear();
		
		try {
			Files.walk(directory)
				.filter((it) -> matcher.matches(it))
				.forEach((it) -> loadFile(it));

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
	public List<GpxViewerTrack> getLoadedTracks() {
		return loadedTracks;
	}
	
}
