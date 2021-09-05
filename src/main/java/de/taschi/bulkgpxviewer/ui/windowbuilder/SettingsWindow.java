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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import de.taschi.bulkgpxviewer.settings.ColorConverter;
import de.taschi.bulkgpxviewer.settings.SettingsManager;
import de.taschi.bulkgpxviewer.ui.ColorListItemRenderer;
import de.taschi.bulkgpxviewer.ui.Messages;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import de.taschi.bulkgpxviewer.settings.dto.Settings;
import de.taschi.bulkgpxviewer.settings.dto.UnitSystem;

public class SettingsWindow extends JDialog {
	
	private static final long serialVersionUID = 1202795436996433035L;

	private List<Color> colorsIntl;

	private final JPanel contentPanel = new JPanel();
	private JList<Color> list;
	
	private MainWindow mainWindow;
	private JComboBox<?> unitSystem;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SettingsWindow dialog = new SettingsWindow(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SettingsWindow(MainWindow mainWindow) {
		super(mainWindow.getFrame());
		setTitle(Messages.getString("SettingsWindow.Settings")); //$NON-NLS-1$
		this.mainWindow = mainWindow;
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				initModel();
			}
		});
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
				panel.setBorder(new EmptyBorder(5, 5, 5, 5));
				tabbedPane.addTab(Messages.getString("SettingsWindow.GeneralTab"), null, panel, null); //$NON-NLS-1$
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.columnWidths = new int[] {0, 0};
				gbl_panel.rowHeights = new int[]{0, 0};
				gbl_panel.columnWeights = new double[]{0.0, 1.0};
				gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
				panel.setLayout(gbl_panel);
				{
					JLabel lblNewLabel = new JLabel(Messages.getString("SettingsWindow.Units")); //$NON-NLS-1$
					GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
					gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
					gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
					gbc_lblNewLabel.gridx = 0;
					gbc_lblNewLabel.gridy = 0;
					panel.add(lblNewLabel, gbc_lblNewLabel);
				}
				{
					unitSystem = new JComboBox<>();
					unitSystem.setModel(new DefaultComboBoxModel(new String[] {Messages.getString("SettingsWindow.Metric"), Messages.getString("SettingsWindow.Imperial")})); //$NON-NLS-1$ //$NON-NLS-2$
					GridBagConstraints gbc_unitSystem = new GridBagConstraints();
					gbc_unitSystem.fill = GridBagConstraints.HORIZONTAL;
					gbc_unitSystem.gridx = 1;
					gbc_unitSystem.gridy = 0;
					panel.add(unitSystem, gbc_unitSystem);
				}
			}
			{
				JPanel panel = new JPanel();
				tabbedPane.addTab(Messages.getString("SettingsWindow.TrailColors"), null, panel, null); //$NON-NLS-1$
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.columnWidths = new int[]{0, 0};
				gbl_panel.rowHeights = new int[]{0, 0, 0};
				gbl_panel.columnWeights = new double[]{1.0, 0.0};
				gbl_panel.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
				panel.setLayout(gbl_panel);
				{
					list = new JList<>();
					list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					GridBagConstraints gbc_list = new GridBagConstraints();
					gbc_list.gridheight = 2;
					gbc_list.insets = new Insets(0, 0, 0, 5);
					gbc_list.fill = GridBagConstraints.BOTH;
					gbc_list.gridx = 0;
					gbc_list.gridy = 0;
					panel.add(list, gbc_list);
				}
				{
					JPanel panel_1 = new JPanel();
					GridBagConstraints gbc_panel_1 = new GridBagConstraints();
					gbc_panel_1.gridheight = 2;
					gbc_panel_1.insets = new Insets(0, 0, 5, 0);
					gbc_panel_1.fill = GridBagConstraints.BOTH;
					gbc_panel_1.gridx = 1;
					gbc_panel_1.gridy = 0;
					panel.add(panel_1, gbc_panel_1);
					GridBagLayout gbl_panel_1 = new GridBagLayout();
					gbl_panel_1.columnWidths = new int[]{89, 89, 0};
					gbl_panel_1.rowHeights = new int[]{23, 0, 0};
					gbl_panel_1.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
					gbl_panel_1.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
					panel_1.setLayout(gbl_panel_1);
					{
						JButton btnNewButton_1 = new JButton(Messages.getString("SettingsWindow.Add")); //$NON-NLS-1$
						btnNewButton_1.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								onAdd();
							}


						});
						GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
						gbc_btnNewButton_1.gridwidth = 2;
						gbc_btnNewButton_1.fill = GridBagConstraints.HORIZONTAL;
						gbc_btnNewButton_1.anchor = GridBagConstraints.NORTH;
						gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 5);
						gbc_btnNewButton_1.gridx = 0;
						gbc_btnNewButton_1.gridy = 0;
						panel_1.add(btnNewButton_1, gbc_btnNewButton_1);
					}
					{
						JButton btnNewButton = new JButton(Messages.getString("SettingsWindow.Remove")); //$NON-NLS-1$
						btnNewButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								onRemove();
							}

						});
						GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
						gbc_btnNewButton.gridwidth = 2;
						gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
						gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
						gbc_btnNewButton.anchor = GridBagConstraints.NORTH;
						gbc_btnNewButton.gridx = 0;
						gbc_btnNewButton.gridy = 1;
						panel_1.add(btnNewButton, gbc_btnNewButton);
					}
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton(Messages.getString("SettingsWindow.OK")); //$NON-NLS-1$
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onOk();
					}
				});
				okButton.setActionCommand("OK"); //$NON-NLS-1$
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton(Messages.getString("SettingsWindow.Cancel")); //$NON-NLS-1$
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onCancel();
					}
				});
				cancelButton.setActionCommand("Cancel"); //$NON-NLS-1$
				buttonPane.add(cancelButton);
			}
		}
	}
	
	protected void onCancel() {
		setVisible(false);
	}

	protected void onOk() {
		Settings settings = SettingsManager.getInstance().getSettings();
		settings.setRouteColors(ColorConverter.convertToSettings(colorsIntl));
		
		if(unitSystem.getSelectedIndex() == 0) {
			settings.setUnitSystem(UnitSystem.METRIC);
		} else {
			settings.setUnitSystem(UnitSystem.IMPERIAL);
		}
		SettingsManager.getInstance().saveSettings();
		SettingsManager.getInstance().fireNotifications();
		
		setVisible(false);
	}

	private void initModel() {
		Settings settings = SettingsManager.getInstance().getSettings();
		
		colorsIntl = ColorConverter.convertToAwt(settings.getRouteColors());
		getColorList().setCellRenderer(new ColorListItemRenderer());
		getColorList().setListData(new Vector<Color>(colorsIntl));
		
		if (settings.getUnitSystem() == UnitSystem.METRIC) {
			unitSystem.setSelectedIndex(0);
		} else {
			unitSystem.setSelectedIndex(1);
		}
	}
	
	private void onAdd() {
		Color c = ColorChooserDialog.showColorPicker();
		if (c != null) {
			colorsIntl.add(c);
			getColorList().setListData(new Vector<Color>(colorsIntl));
		}
	}


	private void onRemove() {
		Color c = getColorList().getSelectedValue();
		colorsIntl.remove(c);
		getColorList().setListData(new Vector<Color>(colorsIntl));
	}
	
	public JList<Color> getColorList() {
		return list;
	}
}
