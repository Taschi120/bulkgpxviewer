package de.taschi.bulkgpxviewer.ui.windowbuilder

import com.google.inject.Inject
import de.taschi.bulkgpxviewer.Application
import de.taschi.bulkgpxviewer.settings.ColorConverter
import de.taschi.bulkgpxviewer.settings.SettingsManager
import de.taschi.bulkgpxviewer.settings.dto.UnitSystem
import de.taschi.bulkgpxviewer.ui.ColorListItemRenderer
import de.taschi.bulkgpxviewer.ui.Messages
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.*
import javax.swing.*
import javax.swing.border.EmptyBorder

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
class SettingsWindow(mainWindow: MainWindow?) : JDialog(mainWindow!!.frame) {
    private var colorsIntl: MutableList<Color?>? = null
    private val contentPanel = JPanel()
    var colorList: JList<Color?>? = null
    private var unitSystem: JComboBox<*>? = null

    @Inject
    private val settingsManager: SettingsManager? = null

    /**
     * Create the dialog.
     */
    init {
        Application.Companion.getInjector().injectMembers(this)
        title = Messages.getString("SettingsWindow.Settings") //$NON-NLS-1$
        addWindowListener(object : WindowAdapter() {
            override fun windowOpened(e: WindowEvent) {
                initModel()
            }
        })
        setBounds(100, 100, 450, 300)
        contentPane.layout = BorderLayout()
        contentPanel.border = EmptyBorder(5, 5, 5, 5)
        contentPane.add(contentPanel, BorderLayout.CENTER)
        contentPanel.layout = BorderLayout(0, 0)
        run {
            val tabbedPane = JTabbedPane(JTabbedPane.TOP)
            contentPanel.add(tabbedPane)
            run {
                val panel = JPanel()
                panel.border = EmptyBorder(5, 5, 5, 5)
                tabbedPane.addTab(Messages.getString("SettingsWindow.GeneralTab"), null, panel, null) //$NON-NLS-1$
                val gbl_panel = GridBagLayout()
                gbl_panel.columnWidths = intArrayOf(0, 0)
                gbl_panel.rowHeights = intArrayOf(0, 0)
                gbl_panel.columnWeights = doubleArrayOf(0.0, 1.0)
                gbl_panel.rowWeights = doubleArrayOf(0.0, Double.MIN_VALUE)
                panel.layout = gbl_panel
                run {
                    val lblNewLabel = JLabel(Messages.getString("SettingsWindow.Units")) //$NON-NLS-1$
                    val gbc_lblNewLabel = GridBagConstraints()
                    gbc_lblNewLabel.insets = Insets(0, 0, 0, 5)
                    gbc_lblNewLabel.anchor = GridBagConstraints.EAST
                    gbc_lblNewLabel.gridx = 0
                    gbc_lblNewLabel.gridy = 0
                    panel.add(lblNewLabel, gbc_lblNewLabel)
                }
                run {
                    unitSystem = JComboBox<Any>()
                    unitSystem!!.model = DefaultComboBoxModel(
                        arrayOf(
                            Messages.getString("SettingsWindow.Metric"),
                            Messages.getString("SettingsWindow.Imperial")
                        )
                    )

                    val gbc_unitSystem = GridBagConstraints()
                    gbc_unitSystem.fill = GridBagConstraints.HORIZONTAL
                    gbc_unitSystem.gridx = 1
                    gbc_unitSystem.gridy = 0
                    panel.add(unitSystem, gbc_unitSystem)
                }
            }
            run {
                val panel = JPanel()
                tabbedPane.addTab(Messages.getString("SettingsWindow.TrailColors"), null, panel, null) //$NON-NLS-1$
                val gbl_panel = GridBagLayout()
                gbl_panel.columnWidths = intArrayOf(0, 0)
                gbl_panel.rowHeights = intArrayOf(0, 0, 0)
                gbl_panel.columnWeights = doubleArrayOf(1.0, 0.0)
                gbl_panel.rowWeights = doubleArrayOf(1.0, 1.0, Double.MIN_VALUE)
                panel.layout = gbl_panel
                run {
                    this.colorList = JList()
                    colorList!!.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
                    val gbc_list = GridBagConstraints()
                    gbc_list.gridheight = 2
                    gbc_list.insets = Insets(0, 0, 0, 5)
                    gbc_list.fill = GridBagConstraints.BOTH
                    gbc_list.gridx = 0
                    gbc_list.gridy = 0
                    panel.add(this.colorList, gbc_list)
                }
                run {
                    val panel_1 = JPanel()
                    val gbc_panel_1 = GridBagConstraints()
                    gbc_panel_1.gridheight = 2
                    gbc_panel_1.insets = Insets(0, 0, 5, 0)
                    gbc_panel_1.fill = GridBagConstraints.BOTH
                    gbc_panel_1.gridx = 1
                    gbc_panel_1.gridy = 0
                    panel.add(panel_1, gbc_panel_1)
                    val gbl_panel_1 = GridBagLayout()
                    gbl_panel_1.columnWidths = intArrayOf(89, 89, 0)
                    gbl_panel_1.rowHeights = intArrayOf(23, 0, 0)
                    gbl_panel_1.columnWeights = doubleArrayOf(0.0, 0.0, Double.MIN_VALUE)
                    gbl_panel_1.rowWeights = doubleArrayOf(0.0, 0.0, Double.MIN_VALUE)
                    panel_1.layout = gbl_panel_1
                    run {
                        val btnNewButton_1 = JButton(Messages.getString("SettingsWindow.Add")) //$NON-NLS-1$
                        btnNewButton_1.addActionListener(ActionListener { onAdd() })
                        val gbc_btnNewButton_1 = GridBagConstraints()
                        gbc_btnNewButton_1.gridwidth = 2
                        gbc_btnNewButton_1.fill = GridBagConstraints.HORIZONTAL
                        gbc_btnNewButton_1.anchor = GridBagConstraints.NORTH
                        gbc_btnNewButton_1.insets = Insets(0, 0, 5, 5)
                        gbc_btnNewButton_1.gridx = 0
                        gbc_btnNewButton_1.gridy = 0
                        panel_1.add(btnNewButton_1, gbc_btnNewButton_1)
                    }
                    run {
                        val btnNewButton = JButton(Messages.getString("SettingsWindow.Remove")) //$NON-NLS-1$
                        btnNewButton.addActionListener(object : ActionListener {
                            override fun actionPerformed(e: ActionEvent) {
                                onRemove()
                            }
                        })
                        val gbc_btnNewButton = GridBagConstraints()
                        gbc_btnNewButton.gridwidth = 2
                        gbc_btnNewButton.insets = Insets(0, 0, 0, 5)
                        gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL
                        gbc_btnNewButton.anchor = GridBagConstraints.NORTH
                        gbc_btnNewButton.gridx = 0
                        gbc_btnNewButton.gridy = 1
                        panel_1.add(btnNewButton, gbc_btnNewButton)
                    }
                }
            }
        }
        run {
            val buttonPane = JPanel()
            buttonPane.layout = FlowLayout(FlowLayout.RIGHT)
            contentPane.add(buttonPane, BorderLayout.SOUTH)
            run {
                val okButton = JButton(Messages.getString("SettingsWindow.OK")) //$NON-NLS-1$
                okButton.addActionListener(object : ActionListener {
                    override fun actionPerformed(e: ActionEvent) {
                        onOk()
                    }
                })
                okButton.actionCommand = "OK" //$NON-NLS-1$
                buttonPane.add(okButton)
                getRootPane().defaultButton = okButton
            }
            run {
                val cancelButton = JButton(Messages.getString("SettingsWindow.Cancel")) //$NON-NLS-1$
                cancelButton.addActionListener(object : ActionListener {
                    override fun actionPerformed(e: ActionEvent) {
                        onCancel()
                    }
                })
                cancelButton.actionCommand = "Cancel" //$NON-NLS-1$
                buttonPane.add(cancelButton)
            }
        }
    }

