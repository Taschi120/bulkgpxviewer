package de.taschi.bulkgpxviewer.ui.windowbuilder

import com.google.inject.Inject
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.files.GpxFile
import de.taschi.bulkgpxviewer.files.LoadedFileManager
import de.taschi.bulkgpxviewer.settings.SettingsManager
import de.taschi.bulkgpxviewer.settings.SettingsUpdateListener
import de.taschi.bulkgpxviewer.ui.IconHandler
import de.taschi.bulkgpxviewer.ui.Messages
import de.taschi.bulkgpxviewer.ui.graphs.HeightProfilePanel
import de.taschi.bulkgpxviewer.ui.graphs.SpeedOverTimePanel
import de.taschi.bulkgpxviewer.ui.map.MapPanel
import de.taschi.bulkgpxviewer.ui.sidepanel.SidePanel
import io.jenetics.jpx.WayPoint
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.io.IOException
import java.net.URI
import java.util.*
import javax.imageio.ImageIO
import javax.swing.*

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

class MainWindow : SettingsUpdateListener {
    var frame: JFrame? = null
        private set
    private var splitPane: JSplitPane? = null
    private var openFolderMenuItem: JMenuItem? = null
    private var settingsMenuItem: JMenuItem? = null
    private var quitMenuItem: JMenuItem? = null
    private var openGithubMenuItem: JMenuItem? = null
    private var aboutMenuItem: JMenuItem? = null
    private var mapPanel: MapPanel? = null
    private val BASE_TITLE = Messages.getString("MainWindow.WindowTitle") //$NON-NLS-1$
    private var sidePanel: SidePanel? = null
    private var currentMode = MainWindowMode.BULK_DISPLAY
    private var leftSideRootPanel: JPanel? = null
    private var rightSideRootPanel: JPanel? = null
    private var splitPane_1: JSplitPane? = null
    private var tabbedPane: JTabbedPane? = null
    private var editingPanel: EditingPanelWrapper? = null
    private var speedOverTimePanel: SpeedOverTimePanel? = null
    private var heightProfilePanel: HeightProfilePanel? = null

    @Inject
    private val settingsManager: SettingsManager? = null

    @Inject
    private val loadedFileManager: LoadedFileManager? = null

    /**
     * Create the application.
     */
    init {
        Application.getInjector().injectMembers(this)
        initialize()
    }

