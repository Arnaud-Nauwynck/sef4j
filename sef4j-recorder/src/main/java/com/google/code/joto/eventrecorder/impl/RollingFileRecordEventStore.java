package com.google.code.joto.eventrecorder.impl;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.AddRecordEventStoreEvent;
import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 * rolling file implementation of RecordEventStore
 *
 * This class is the optimal candidate to use for production,
 * because it limits total file size (no need to cleanup logs). 
 * It should be combined with asynchronous writer for avoiding performance bottlenecks.
 *  
 */
public class RollingFileRecordEventStore extends AbstractRecordEventStore {

	private static final Logger log = LoggerFactory.getLogger(RollingFileRecordEventStore.class);

	/** Factory pattern for RecordEventStore */
	public static class RollingFileRecordEventStoreFactory implements RecordEventStoreFactory {
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private File parentDir;
		private String fileBasename;
		private String fileSuffix;

		public RollingFileRecordEventStoreFactory(File parentDir,
				String fileBasename, String fileSuffix) {
			this.parentDir = parentDir;
			this.fileBasename = fileBasename;
			this.fileSuffix = fileSuffix;
		}

		public RecordEventStore create() {
			return new RollingFileRecordEventStore(parentDir, fileBasename, fileSuffix);
		}
	}
	
	private File parentDir;
	private String fileBasename;
	private String fileSuffix;
	
	private int maxRollingFiles = 5;
	private int maxFileSize = 10 * 1024 * 1024; // 10Mo
	
	private int currRollingFileCount = 1;
	
	/**
	 * rolled files names, sorted as most recent first
	 * current = [0] = <<fileBasename>>.<<fileSuffix>>  
	 * prev = [1] = <<fileBasename>>.1.<<fileSuffix>>  
	 * prev-prev = [2] = <<fileBasename>>.2.<<fileSuffix>>  
	 * ...
	 * oldest = [currRollingFileCount-1] = <<fileBasename>>.<<x>>.<<fileSuffix>> 
	 */
	private FileRecordEventStore[] rolledFiles; // = new FileRecordEventStore[maxRollingFiles];
	
	//-------------------------------------------------------------------------

	public RollingFileRecordEventStore(File parentDir, String fileBasename, String fileSuffix) {
		this.parentDir = parentDir;
		this.fileBasename = fileBasename;
		this.fileSuffix = fileSuffix;
	}

	// getter/setter
	//-------------------------------------------------------------------------

	public File getParentDir() {
		return parentDir;
	}

	public String getFileBasename() {
		return fileBasename;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}
	
	public int getMaxRollingFiles() {
		return maxRollingFiles;
	}

	public void setMaxRollingFiles(int p) {
		this.maxRollingFiles = p;
		// TODO
	}

	public int getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	
	// -------------------------------------------------------------------------
	
	@Override
	public void open(String mode) {
		super.setMode(mode);

		if (mode.equals("rw")) {
			// delete existing files... re-create empty
			deleteFiles();
		}

		rolledFiles = new FileRecordEventStore[maxRollingFiles];
		rolledFiles[0] = new FileRecordEventStore(getNthRotateFile(0));
		rolledFiles[0].open(mode);
		currRollingFileCount = 1;
		
		for (int i = 1; i < maxRollingFiles; i++) {
			File nthFile = getNthRotateFile(i);
			if (nthFile.exists()) {
				rolledFiles[i] = new FileRecordEventStore(nthFile);
				rolledFiles[i].open("r");
				currRollingFileCount = i + 1;
			} else {
				break;
			}
		}
		if (currRollingFileCount == maxRollingFiles-1) {
			// TODO delete remaining files?
		}
		
		// check re-read firstEventId,lastEventId for each fragment..
		// TODO
	}

	@Override
	public void close() {
		super.close();
		for (int i = 0; i < currRollingFileCount; i++) {
			rolledFiles[i].close();
			rolledFiles[i] = null;
		}
		currRollingFileCount = 0;
	}

	public void deleteFiles() {
		deleteFiles(0, -1);
	}
	
	public void deleteFiles(int fromFileIndex, int toFileIndex) {
		if (toFileIndex == -1) toFileIndex = Integer.MAX_VALUE;
		for (int i = fromFileIndex; i < toFileIndex; i++) {
			File file = getNthRotateFile(i);
			if (file.exists()) {
				try {
					file.delete();
				} catch(Exception ex) {
					String msg = "Failed to delete file '" + file + "' for rotate!";
					log.error(msg, ex);
					throw new RuntimeException(msg, ex);
				}
			} else {
				// file not exists... break or find followings?
				if (toFileIndex == Integer.MAX_VALUE) {
					toFileIndex = i + 10; // give a chance to delete few more
					// break;
				}
			}
		}
	}
	
	@Override
	public void flush() {
		rolledFiles[0].flush();
	}

	@Override
	protected RecordEventData doAddEvent(RecordEventSummary eventInfo, Serializable objData) {
		RecordEventData res = rolledFiles[0].addEvent(eventInfo, objData);

		// update lastId + fire event in parent
		super.setLastEventId(res.getEventId() + 1);
		fireStoreEvent(new AddRecordEventStoreEvent(res));
		
		if (rolledFiles[0].getLastFilePosition() > maxFileSize) {
			// detected need to rotate
			rotateFile();
		}
		
		return res;
	}

