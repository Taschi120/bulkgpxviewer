package de.taschi.bulkgpsviewer.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ColorChooserDialog extends JDialog {

	private static final long serialVersionUID = 1659785303836654966L;
	
	private final JPanel contentPanel = new JPanel();
	private JColorChooser colorChooser;
	
	private enum ReturnCode { OK, CANCEL };
	
	private ReturnCode rc;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ColorChooserDialog dialog = new ColorChooserDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ColorChooserDialog() {
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 519, 319);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			colorChooser = new JColorChooser();
			contentPanel.add(colorChooser);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onOk();
					}

				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onCancel();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	protected void onCancel() {
		rc = ReturnCode.CANCEL;
		setVisible(false);
	}

	private void onOk() {
		rc = ReturnCode.OK;
		setVisible(false);
	}

	protected JColorChooser getColorChooser() {
		return colorChooser;
	}
	
	public static Color showColorPicker() {
		ColorChooserDialog c = new ColorChooserDialog();
		c.setVisible(true);
		// dialog is modal, wait for close
		if (c.rc == ReturnCode.OK) {
			return c.getColorChooser().getColor();
		} else {
			return null;
		}
		
	}
}
