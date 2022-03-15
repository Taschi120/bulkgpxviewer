package de.taschi.bulkgpxviewer.files

import com.google.inject.Singleton
import de.taschi.bulkgpxviewer.files.LoadedFileManager
import io.jenetics.jpx.GPX
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import javax.swing.SwingUtilities

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
 * Singleton class for access to the loaded files and GPX tracks
 */
@Singleton
class LoadedFileManager constructor() {
    private val loadedTracks: MutableList<GpxFile> = ArrayList()
    private val changeListeners: MutableList<LoadedFileChangeListener> = LinkedList()

    @get:Synchronized
    @set:Synchronized
    var highlightedTrack: GpxFile? = null
    private var loadedDirectory: Path? = null

    /**
     * Load one GPX file
     * @param file
     */
    private fun loadFile(file: Path) {
        log.info("Loading GPX file $file") //$NON-NLS-1$
        try {
            val gpx = GPX.read(file)
            val track = GpxFile(file, gpx)
            loadedTracks.add(track)
            log.info("GPX file $file has been successfully loaded.") //$NON-NLS-1$ //$NON-NLS-2$
            fireChangeListeners()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun fireChangeListeners() {
        SwingUtilities.invokeLater(Runnable({
            for (listener: LoadedFileChangeListener in changeListeners) {
                listener.onLoadedFileChange()
            }
        }))
    }

    /**
     * Remove all currently loaded GPX files and load all files from the given directory
     * @param directory
     */
    @Synchronized
    @Throws(IOException::class)
    fun clearAndLoadAllFromDirectory(directory: Path?) {
        val matcher: PathMatcher = FileSystems.getDefault().getPathMatcher("glob:**.gpx") //$NON-NLS-1$
        loadedTracks.clear()
        try {
            Files.walk(directory)
                .filter(Predicate({ it: Path? -> matcher.matches(it) }))
                .forEach(Consumer({ it: Path -> loadFile(it) }))
            loadedDirectory = directory
            Thread(Runnable({ fireChangeListeners() })).start()
        } catch (e: RuntimeException) {
            if (e.cause != null && e.cause is IOException) {
                throw (e.cause as IOException?)!!
            } else {
                throw e
            }
        }
    }

    /**
     * Add a file change listener
     * @param listener
     */
    fun addChangeListener(listener: LoadedFileChangeListener) {
        changeListeners.add(listener)
    }

    /**
     * Remove a file change listener
     * @param listener
     */
    fun removeChangeListener(listener: LoadedFileChangeListener) {
        changeListeners.remove(listener)
    }

    /**
     * get all currently loaded tracks
     * @return
     */
    @Synchronized
    fun getLoadedTracks(): List<GpxFile> {
        return Collections.unmodifiableList(loadedTracks)
    }

    /**
     * Reloads files from current directory
     */
    @Throws(IOException::class)
    fun refresh() {
        clearAndLoadAllFromDirectory(loadedDirectory)
    }

    companion object {
        private val log = LoggerFactory.getLogger(LoadedFileManager::class.java)
    }
}