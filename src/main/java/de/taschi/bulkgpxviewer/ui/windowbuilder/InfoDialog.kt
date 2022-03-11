package de.taschi.bulkgpxviewer.ui.windowbuilder;

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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.taschi.bulkgpxviewer.ui.Messages;

import javax.swing.JScrollPane;

public class InfoDialog extends JDialog {
	
	private static final Logger LOG = LogManager.getLogger(InfoDialog.class);

	private static final long serialVersionUID = -8478805153020789079L;
	
	private final JPanel contentPanel = new JPanel();
	private JTextPane licenseTextPane;
	private JTextPane thirdPartyTextPane;
	private JScrollPane licenseScrollPane;
	private JScrollPane thirdPartyScrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			InfoDialog dialog = new InfoDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	/**
	 * Create the dialog.
	 */
	public InfoDialog() {
		setTitle(Messages.getString("InfoDialog.Info")); //$NON-NLS-1$
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			contentPanel.add(tabbedPane);
			{
				JPanel panel = new JPanel();
				tabbedPane.addTab(Messages.getString("InfoDialog.AboutTab"), null, panel, null); //$NON-NLS-1$
				{
					JLabel lblNewLabel = new JLabel(Messages.getString("InfoDialog.2")); //$NON-NLS-1$
					panel.add(lblNewLabel);
				}
			}
			{
				JPanel panel = new JPanel();
				tabbedPane.addTab(Messages.getString("InfoDialog.LicenseTab"), null, panel, null); //$NON-NLS-1$
				panel.setLayout(new BorderLayout(0, 0));
				{
					licenseScrollPane = new JScrollPane();
					panel.add(licenseScrollPane, BorderLayout.CENTER);
					{
						licenseTextPane = new JTextPane();
						licenseScrollPane.setViewportView(licenseTextPane);
					}
				}
			}
			{
				JPanel panel = new JPanel();
				tabbedPane.addTab(Messages.getString("InfoDialog.ThirdPartyTab"), null, panel, null); //$NON-NLS-1$
				panel.setLayout(new BorderLayout(0, 0));
				{
					JLabel lblBulkGpxViewer = new JLabel(Messages.getString("InfoDialog.ThirdPartyLabel")); //$NON-NLS-1$
					panel.add(lblBulkGpxViewer, BorderLayout.NORTH);
				}
				{
					thirdPartyScrollPane = new JScrollPane();
					panel.add(thirdPartyScrollPane, BorderLayout.CENTER);
					{
						thirdPartyTextPane = new JTextPane();
						thirdPartyScrollPane.setViewportView(thirdPartyTextPane);
					}
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton(Messages.getString("InfoDialog.OK")); //$NON-NLS-1$
				okButton.setActionCommand(Messages.getString("InfoDialog.OK")); //$NON-NLS-1$
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				
				okButton.addActionListener(this::okEventHandler);
			}
		}
		
		loadTexts();
	}
	
	private void loadTexts() {
		try {
			String license = FileUtils.readFileToString(new File("COPYING"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
			String thirdParty = FileUtils.readFileToString(new File("THIRD-PARTY.txt"), "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
			
			
			licenseTextPane.setText(license);
			thirdPartyTextPane.setText(thirdParty);
			
			licenseScrollPane.getVerticalScrollBar().setValue(0);
			thirdPartyScrollPane.getVerticalScrollBar().setValue(0);
		} catch (IOException e) {
			LOG.error("Couldn't load license or third-party licenses", e); //$NON-NLS-1$
		}
	}
	
	private void okEventHandler(ActionEvent evt) {
		setVisible(false);
	}

}
