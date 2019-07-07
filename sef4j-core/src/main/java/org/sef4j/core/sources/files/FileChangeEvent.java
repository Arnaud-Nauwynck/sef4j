package org.sef4j.core.sources.files;

import java.io.Serializable;
import java.nio.file.WatchEvent;

public class FileChangeEvent implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    protected final String filePath;
    protected final WatchEvent.Kind<?> eventKind;

    // ------------------------------------------------------------------------

    public FileChangeEvent(String filePath, WatchEvent.Kind<?> eventKind) {
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
	return "ContentFileWatchChangeEvent [filePath=" + filePath + ", eventKind=" + eventKind + ", fileContent="
		+ "]";
    }

}