package org.sef4j.core.helpers.files;

import java.io.Serializable;
import java.nio.file.WatchEvent;

public class FileWatchChangeEvent implements Serializable {
	
	/** */
	private static final long serialVersionUID = 1L;
	
	private final String filePath;
	private final WatchEvent.Kind<?> eventKind;
	
	// ------------------------------------------------------------------------
	
	public FileWatchChangeEvent(String filePath, WatchEvent.Kind<?> eventKind) {
		this.filePath = filePath;
		this.eventKind = eventKind;
	}

	// ------------------------------------------------------------------------
	
	public String getFilePath() {
		return filePath;
	}
	
	public WatchEvent.Kind<?> getEventKind() {
		return eventKind;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "ContentFileWatchChangeEvent [filePath=" + filePath 
				+ ", eventKind=" + eventKind + ", fileContent="
				+ "]";
	}

	
}