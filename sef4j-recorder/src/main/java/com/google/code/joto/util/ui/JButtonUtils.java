package com.google.code.joto.util.ui;

import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * swing utility for JButton
 *
 */
public class JButtonUtils {
	
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
		ActionListener objActionListener = SwingBinderUtils.createObjectActionListener(targetObj, methodName);
		jcomp.addActionListener(objActionListener);
	}


	public static JButton snew(String label, Object targetObj, String methodName) {
		JButton res = new JButton(label);
		addActionListener(res, targetObj, methodName);
		return res;
	}

	public static JButton snew(ImageIcon icon, String label, Object targetObj, String methodName) {
		JButton res;
		if (icon != null) {
			//?? res = new JButton(label, icon);
			res = new JButton(icon);
			res.setToolTipText(label);
		} else {
			res = new JButton(label);
		}
		addActionListener(res, targetObj, methodName);
		return res;
	}
	
}