	@Override
	public RecordEventData getEventData(RecordEventSummary evt) {
		int eventId = evt.getEventId();
		FileRecordEventStore[] array = rolledFiles;
		int fileIndex = getFileIndexForEventId(eventId);
		RecordEventData res = array[fileIndex].getEventData(evt);
		return res;
	}


	@Override
	public List<RecordEventSummary> getEvents(int fromEventId, int toEventId) {
		if (fromEventId == 0) {
			fromEventId = getFirstEventId();
		}
		if (toEventId == -1) {
			toEventId = getLastEventId();
		} else if (toEventId > getLastEventId()) {
			throw new IllegalArgumentException();
		}
		
		List<RecordEventSummary> res = new ArrayList<RecordEventSummary>(toEventId - fromEventId);
		FileRecordEventStore[] array = rolledFiles;
		int currFileIndex = getFileIndexForEventId(fromEventId);
		for (int currFromEventId = fromEventId; currFromEventId < toEventId; ) {
			int currLastEventId = array[currFileIndex].getLastEventId();
			if (toEventId >= currLastEventId) {
				// get events fragment and continue 
				List<RecordEventSummary> tmpres = 
						array[currFileIndex].getEvents(currFromEventId, currLastEventId);
				res.addAll(tmpres);
				currFromEventId = currLastEventId;
				currFileIndex--;
				if (currFileIndex == -1) {
					break; // should not occur?!
				}
			} else {
				// finish get
				List<RecordEventSummary> tmpres = 
					array[currFileIndex].getEvents(currFromEventId, toEventId);
				res.addAll(tmpres);
				break;
			}
		}
		return res;
	}


	@Override
	public synchronized void purgeEvents(int toEventId) {
		int fileIndex = getFileIndexForEventId(toEventId);
		int purgeAfterFileIndex = fileIndex + 1; // TOCHECK.. round
		// int oldFirstEventId = getFirstEventId(); 
		int effectiveNewFirstEventId = rolledFiles[fileIndex].getFirstEventId();  
		for (int i = purgeAfterFileIndex; i < currRollingFileCount; i++) {
			closeAndDeleteNthRolledFile(i);
		}
		currRollingFileCount = purgeAfterFileIndex;
		super.onTruncateSetFirstEventId(effectiveNewFirstEventId, null);
	}

	// ------------------------------------------------------------------------- 
	
	public File getNthRotateFile(int index) {
		String fn = this.fileBasename 
			+ ((index == 0)? "" : "." + index)
			+ this.fileSuffix;
		return new File(parentDir, fn);
	}
	
	public synchronized void rotateFile() {
		if (currRollingFileCount == maxRollingFiles) {
			// delete last file (or truncate+rename to reuse as curr?)
			FileRecordEventStore oldToDelete = rolledFiles[maxRollingFiles-1];
			int newFirstEventId = oldToDelete.getLastEventId();
			onTruncateSetFirstEventId(newFirstEventId, null);
			closeAndDeleteNthRolledFile(maxRollingFiles-1);
		} else {
			currRollingFileCount++;	
		}
		int lastEventId = getLastEventId();
		
		FileRecordEventStore prev0 = rolledFiles[0];
		// old file[0] in readwrite mode + is now file[1] in readonly mode
		// optim (equivalent?!) to: prev0.close(); prev0.open("r");
		prev0.setReadonly();

		// rotate files
		for(int i = currRollingFileCount-1; i > 0; i--) {
			rolledFiles[i] = rolledFiles[i-1];
			File dest = getNthRotateFile(i);
			rolledFiles[i].renameFile(dest);
		}
		rolledFiles[0] = null;
		
		
		// create new(0), in append mode
		FileRecordEventStore new0 = new FileRecordEventStore(getNthRotateFile(0));
		rolledFiles[0] = new0;
		new0.initSetFirstEventId(lastEventId);
		new0.setLastEventId(lastEventId);
		String mode0 = canRead? "rw" : "w";
		new0.open(mode0);
		
	}

	protected void closeAndDeleteNthRolledFile(int index) {
		FileRecordEventStore rolledFile = rolledFiles[index];
		try {
			rolledFile.close();
		} catch(Exception ex) {
			log.warn("Failed to close last file for rotate!", ex);
		}
		rolledFiles[index] = null;
		File file = getNthRotateFile(index);
		try {
			file.delete();
		} catch(Exception ex) {
			String msg = "Failed to delete file '" + file + "' for rotate!";
			log.error(msg, ex);
			throw new RuntimeException(msg, ex);
		}
	}

	protected int getFileIndexForEventId(int eventId) {
		int res = -1;
		FileRecordEventStore[] array = rolledFiles;
		if (eventId > array[0].getLastEventId()) {
			throw new IllegalArgumentException("invalid eventId, > lastEventId (: " + eventId + " > " + array[0].getLastEventId() + ")");
		}
		if (eventId < getFirstEventId()) {
			throw new IllegalArgumentException("event already purged: id=" + eventId + " < " + getFirstEventId());
		}

		for (int i = currRollingFileCount-1; i >= 0; i--) {
			if (eventId < array[i].getLastEventId()) {
				res = i;
				break;
			}// else continue with next rolledFile
		}
		
		if (res == -1) {
			throw new IllegalArgumentException("event already purged: id=" + eventId + " < " + getFirstEventId());
		}
		return res;
	}

}
