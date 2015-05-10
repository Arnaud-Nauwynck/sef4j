package org.sef4j.core.helpers.files;

import org.sef4j.core.helpers.tasks.TaskDef;

public class ChangedFileWatchPollingEventProviderDef extends TaskDef {
	
	/** */
	private static final long serialVersionUID = 1L;

	private final String filePath;
	
	public ChangedFileWatchPollingEventProviderDef(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChangedFileWatchPollingEventProviderDef other = (ChangedFileWatchPollingEventProviderDef) obj;
		if (filePath == null) {
			if (other.filePath != null)
				return false;
		} else if (!filePath.equals(other.filePath))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ChangedFileWatch[filePath=" + filePath + "]";
	}
	
}