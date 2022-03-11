package de.taschi.bulkgpxviewer.ui.windowbuilder

import de.taschi.bulkgpxviewer.ui.Messages
import de.taschi.bulkgpxviewer.ui.windowbuilder.InfoDialog
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.io.File
import java.io.IOException
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
 */   class InfoDialog : JDialog() {
    private val contentPanel = JPanel()
    private var licenseTextPane: JTextPane? = null
    private var thirdPartyTextPane: JTextPane? = null
    private var licenseScrollPane: JScrollPane? = null
    private var thirdPartyScrollPane: JScrollPane? = null

    /**
     * Create the dialog.
     */
    init {
        title = Messages.getString("InfoDialog.Info") //$NON-NLS-1$
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
                tabbedPane.addTab(Messages.getString("InfoDialog.AboutTab"), null, panel, null) //$NON-NLS-1$
                run {
                    val lblNewLabel = JLabel(Messages.getString("InfoDialog.2")) //$NON-NLS-1$
                    panel.add(lblNewLabel)
                }
            }
            run {
                val panel = JPanel()
                tabbedPane.addTab(Messages.getString("InfoDialog.LicenseTab"), null, panel, null) //$NON-NLS-1$
                panel.layout = BorderLayout(0, 0)
                run {
                    licenseScrollPane = JScrollPane()
                    panel.add(licenseScrollPane, BorderLayout.CENTER)
                    run {
                        licenseTextPane = JTextPane()
                        licenseScrollPane!!.setViewportView(licenseTextPane)
                    }
                }
            }
            run {
                val panel = JPanel()
                tabbedPane.addTab(Messages.getString("InfoDialog.ThirdPartyTab"), null, panel, null) //$NON-NLS-1$
                panel.layout = BorderLayout(0, 0)
                run {
                    val lblBulkGpxViewer = JLabel(Messages.getString("InfoDialog.ThirdPartyLabel")) //$NON-NLS-1$
                    panel.add(lblBulkGpxViewer, BorderLayout.NORTH)
                }
                run {
                    thirdPartyScrollPane = JScrollPane()
                    panel.add(thirdPartyScrollPane, BorderLayout.CENTER)
                    run {
                        thirdPartyTextPane = JTextPane()
                        thirdPartyScrollPane!!.setViewportView(thirdPartyTextPane)
                    }
                }
            }
        }
        run {
            val buttonPane = JPanel()
            buttonPane.layout = FlowLayout(FlowLayout.RIGHT)
            contentPane.add(buttonPane, BorderLayout.SOUTH)
            run {
                val okButton = JButton(Messages.getString("InfoDialog.OK")) //$NON-NLS-1$
                okButton.actionCommand = Messages.getString("InfoDialog.OK") //$NON-NLS-1$
                buttonPane.add(okButton)
                getRootPane().defaultButton = okButton
                okButton.addActionListener { evt: ActionEvent -> this.okEventHandler(evt) }
            }
        }
        loadTexts()
    }

    private fun loadTexts() {
        try {
            val license = FileUtils.readFileToString(File("COPYING"), "UTF-8") //$NON-NLS-1$ //$NON-NLS-2$
            val thirdParty = FileUtils.readFileToString(File("THIRD-PARTY.txt"), "UTF-8") //$NON-NLS-1$ //$NON-NLS-2$
            licenseTextPane!!.text = license
            thirdPartyTextPane!!.text = thirdParty
            licenseScrollPane!!.verticalScrollBar.value = 0
            thirdPartyScrollPane!!.verticalScrollBar.value = 0
        } catch (e: IOException) {
            LOG.error("Couldn't load license or third-party licenses", e) //$NON-NLS-1$
        }
    }

    private fun okEventHandler(evt: ActionEvent) {
        isVisible = false
    }

    companion object {
        private val LOG = LogManager.getLogger(InfoDialog::class.java)
        private const val serialVersionUID = -8478805153020789079L

        /**
         * Launch the application.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                val dialog = InfoDialog()
                dialog.defaultCloseOperation = DISPOSE_ON_CLOSE
                dialog.isVisible = true
            } catch (e: Exception) {
                LOG.error(e)
            }
        }
    }
}