    /**
     * Initialize the contents of the frame.
     */
    private fun initialize() {
        frame = JFrame()
        frame!!.title = Messages.getString("MainWindow.WindowTitle") //$NON-NLS-1$
        frame!!.setBounds(100, 100, 710, 481)
        frame!!.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        splitPane = JSplitPane()
        splitPane!!.resizeWeight = 0.75
        splitPane!!.isOneTouchExpandable = true
        frame!!.contentPane.add(splitPane, BorderLayout.CENTER)
        leftSideRootPanel = JPanel()
        splitPane!!.leftComponent = leftSideRootPanel
        leftSideRootPanel!!.layout = BorderLayout(0, 0)
        splitPane_1 = JSplitPane()
        splitPane_1!!.orientation = JSplitPane.VERTICAL_SPLIT
        splitPane_1!!.isOneTouchExpandable = true
        splitPane_1!!.resizeWeight = 0.75
        leftSideRootPanel!!.add(splitPane_1, BorderLayout.CENTER)
        mapPanel = MapPanel()
        splitPane_1!!.leftComponent = mapPanel
        tabbedPane = JTabbedPane(JTabbedPane.TOP)
        splitPane_1!!.rightComponent = tabbedPane
        editingPanel = EditingPanelWrapper()
        tabbedPane!!.addTab(
            Messages.getString("MainWindow.editingPanel_1.title"),
            null,
            editingPanel,
            null
        ) //$NON-NLS-1$
        speedOverTimePanel = SpeedOverTimePanel()
        tabbedPane!!.addTab(
            Messages.getString("MainWindow.speedOverTimePanel.title"),
            null,
            speedOverTimePanel,
            null
        ) //$NON-NLS-1$
        heightProfilePanel = HeightProfilePanel()
        tabbedPane!!.addTab(
            Messages.getString("MainWindow.heightProfilePanel.title"),
            null,
            heightProfilePanel,
            null
        ) //$NON-NLS-1$
        mapPanel!!.selectionHandler
            .addSelectionChangeListener { selection: Set<WayPoint?>? -> editingPanel!!.setSelection(selection) }
        mapPanel!!.autoSetZoomAndLocation()
        rightSideRootPanel = JPanel()
        splitPane!!.rightComponent = rightSideRootPanel
        rightSideRootPanel!!.layout = BorderLayout(0, 0)
        sidePanel = SidePanel()
        rightSideRootPanel!!.add(sidePanel)
        val menuBar = JMenuBar()
        frame!!.jMenuBar = menuBar
        val mnNewMenu = JMenu(Messages.getString("MainWindow.FileMenu")) //$NON-NLS-1$
        menuBar.add(mnNewMenu)
        openFolderMenuItem = JMenuItem(Messages.getString("MainWindow.OpenFolderMenuItem")) //$NON-NLS-1$
        openFolderMenuItem!!.addActionListener { evt: ActionEvent -> openFolderEventHandler(evt) }
        openFolderMenuItem!!.icon = IconHandler.loadIcon("folder-open-line") //$NON-NLS-1$
        mnNewMenu.add(openFolderMenuItem)
        settingsMenuItem = JMenuItem(Messages.getString("MainWindow.SettingsMenuItem")) //$NON-NLS-1$
        settingsMenuItem!!.addActionListener { evt: ActionEvent -> showSettingsWindowEventHandler(evt) }
        settingsMenuItem!!.icon = IconHandler.loadIcon("settings-5-line") //$NON-NLS-1$
        mnNewMenu.add(settingsMenuItem)
        quitMenuItem = JMenuItem(Messages.getString("MainWindow.QuitMenuItem")) //$NON-NLS-1$
        quitMenuItem!!.addActionListener { evt: ActionEvent? -> closeEventHandler(evt) }
        quitMenuItem!!.icon = IconHandler.loadIcon("door-open-line") //$NON-NLS-1$
        mnNewMenu.add(quitMenuItem)
        val mnNewMenu_1 = JMenu(Messages.getString("MainWindow.HelpMenuItem")) //$NON-NLS-1$
        menuBar.add(mnNewMenu_1)
        openGithubMenuItem = JMenuItem(Messages.getString("MainWindow.GithubMenuItem")) //$NON-NLS-1$
        openGithubMenuItem!!.addActionListener { evt: ActionEvent -> openGithubEventHandler(evt) }
        mnNewMenu_1.add(openGithubMenuItem)
        aboutMenuItem = JMenuItem(Messages.getString("MainWindow.AboutMenuItem")) //$NON-NLS-1$
        aboutMenuItem!!.addActionListener { evt: ActionEvent -> aboutEventHandler(evt) }
        aboutMenuItem!!.icon = IconHandler.loadIcon("question-line") //$NON-NLS-1$
        mnNewMenu_1.add(aboutMenuItem)
        restoreWindowGeometry()
        loadAndSetIcon()
        frame!!.addWindowListener(LocalWindowAdapter())
        val lastUsedDirectory = settingsManager!!.settings!!.lastUsedDirectory
        if (!StringUtils.isEmpty(lastUsedDirectory)) {
            setCrawlDirectory(File(lastUsedDirectory))
        }
        mapPanel!!.autoSetZoomAndLocation()
        settingsManager!!.addSettingsUpdateListener(this)
    }

    fun startEditingMode(trackToEdit: GpxFile?) {
        sidePanel!!.setEnabled(false)
        editingPanel!!.startEditingMode()
        editingPanel!!.setTrack(trackToEdit)
        editingPanel!!.setSelectionHandler(mapPanel!!.selectionHandler)
        editingPanel!!.setSelection(emptySet())

        // set up map display
        mapPanel!!.selectionPainter.isEnabled = true
        mapPanel!!.selectionPainter.setTrack(trackToEdit)
        mapPanel!!.routesPainter.setProvider(mapPanel!!.routesPainter.makeSingleTrackProvider(trackToEdit!!))

        // TODO with multiple switches from regular mode to edit mode, there will be multiple event handlers,
        // wasting computing time. Refactor to fix this.
        mapPanel!!.selectionHandler.addSelectionChangeListener { selection: Set<WayPoint?>? -> mapPanel!!.repaint() }

        // enable map listener
        mapPanel!!.selectionHandler.activate(trackToEdit)
        currentMode = MainWindowMode.EDITING
    }

    private fun restoreWindowGeometry() {
        val settings = settingsManager!!.settings!!.mainWindowSettings
        frame!!.setLocation(settings!!.x, settings!!.y)
        frame!!.setSize(1185, 646)
        if (settings!!.isMaximized) {
            frame!!.extendedState = frame!!.extendedState or JFrame.MAXIMIZED_BOTH
        }
    }

