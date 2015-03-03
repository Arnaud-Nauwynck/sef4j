package com.google.code.joto.ui.filter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.util.ui.IconUtils;
import com.google.code.joto.util.ui.JButtonUtils;

/**
 * simple swing Table Panel containing RecordEventFilter table + detailed viewer/editor
 *
 */
public class RecordEventFilterFileTablePanel {

	private static Logger log = LoggerFactory.getLogger(RecordEventFilterFileTablePanel.class);
	
	private JPanel panel;

	private RecordEventFilterFileTableModel filterFilesTableModel;
	
	private JPanel filterFilesTablePane;
	private JPanel filterFilesTableToolbar;
	private JScrollPane filterFilesTableScrollPane;
	private JTable filterFilesTable;
	
	private JButton editFilterFileButton;
	private JButton newFilterFileButton;
	private JButton deleteFilterFileButton;
	private JButton importFilterFileButton;
	private JButton removeFilterFileButton;
	
	private JButton saveAllFilterFilesButton;
	private JButton reloaAlldFilterFilesButton;

	
	private RecordEventFilterFilesPreferences preferences = new RecordEventFilterFilesPreferences(); 
	
	// ------------------------------------------------------------------------

	public RecordEventFilterFileTablePanel(RecordEventFilterFileTableModel tableModel) {
		this.filterFilesTableModel = tableModel;
		initComponents();
	}

