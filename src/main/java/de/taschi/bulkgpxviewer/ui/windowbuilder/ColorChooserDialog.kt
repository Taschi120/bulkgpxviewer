package de.taschi.bulkgpxviewer.ui.windowbuilder

import de.taschi.bulkgpxviewer.ui.Messages
import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JColorChooser
import javax.swing.JDialog
import javax.swing.JPanel
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
 */   class ColorChooserDialog constructor() : JDialog() {
    private val contentPanel: JPanel = JPanel()
    protected var colorChooser: JColorChooser? = null

    private enum class ReturnCode {
        OK, CANCEL
    }

    private var rc: ReturnCode? = null

    /**
     * Create the dialog.
     */
    init {
        setTitle(Messages.getString("ColorChooserDialog.SelectColor")) //$NON-NLS-1$
        setModal(true)
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)
        setBounds(100, 100, 519, 319)
        getContentPane().setLayout(BorderLayout())
        contentPanel.setLayout(FlowLayout())
        contentPanel.setBorder(EmptyBorder(5, 5, 5, 5))
        getContentPane().add(contentPanel, BorderLayout.CENTER)
        run({
            colorChooser = JColorChooser()
            contentPanel.add(colorChooser)
        })
        run({
            val buttonPane: JPanel = JPanel()
            buttonPane.setLayout(FlowLayout(FlowLayout.RIGHT))
            getContentPane().add(buttonPane, BorderLayout.SOUTH)
            run({
                val okButton: JButton = JButton(Messages.getString("ColorChooserDialog.OK")) //$NON-NLS-1$
                okButton.addActionListener(object : ActionListener {
                    public override fun actionPerformed(e: ActionEvent) {
                        onOk()
                    }
                })
                okButton.setActionCommand("OK") //$NON-NLS-1$
                buttonPane.add(okButton)
                getRootPane().setDefaultButton(okButton)
            })
            run({
                val cancelButton: JButton = JButton(Messages.getString("ColorChooserDialog.Cancel")) //$NON-NLS-1$
                cancelButton.addActionListener(object : ActionListener {
                    public override fun actionPerformed(e: ActionEvent) {
                        onCancel()
                    }
                })
                cancelButton.setActionCommand("Cancel") //$NON-NLS-1$
                buttonPane.add(cancelButton)
            })
        })
        pack()
    }

    protected fun onCancel() {
        rc = ReturnCode.CANCEL
        setVisible(false)
    }

    private fun onOk() {
        rc = ReturnCode.OK
        setVisible(false)
    }

    companion object {
        private val serialVersionUID: Long = 1659785303836654966L

        /**
         * Launch the application.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                val dialog: ColorChooserDialog = ColorChooserDialog()
                dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE)
                dialog.setVisible(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun showColorPicker(): Color? {
            val c: ColorChooserDialog = ColorChooserDialog()
            c.setVisible(true)
            // dialog is modal, wait for close
            if (c.rc == ReturnCode.OK) {
                return c.colorChooser!!.getColor()
            } else {
                return null
            }
        }
    }
}