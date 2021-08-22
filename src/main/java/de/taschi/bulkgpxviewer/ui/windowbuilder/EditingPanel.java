package de.taschi.bulkgpxviewer.ui.windowbuilder;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class EditingPanel extends JPanel {

	private static final long serialVersionUID = 6937011900054488316L;

	/**
	 * Create the panel.
	 */
	public EditingPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JSlider slider = new JSlider();
		add(slider, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		
		JButton btnNewButton = new JButton("Delete from Start to Here");
		panel_1.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Delete from Here to End");
		panel_1.add(btnNewButton_1);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		
		JButton btnNewButton_2 = new JButton("Delete from Here to...");
		panel_2.add(btnNewButton_2);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

	}

}