	private void initComponents() {
		panel = new JPanel(new BorderLayout());
		
		filterFilesTablePane = new JPanel(new BorderLayout());
		filterFilesTable = new JTable(filterFilesTableModel);
		filterFilesTableScrollPane = new JScrollPane(filterFilesTable);
		filterFilesTablePane.add(filterFilesTableScrollPane, BorderLayout.CENTER);

		filterFilesTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					openSelectedFilterFileEditors();
				}
			}
		});
		
		panel.add(filterFilesTablePane, BorderLayout.CENTER);
		filterFilesTable.setPreferredScrollableViewportSize(new Dimension(600, 200));
		
		{
			// toolbar buttons for Add / Remove .. in table
			filterFilesTableToolbar = new JPanel(new FlowLayout());
			filterFilesTablePane.add(filterFilesTableToolbar, BorderLayout.SOUTH);

			editFilterFileButton = JButtonUtils.snew(IconUtils.eclipseGif.get("edit"), "Edit", this, "onEditFilterButton");
			filterFilesTableToolbar.add(editFilterFileButton);
		
			newFilterFileButton = JButtonUtils.snew(IconUtils.eclipseGif.get("new"), "New", this, "onNewFilterButton");
			filterFilesTableToolbar.add(newFilterFileButton);
		
			deleteFilterFileButton = JButtonUtils.snew(IconUtils.eclipseGif.get("delete"), "Delete", this, "onDeleteFilterButton");
			filterFilesTableToolbar.add(deleteFilterFileButton);
	
			importFilterFileButton = JButtonUtils.snew(IconUtils.eclipseGif.get("add"), "Import ...", this, "onImportFilterFileButton");
			filterFilesTableToolbar.add(importFilterFileButton);
			
			removeFilterFileButton = JButtonUtils.snew(IconUtils.eclipseGif.get("remove"), "Remove", this, "onRemoveFilterFileButton");
			filterFilesTableToolbar.add(removeFilterFileButton);

			saveAllFilterFilesButton = JButtonUtils.snew(IconUtils.eclipseGif.get("saveAll"), "Save All Files", this, "onSaveAllFilterFilesButton");
			filterFilesTableToolbar.add(saveAllFilterFilesButton);

			reloaAlldFilterFilesButton = JButtonUtils.snew(IconUtils.eclipseGif.get("reloadAll"), "Reload All Files", this, "onReloadAllFilterFilesButton");
			filterFilesTableToolbar.add(reloaAlldFilterFilesButton);

		}
		
	}

	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return panel;
	}


	public RecordEventFilterFilesPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(RecordEventFilterFilesPreferences p) {
		this.preferences = p;
	}
	

	private List<RecordEventFilterFile> getSelectedItems() {
		List<RecordEventFilterFile> items = new ArrayList<RecordEventFilterFile>();
		int[] selectedViewRows = filterFilesTable.getSelectedRows();
		if (selectedViewRows != null && selectedViewRows.length != 0) {
			for(int viewRow : selectedViewRows) {
				int modelRow = viewRow; // no conversion model->view for sort yet?
				RecordEventFilterFile item = filterFilesTableModel.getRow(modelRow);
				if (item != null) {
					items.add(item);
				}
			}
		}
		return items;
	}

	public List<RecordEventFilterFile> loadFilterFiles(File[] filterFiles,  
			StringBuilder errorsText, List<File> errorFiles) {
		List<RecordEventFilterFile> res = new ArrayList<RecordEventFilterFile>();
		if (filterFiles != null && filterFiles.length != 0) {
			for(File f : filterFiles) {
				try {
					RecordEventFilterFile filter = loadFilterFile(f);
					res.add(filter);
				} catch(Exception ex) {
					errorsText.append("failed to import file " + f.getAbsolutePath() + " : " + ex.getMessage());
					errorFiles.add(f);
				}
			}
		}
		return res;
	}

	
	public RecordEventFilterFile loadFilterFile(File f) {
		RecordEventFilterFile res = new RecordEventFilterFile();
		res.setPersistentFile(f);
		// RecordEventFilterFileUtils
		return res;
	}
	
	public void openFilterFileEditor(RecordEventFilterFile filterFile) {
		RecordEventFilterFileEditor editor = new RecordEventFilterFileEditor();
		editor.setModel(filterFile);
		JFrame frame = new JFrame("Edit FilterFile");
		frame.getContentPane().add(editor.getJComponent());
		frame.pack();
		frame.setVisible(true);
	}

	protected void openSelectedFilterFileEditors() {
		List<RecordEventFilterFile> items = getSelectedItems();
		for(RecordEventFilterFile item : items) {
			openFilterFileEditor(item);
		}
	}

	// UI Callbacks
	// ------------------------------------------------------------------------

	public void onEditFilterButton(ActionEvent event) {
		openSelectedFilterFileEditors();
	}
	
	public void onNewFilterButton(ActionEvent event) {
		RecordEventFilterFile item = new RecordEventFilterFile();
		
		// generate a new name and file name (?)
		
		
		filterFilesTableModel.addRow(item);
		openFilterFileEditor(item);
	}

	public void onDeleteFilterButton(ActionEvent event) {
		List<RecordEventFilterFile> items = getSelectedItems();
		for(RecordEventFilterFile item : items) {
			filterFilesTableModel.removeRow(item);
			// also delete file (rename as ".old") !
			if (item.getPersistentFile() != null && item.getPersistentFile().exists()) {
				File destOldFile = new File(item.getPersistentFile().getAbsolutePath() + ".old"); 
				if (destOldFile.exists()) {
					destOldFile.delete(); // do not backup old file ?
				}
				item.getPersistentFile().renameTo(destOldFile);
			}
		}
	}

	public void onImportFilterFileButton(ActionEvent event) {
		JFileChooser dlg = new JFileChooser();
		if (preferences != null) {
			File dir = preferences.getBaseDir();
			if (dir != null) {
				dlg.setCurrentDirectory(dir);
			}
			String suffix = preferences.getFileSuffix();
			if (suffix != null) {
				dlg.setFileFilter(new FileNameExtensionFilter("event filters", suffix));
			}
		}
		dlg.setFileSelectionMode(JFileChooser.FILES_ONLY);
		dlg.setMultiSelectionEnabled(true);
		if (dlg.showDialog(panel, null) == JFileChooser.APPROVE_OPTION) {
			File[] filterFiles = dlg.getSelectedFiles();
			StringBuilder errorsText = new StringBuilder();
			List<File> errorFiles = new ArrayList<File>();
			List<RecordEventFilterFile> loaded = loadFilterFiles(filterFiles, errorsText, errorFiles);
			filterFilesTableModel.addRows(loaded);
			if (!errorFiles.isEmpty()) {
				String msg = "Failed to import " + errorFiles.size() + " / " + filterFiles.length 
						+ " filter file(s):\n"
						+ errorsText;
				JOptionPane.showMessageDialog(panel, msg, "Failed to import filter file(s)", JOptionPane.WARNING_MESSAGE, null);
			}
		}
	}

	public void onRemoveFilterFileButton(ActionEvent event) {
		List<RecordEventFilterFile> items = getSelectedItems();
		for(RecordEventFilterFile item : items) {
			filterFilesTableModel.removeRow(item);
		}
	}

	public void onSaveAllFilterFilesButton(ActionEvent event) {
		for(RecordEventFilterFile item : filterFilesTableModel.getRows()) {
			File file = item.getPersistentFile();
			if (file != null) {
				RecordEventFilterFileUtils.saveFilterFile(item);
			}
		}
	}

	public void onReloadAllFilterFilesButton(ActionEvent event) {
		for(RecordEventFilterFile item : filterFilesTableModel.getRows()) {
			File file = item.getPersistentFile();
			if (file != null && file.exists()) {
				try {
					RecordEventFilterFileUtils.loadFilterFile(item);
				} catch(Exception ex) {
					log.warn("Failed to reload file " + file + ":" + ex.getMessage() + " .. ignore, no rethrow?");
				}
			} else {
				// filter is not persistent .. not reloaded!
			}
		}
		filterFilesTableModel.fireTableDataChanged(); // implicit from PropChangeListener?
	}
	
}
