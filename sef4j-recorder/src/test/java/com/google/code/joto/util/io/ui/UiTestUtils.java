package com.google.code.joto.util.io.ui;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class UiTestUtils {

	public static void showInFrame(JComponent comp) {
		JFrame frame = doShowInFrame(comp);
		try {
			Thread.sleep(60000);
		} catch(InterruptedException ex) {
			// ignore, do nothing!
		}
		frame.setVisible(false);
		frame.dispose();
	}

	public static JFrame doShowInFrame(JComponent comp) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(comp);
		frame.pack();
		frame.setVisible(true);
		return frame;
	}
	
	public static void showInFrame(JComponent comp, JComponent comp2) {
		JFrame frame1 = doShowInFrame(comp);
		JFrame frame2 = doShowInFrame(comp2);
		
		try {
			Thread.sleep(120000);
		} catch(InterruptedException ex) {
			// ignore, do nothing!
		}
		frame1.setVisible(false);
		frame1.dispose();
		frame2.setVisible(false);
		frame2.dispose();
	}
	
}
