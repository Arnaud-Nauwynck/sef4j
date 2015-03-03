package com.google.code.joto.util.ui;

import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JMenuItem;

/**
 * swing utility for JMenuItem
 *
 */
public class JMenuItemUtils {

	/**
	 * reflection utility replacement for
	 * <code>
	 * {@ 
	 * jmenuitem.addActionListener(new ActionListener() { 
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
	public static void addActionListener(AbstractButton jmenuitem, Object targetObj, String methodName) {
		ActionListener objActionListener = SwingBinderUtils.createObjectActionListener(targetObj, methodName);
		jmenuitem.addActionListener(objActionListener);
	}


	public static JMenuItem snew(String label, Object targetObj, String methodName) {
		JMenuItem res = new JMenuItem(label);
		addActionListener(res, targetObj, methodName);
		return res;
	}

}
