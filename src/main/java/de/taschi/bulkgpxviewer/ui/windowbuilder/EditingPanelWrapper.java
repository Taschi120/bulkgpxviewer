package de.taschi.bulkgpxviewer.ui.windowbuilder;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.taschi.bulkgpxviewer.files.GpxFile;
import de.taschi.bulkgpxviewer.ui.map.MapSelectionHandler;
import io.jenetics.jpx.WayPoint;

/**
 * A wrapper for {@link EditingPanel} which shows or hides that panel as necessary.
 */
public class EditingPanelWrapper extends JPanel {

	private static final long serialVersionUID = 6165429227505288375L;
	
	private EditingPanel editingPanel;
	private JPanel editingInactivePanel;
	
	public EditingPanelWrapper() {
		editingPanel = new EditingPanel();
		
		editingInactivePanel = new JPanel();
		editingInactivePanel.setLayout(new BorderLayout());
		
		JLabel label = new JLabel("Right-click a track and select 'Edit' to start editing.");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		editingInactivePanel.add(label, BorderLayout.CENTER);
		
		this.setLayout(new BorderLayout());
		this.add(editingInactivePanel, BorderLayout.CENTER);
	}
	
	public void setSelection(Set<WayPoint> selection) {
		editingPanel.setSelection(selection);
	}
	
	public void setTrack(GpxFile track) {
		editingPanel.setTrack(track);
	}

	public void setSelectionHandler(MapSelectionHandler selectionHandler) {
		editingPanel.setSelectionHandler(selectionHandler);
	}
	
	public void startEditingMode() {
		remove(editingInactivePanel);
		add(editingPanel, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
	
	public void exitEditingMode() {
		remove(editingPanel);
		add(editingInactivePanel, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
	
}
