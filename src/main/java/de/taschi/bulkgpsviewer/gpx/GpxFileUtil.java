package de.taschi.bulkgpsviewer.gpx;

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
