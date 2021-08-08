package de.taschi.bulkgpxviewer.gpx;

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
import java.util.List;
import java.util.stream.Collectors;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

public class GpxFileUtil {
	private static GpxFileUtil INSTANCE = null;
		
	public static GpxFileUtil getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GpxFileUtil();
		}
		return INSTANCE;
	}

	private GpxFileUtil() {
		super();
	}

	public List<WayPoint> loadOneGpx(Path path) {
		System.out.println("Loading GPX file " + path.toString());

		
		try {
			return GPX.read(path).tracks()
					.flatMap(Track::segments)
					.flatMap(TrackSegment::points)
					.collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<List<WayPoint>> loadAllGpxInFolder(Path path) throws IOException {
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.gpx");
		
		try {
			return Files.walk(path)
				.filter((it) -> matcher.matches(it))
				.map((it) -> loadOneGpx(it))
				.collect(Collectors.toList());
			
			
		} catch (RuntimeException e) {
			if (e.getCause() != null && e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			} else {
				throw e;
			}
		}
	}

}