    private fun loadAndSetIcon() {
        try {
            val image: Image = ImageIO.read(javaClass.getResource("/icon_256.png")) //$NON-NLS-1$
            frame!!.iconImage = image
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setCrawlDirectory(selectedFile: File) {
        try {
            loadedFileManager!!.clearAndLoadAllFromDirectory(selectedFile.toPath())
            frame!!.title =
                BASE_TITLE + Messages.getString("MainWindow.WindowTitleSpace") + selectedFile.path //$NON-NLS-1$
            settingsManager!!.settings!!.lastUsedDirectory = selectedFile.canonicalPath
        } catch (e: IOException) {
            JOptionPane.showMessageDialog(
                frame,
                Messages.getString("MainWindow.ErrorWhileLoadingGPXFiles") + selectedFile.absolutePath
            ) //$NON-NLS-1$
            LOG.error("Error while loading GPX files from folder " + selectedFile.absolutePath, e) //$NON-NLS-1$
        }
    }

    override fun onSettingsUpdated() {
        mapPanel!!.repaint()
        sidePanel!!.updateModel()
    }

    private fun writeWindowStateToSettings() {
        val settings = settingsManager!!.settings!!.mainWindowSettings
        settings!!.width = frame!!.width
        settings!!.height = frame!!.height
        settings!!.x = frame!!.x
        settings!!.y = frame!!.y
        settings!!.isMaximized = frame!!.extendedState and JFrame.MAXIMIZED_BOTH != 0
    }

    fun setVisible(b: Boolean) {
        frame!!.isVisible = b
    }

    private fun showSettingsWindowEventHandler(evt: ActionEvent) {
        val s = SettingsWindow(this)
        s.isVisible = true
    }

    private fun aboutEventHandler(evt: ActionEvent) {
        InfoDialog().isVisible = true
    }

    private fun openFolderEventHandler(evt: ActionEvent) {
        val chooser = JFileChooser()
        chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        chooser.isVisible = true
        try {
            val rc = chooser.showOpenDialog(frame)
            if (rc == JFileChooser.APPROVE_OPTION) {
                setCrawlDirectory(chooser.selectedFile)
            }
        } catch (e: HeadlessException) {
            throw RuntimeException(e)
        }
    }

    private fun openGithubEventHandler(evt: ActionEvent) {
        try {
            Desktop.getDesktop().browse(URI.create("https://github.com/Taschi120/bulkgpxviewer")) //$NON-NLS-1$
        } catch (e: IOException) {
            LOG.error("Error while trying to open GitHub site in system browser", e) //$NON-NLS-1$
            JOptionPane.showMessageDialog(
                frame,
                Messages.getString("MainWindow.CouldNotOpenBrowser"),
                Messages.getString("MainWindow.Error"),
                JOptionPane.ERROR_MESSAGE
            ) //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private fun closeEventHandler(evt: ActionEvent?) {
        writeWindowStateToSettings()
        settingsManager!!.saveSettings()
        LOG.info("Closing application by user request.") //$NON-NLS-1$
        System.exit(0)
    }

    private inner class LocalWindowAdapter : WindowAdapter() {
        override fun windowClosing(e: WindowEvent) {
            closeEventHandler(null)
        }
    }

    fun exitEditingMode() {
        leftSideRootPanel!!.remove(editingPanel)
        mapPanel!!.selectionHandler.deactivate()
        mapPanel!!.selectionPainter.isEnabled = false
        mapPanel!!.routesPainter.setProvider(mapPanel!!.routesPainter.allRouteProvider)
        editingPanel!!.exitEditingMode()
        sidePanel!!.setEnabled(true)
        frame!!.validate()
        loadedFileManager!!.fireChangeListeners()
        currentMode = MainWindowMode.BULK_DISPLAY
    }

    fun setSelectedGpxFile(file: Optional<GpxFile>) {
        val firstSegment = file.map { it.firstSegment }.orElse(Optional.empty())
        speedOverTimePanel!!.setGpxTrackSegment(firstSegment!!.orElse(null))
        heightProfilePanel!!.setGpxTrackSegment(firstSegment.orElse(null))
        mapPanel!!.selectedFile = file.orElse(null)
    }

    companion object {
        private val LOG = LogManager.getLogger(MainWindow::class.java)

        /**
         * Launch the application.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            EventQueue.invokeLater {
                try {
                    val window = MainWindow()
                    Application.getInjector().injectMembers(window)
                    window.frame!!.isVisible = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}