    protected fun onCancel() {
        isVisible = false
    }

    protected fun onOk() {
        val settings = settingsManager!!.settings
        settings!!.routeColors = ColorConverter.convertToSettings(colorsIntl!!.filterNotNull())
        if (unitSystem!!.selectedIndex == 0) {
            settings.unitSystem = UnitSystem.METRIC
        } else {
            settings.unitSystem = UnitSystem.IMPERIAL
        }
        settingsManager!!.saveSettings()
        settingsManager.fireNotifications()
        isVisible = false
    }

    private fun initModel() {
        val settings = settingsManager!!.settings
        colorsIntl = ColorConverter.convertToAwt(settings!!.routeColors!!.filterNotNull()).toMutableList()
        colorList!!.cellRenderer = ColorListItemRenderer()
        colorList!!.setListData(Vector(colorsIntl))
        if (settings.unitSystem == UnitSystem.METRIC) {
            unitSystem!!.setSelectedIndex(0)
        } else {
            unitSystem!!.setSelectedIndex(1)
        }
    }

    private fun onAdd() {
        val c = ColorChooserDialog.Companion.showColorPicker()
        if (c != null) {
            colorsIntl!!.add(c)
            colorList!!.setListData(Vector(colorsIntl))
        }
    }

    private fun onRemove() {
        val c = colorList!!.selectedValue
        colorsIntl!!.remove(c)
        colorList!!.setListData(Vector(colorsIntl))
    }

    companion object {
        private const val serialVersionUID = 1202795436996433035L

        /**
         * Launch the application.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                val dialog = SettingsWindow(null)
                dialog.defaultCloseOperation = DISPOSE_ON_CLOSE
                dialog.isVisible = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}