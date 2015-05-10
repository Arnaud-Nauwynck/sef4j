package org.sef4j.core.helpers.files;

import java.io.Serializable;
import java.nio.file.WatchEvent;

public class ContentFileChangeEvent extends FileChangeEvent implements Serializable {
	
	/** */
	private static final long serialVersionUID = 1L;
	
	private byte[] content;
	
	// ------------------------------------------------------------------------
	
	public ContentFileChangeEvent(String filePath, WatchEvent.Kind<?> eventKind, byte[] content) {
		super(filePath, eventKind);
		this.content = content;
	}

	// ------------------------------------------------------------------------
	
	public byte[] getContent() {
		return content;
	}
	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "ContentFileChangeEvent[filePath=" + filePath 
				+ ", eventKind=" + eventKind + ", fileContent="
				+ "]";
	}

}