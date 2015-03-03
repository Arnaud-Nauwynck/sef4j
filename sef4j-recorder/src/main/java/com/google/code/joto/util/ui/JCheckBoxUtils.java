package com.google.code.joto.util.ui;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;

public class JCheckBoxUtils {


	/**
	 * reflection utility replacement for
	 * <code>
	 * {@ 
	 * jcomp.addActionListener(new ActionListener() { 
	 * 		public void actionPerformed(ActionEvent event) { 
	 * 			targetObj.methodName(event);
	 *  	}
	 * });
	 * }
	 * </code>   
	 * @param jcomp
	 * @param targetObj
	 * @param methodName
	 */
	public static void addActionListener(AbstractButton jcomp, Object targetObj, String methodName) {
		JButtonUtils.addActionListener(jcomp, targetObj, methodName);
	}


	public static JCheckBox snew(String label, boolean initiallySelected, Object targetObj, String methodName) {
		JCheckBox res = new JCheckBox(label);
		res.setSelected(initiallySelected);
		JButtonUtils.addActionListener(res, targetObj, methodName);
		return res;
	}
}
