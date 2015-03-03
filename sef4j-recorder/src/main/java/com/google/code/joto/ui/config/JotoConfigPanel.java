package com.google.code.joto.ui.config;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.code.joto.JotoConfig;
import com.google.code.joto.JotoConfigUtils;
import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.util.io.XStreamUtils;
import com.google.code.joto.util.ui.GridBagLayoutFormBuilder;
import com.google.code.joto.util.ui.JButtonUtils;
import com.google.code.joto.util.ui.ScrolledTextPane;
import com.thoughtworks.xstream.XStream;

/**
 * swing Panel for general information / Config about JotoContext
 *
 */
public class JotoConfigPanel {

	protected JotoContext context;

	protected JPanel panel;

	protected JPanel toolbar;
	
	protected JTextField configFileField;

	protected ScrolledTextPane xmlTextPane;
	
	// ------------------------------------------------------------------------

	public JotoConfigPanel(JotoContext context) {
		this.context = context;
		initComponents();
	}

	private void initComponents() {
		panel = new JPanel(new BorderLayout());
		
		toolbar = new JPanel(new FlowLayout());
		panel.add(toolbar, BorderLayout.NORTH);

		toolbar.add(JButtonUtils.snew("apply edit", this, "onButtonApplyEditConfigFile"));
		toolbar.add(JButtonUtils.snew("undo edit", this, "onButtonUndoEditConfigFile"));
		toolbar.add(JButtonUtils.snew("reload", this, "onButtonReloadConfigFile"));
		toolbar.add(JButtonUtils.snew("save", this, "onButtonSaveConfigFile"));

		JPanel formPanel = new JPanel(new GridBagLayout());
		GridBagLayoutFormBuilder fb = new GridBagLayoutFormBuilder(formPanel);
		panel.add(formPanel, BorderLayout.CENTER);

		configFileField = new JTextField();
		fb.addLabelComp("Config File", configFileField);
		
//		fb.addLabelComp("reload config", JButtonUtils.snew("reload", this, "onButtonReloadConfigFile"));
//		fb.addLabelComp("save config", JButtonUtils.snew("save", this, "onButtonSaveConfigFile"));
		
		xmlTextPane = new ScrolledTextPane(); 
		fb.addLabelCompFill2Rows("xml config (XStream)", xmlTextPane.getJComponent());
	}

	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return panel;
	}
	
	public void setContext(JotoContext p) {
		this.context = p;
	}

	/** called by introspection, from UI */
	public void onButtonApplyEditConfigFile(ActionEvent event) {
		viewToModel();
	}

	private void viewToModel() {
		String xml = xmlTextPane.getText();
		XStream xstream = JotoConfigUtils.getXStream();
		JotoConfig config = context.getConfig();
		try {
			config = (JotoConfig) xstream.fromXML(xml);
		} catch(Exception ex) {
			// context.getConfig();
			xml = "<!-- Failed to parse xstream JotoConfig :" + ex.getMessage() + "-->\n" + xml;
			xmlTextPane.setText(xml);
		}
		context.setConfig(config);
	}

	private void modelToView() {
		JotoConfig config = context.getConfig();
		XStream xstream = JotoConfigUtils.getXStream();
		String xml = xstream.toXML(config);
		xmlTextPane.setText(xml);
	}

	/** called by introspection, from UI */
	public void onButtonUndoEditConfigFile(ActionEvent event) {
		modelToView();
	}

	/** called by introspection, from UI */
	public void onButtonReloadConfigFile(ActionEvent event) {
		File configFile = new File(configFileField.getText());
		XStream xstream = JotoConfigUtils.getXStream();
		JotoConfig config = (JotoConfig) XStreamUtils.fromFile(xstream, configFile);
		context.setConfig(config);
		
		modelToView();
	}
	
	/** called by introspection, from UI */
	public void onButtonSaveConfigFile(ActionEvent event) {
		viewToModel();

		File configFile = new File(configFileField.getText());
		JotoConfig config = context.getConfig();
		XStream xstream = JotoConfigUtils.getXStream();
		XStreamUtils.toFile(xstream, config, configFile);
	}

}
