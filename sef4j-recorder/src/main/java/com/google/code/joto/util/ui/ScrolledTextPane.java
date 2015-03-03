package com.google.code.joto.util.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ScrolledTextPane {

	private static Logger log = LoggerFactory.getLogger(ScrolledTextPane.class);
	
	private JPanel mainPanel;
	private JScrollPane scrollPane;
	private JTextPane textPane;
	
	private JToolBar southToolBar;
	
	//-------------------------------------------------------------------------

	public ScrolledTextPane() {
		mainPanel = new JPanel(new BorderLayout());
		textPane = new JTextPane();
		scrollPane = new JScrollPane(textPane, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setPreferredScrollableSize(new Dimension(350, 550));
		mainPanel.add(BorderLayout.CENTER, scrollPane);
		
		southToolBar = new JToolBar();
		mainPanel.add(BorderLayout.SOUTH, southToolBar);
		
		JButton clearButton = new JButton("clear");
		JButton copyToClipboardButton = new JButton("copy");
		southToolBar.add(clearButton);
		southToolBar.add(copyToClipboardButton);
		
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearText();
			}
		});
		copyToClipboardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copyToClipboard();
			}
		});
	}

	//-------------------------------------------------------------------------

	public JComponent getJComponent() {
		return mainPanel;
	}
	
	public JToolBar getToolbar() {
		return southToolBar;
	}
	
	public void setPreferredScrollableSize(Dimension preferredSize) {
		scrollPane.setPreferredSize(preferredSize);
	}
	
	public void clearText() {
		try {
			Document doc = textPane.getDocument();
			doc.remove(0, doc.getLength());
		} catch(Exception ex) {
			log.warn("failed to clear text", ex);
		}
	}

	public String getText() {
		return textPane.getText();
	}

	public void setText(String p) {
		textPane.setText(p);
	}

	public void scrollToStart() {
		scrollPane.scrollRectToVisible(new Rectangle(0, 0, 0, 0));
	}
	
	public void copyToClipboard() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection contents = new StringSelection(getText());
		clipboard.setContents(contents, contents);
	}

	// ------------------------------------------------------------------------
	
	public void addToolbarComp(JComponent comp) {
		southToolBar.add(comp);
	}
	
	public JButton createInsertTextButton(String label, String textToInsert) {
		JButton res = new JButton(label);
		res.addActionListener(createInsertTextActionListener(textToInsert));
		return res;
	}

	public JMenuItem createInsertTextMenuItem(String label, String textToInsert) {
		JMenuItem res = new JMenuItem(label);
		res.addActionListener(createInsertTextActionListener(textToInsert));
		return res;
	}

	public ActionListener createInsertTextActionListener(String textToInsert) {
		return new InsertTextActionListener(textPane, textToInsert);
	}
	
	public static class InsertTextActionListener implements ActionListener {
		JTextPane textPane;
		String textToInsert;
		public InsertTextActionListener(JTextPane textPane, String textToInsert) {
			super();
			this.textPane = textPane;
			this.textToInsert = textToInsert;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			int pos = textPane.getCaretPosition();
			Document doc = textPane.getDocument();
			if (pos < 0) {
				pos = doc.getLength();
			}
			try {
				doc.insertString(pos, textToInsert, null);
			} catch (BadLocationException ex) {
				log.error("Failed to insert text " + ex.getMessage() + " ... ignore, no rethrow!");
			}
		}
	}
}
