package com.google.code.joto.util.ui;

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * icons utilities
 * 
 * cf for example 
 * http://www.veryicon.com/icons/application/aspnet/filter-1.html
 * http://xantorohara.110mb.com/core-icons/Eclipse.html
 * http://www.cypal.in/EclipseIconsAll.zip
 * http://iconlet.com/
 * http://www.vogella.de/blog/2009/11/19/icons-eclipse/

 */
public class IconUtils {

	public static final IconSet basic32 = new IconSet("icons/basic/", "-32x32.png");
	
	public static final IconSet basicPng = new IconSet("icons/basic/", ".png");
	
	public static final IconSet eclipseGif = new IconSet("icons/eclipse/", ".gif");

	
	public static IconSet getBasic32() {
		return basic32;
	}

	/**
	 *
	 */
	public static class IconSet {
		
		private final String prefix;
		private final String suffix;

//		private ImageIcon notFound = new ImageIcon();
		
		private IconSet(String prefix, String suffix) {
			this.prefix = prefix;
			this.suffix = suffix;
		}

		public ImageIcon get(String name) {
			String resourceName = prefix + name + suffix;
			return getResource(resourceName);
		}

//		private ImageIcon saveIcon;
//		public ImageIcon getSaveIcon() {
//			if (saveIcon == null) saveIcon = get("save"); 
//			return saveIcon;
//		}
//
//		private ImageIcon playIcon;
//		public ImageIcon getPlayIcon() {
//			if (playIcon == null) playIcon = get("play"); 
//			return playIcon;
//		}
//
//		private ImageIcon pauseIcon;
//		public ImageIcon getPauseIcon() {
//			if (pauseIcon == null) pauseIcon = get("pause"); 
//			return pauseIcon;
//		}
	}
	
	public static ImageIcon getResource(String resourceName) {
		ClassLoader cl = IconUtils.class.getClassLoader();
		URL url = null;
		try {
			url = cl.getResource(resourceName);
		} catch(Exception ex) {
			url = null; // icon not found ... temporary hack
		}
		if (url != null) {
			return new ImageIcon(url);
		} else {
			return null;
		}
	}
	
}
