package com.google.code.joto.testapp;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.util.ui.GridBagLayoutFormBuilder;
import com.google.code.joto.util.ui.JButtonUtils;

/**
 *
 */
public class FooPanel {
	
	private static Logger log = LoggerFactory.getLogger(FooPanel.class);
	
	private IFooService fooService;
	
	private JPanel panel;
	
	private JTextField int1TextField;
	private JTextField int2TextField;
	private JTextField double1TextField;
	private JTextField double2TextField;
	private JTextField dateTextField;
	
	// ------------------------------------------------------------------------

	public FooPanel(IFooService fooService) {
		this.fooService = fooService;
		initComponents();
	}

	private void initComponents() {
		panel = new JPanel(new GridBagLayout());
		GridBagLayoutFormBuilder b = new GridBagLayoutFormBuilder(panel);
		

		b.addLabelComp("foo()", JButtonUtils.snew("execute", this, "onButtonFoo"));

		int1TextField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		b.addLabelComp("int 1", int1TextField);
		int2TextField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		b.addLabelComp("int 2", int2TextField);
		b.addLabelComp("methInt()", JButtonUtils.snew("execute", this, "onButtonMethInt"));

		double1TextField = new JFormattedTextField(NumberFormat.getNumberInstance());
		b.addLabelComp("double 1", double1TextField);
		double2TextField = new JFormattedTextField(NumberFormat.getNumberInstance());
		b.addLabelComp("double 2", double2TextField);
		b.addLabelComp("methDouble()", JButtonUtils.snew("execute", this, "onButtonMethDouble"));

		dateTextField = new JFormattedTextField(new SimpleDateFormat("yyyy/MM/dd"));
		b.addLabelComp("date", dateTextField);
		b.addLabelComp("methDate()", JButtonUtils.snew("execute", this, "onButtonMethDate"));

		b.addLabelComp("methObj()", JButtonUtils.snew("execute", this, "onButtonMethObj"));
	
		setDefaultValues();
	}

	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return panel;
	}
	
	public void setDefaultValues() {
		int1TextField.setText("12");
		int2TextField.setText("45");
		double1TextField.setText("123.456");
		double2TextField.setText("45.6789");
		dateTextField.setText("2011/12/25");
	}
	
	/** called by introspection, for GUI callback */
	public void onButtonFoo(ActionEvent event) {
		fooService.foo();
	}

	/** called by introspection, for GUI callback */
	public void onButtonMethInt(ActionEvent event) {
		int arg1;
		int arg2;
		try {
			arg1 = Integer.parseInt(int1TextField.getText());
			arg2 = Integer.parseInt(int2TextField.getText());
		} catch(NumberFormatException ex) {
			log.error("Bad number format .. do nothing! ex:" + ex.getMessage());
			return;
		}
		fooService.methInt(arg1, arg2);
	}

	/** called by introspection, for GUI callback */
	public void onButtonMethDouble(ActionEvent event) {
		double arg1;
		double arg2;
		try {
			arg1 = Double.parseDouble(double1TextField.getText());
		} catch(NumberFormatException ex) {
			log.error("Bad number format .. use arg1=0 !!! " + ex.getMessage());
			arg1 = 0;
		}
		try {
			arg2 = Double.parseDouble(double2TextField.getText());
		} catch(NumberFormatException ex) {
			log.error("Bad number format .. use arg2=0 !!! " + ex.getMessage());
			arg2 = 0;
		}

		fooService.methDouble(arg1, arg2);
	}

	/** called by introspection, for GUI callback */
	public void onButtonMethDate(ActionEvent event) {
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date arg1;
		try {
			arg1 = sdf.parse(dateTextField.getText());
		} catch (ParseException ex) {
			log.error("Bad date format (expecting yyyy/MM/dd).. use arg1=null !!! " + ex.getMessage());
			arg1 = null;
		}
		int arg2 = Integer.parseInt(int1TextField.getText());
		fooService.methDate(arg1, arg2);
	}

	/** called by introspection, for GUI callback */
	public void onButtonMethObj(ActionEvent event) {
		List<Object> obj = new ArrayList<Object>();
		obj.add("testString");
		
		fooService.methObj(obj);
	}
	
}
