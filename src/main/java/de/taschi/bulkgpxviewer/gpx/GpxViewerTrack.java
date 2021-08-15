package de.taschi.bulkgpxviewer.gpx;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.jxmapviewer.viewer.GeoPosition;

public class GpxViewerTrack implements List<GeoPosition> {
	
	private List<GeoPosition> internal;
	
	private Instant startedAt;
	private Path fileName;

	public GpxViewerTrack() {
		internal = new ArrayList<>();
	}
	
	public GpxViewerTrack(List<GeoPosition> geoPositions) {
		internal = new ArrayList<>(geoPositions);
	}
	
	/**
	 * Determine route length.
	 * @return
	 */
	public BigDecimal getRouteLengthInKilometers() {
		
		if (internal.size() < 2) {
			return BigDecimal.ZERO;
		}
		
		double result = 0;
		
		for (int i = 0; i < internal.size() - 1; i++) {
			GeoPosition here = get(i);
			GeoPosition there = get(i + 1);
			
			result += HaversineCalculator.getDistance(here, there);
		}
		
		return new BigDecimal(result).setScale(1, RoundingMode.HALF_UP);
	}
	
	// ======================================================================//
	// Eclipse auto-generated getters and setters from here on               //
	// ======================================================================//

	public Instant getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Instant startedAt) {
		this.startedAt = startedAt;
	}

	public Path getFileName() {
		return fileName;
	}

	public void setFileName(Path fileName) {
		this.fileName = fileName;
	}
	
	// ======================================================================//
	// Eclipse auto-generated proxy methods from here on                     //
	// ======================================================================//

	public void forEach(Consumer<? super GeoPosition> action) {
		internal.forEach(action);
	}

	public int size() {
		return internal.size();
	}

	public boolean isEmpty() {
		return internal.isEmpty();
	}

	public boolean contains(Object o) {
		return internal.contains(o);
	}

	public Iterator<GeoPosition> iterator() {
		return internal.iterator();
	}

	public Object[] toArray() {
		return internal.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return internal.toArray(a);
	}

	public boolean add(GeoPosition e) {
		return internal.add(e);
	}

	public boolean remove(Object o) {
		return internal.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return internal.containsAll(c);
	}

	public boolean addAll(Collection<? extends GeoPosition> c) {
		return internal.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends GeoPosition> c) {
		return internal.addAll(index, c);
	}

	public boolean removeAll(Collection<?> c) {
		return internal.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return internal.retainAll(c);
	}

	public void replaceAll(UnaryOperator<GeoPosition> operator) {
		internal.replaceAll(operator);
	}

	public <T> T[] toArray(IntFunction<T[]> generator) {
		return internal.toArray(generator);
	}

	public void sort(Comparator<? super GeoPosition> c) {
		internal.sort(c);
	}

	public void clear() {
		internal.clear();
	}

	public boolean equals(Object o) {
		return internal.equals(o);
	}

	public int hashCode() {
		return internal.hashCode();
	}

	public GeoPosition get(int index) {
		return internal.get(index);
	}

	public GeoPosition set(int index, GeoPosition element) {
		return internal.set(index, element);
	}

	public void add(int index, GeoPosition element) {
		internal.add(index, element);
	}

	public boolean removeIf(Predicate<? super GeoPosition> filter) {
		return internal.removeIf(filter);
	}

	public GeoPosition remove(int index) {
		return internal.remove(index);
	}

	public int indexOf(Object o) {
		return internal.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return internal.lastIndexOf(o);
	}

	public ListIterator<GeoPosition> listIterator() {
		return internal.listIterator();
	}

	public ListIterator<GeoPosition> listIterator(int index) {
		return internal.listIterator(index);
	}

	public List<GeoPosition> subList(int fromIndex, int toIndex) {
		return internal.subList(fromIndex, toIndex);
	}

	public Spliterator<GeoPosition> spliterator() {
		return internal.spliterator();
	}

	public Stream<GeoPosition> stream() {
		return internal.stream();
	}

	public Stream<GeoPosition> parallelStream() {
		return internal.parallelStream();
	}



}
