package com.google.code.joto.ui.filter;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * utility class for preferences on selecting/saving RecordEventFilterFiles 
 */
public class RecordEventFilterFilesPreferences {
	
	protected File baseDir;
	protected String fileSuffix;
	
	// ------------------------------------------------------------------------

	public RecordEventFilterFilesPreferences() {
	}

	// ------------------------------------------------------------------------

	public File getBaseDir() {
		return baseDir;
	}
	
	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}
	
	public String getFileSuffix() {
		return fileSuffix;
	}
	
	public void setFileSuffix(String p) {
		this.fileSuffix = p;
	}
	
	public void getExportToPreferences(Preferences pref, String prefix) {
		String baseDirPath = (baseDir != null)? baseDir.getAbsolutePath() : null;
		pref.put(prefix + "FilterFileBaseDir", baseDirPath);
		pref.put(prefix + "FilterFileSuffix", fileSuffix);
	}
	
	public void setImportFromPreferences(Preferences pref, String prefix) {
		String baseDirPath = pref.get(prefix + "FilterFileBaseDir", null);
		File newBaseDir = (baseDirPath != null)? new File(baseDirPath) : null;
		setBaseDir(newBaseDir);
		String newSuffix = pref.get(prefix + "FilterFileSuffix", "-filter.xml");
		setFileSuffix(newSuffix);
	}
	
}