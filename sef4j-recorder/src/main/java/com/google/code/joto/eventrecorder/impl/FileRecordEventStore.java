package com.google.code.joto.eventrecorder.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.util.io.ByteArrayOutputStream2;
import com.google.code.joto.util.io.SerializableUtil;

/**
 * file implementation of RecordEventStore
 */
public class FileRecordEventStore extends AbstractRecordEventStore {

	private static Logger log = LoggerFactory.getLogger(FileRecordEventStore.class.getName());
	

	/** Factory pattern for RecordEventStore */
	public static class FileRecordEventStoreFactory implements RecordEventStoreFactory {
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private File eventDataFile;
		
		public FileRecordEventStoreFactory(File eventDataFile) {
			this.eventDataFile = eventDataFile;
		}

		public RecordEventStore create() {
			return new FileRecordEventStore(eventDataFile);
		}
	}
	
	private File eventDataFile;
	
	private int writeBufferSize = 8 * 8192;
	private OutputStream eventDataFileAppender;
	private Object eventDataFileLock = new Object();
	
	private long firstEventFilePosition; // required for skipping header
	private long lastFilePosition; // redundant with eventDataFileAppender.getCount() !!! 
	private int lastFlushedEventId;
	// private int lastFlushedFilePosition;
	
	
	
	private RandomAccessFile eventDataRandomAccessFile; // TODO remove... re-create InputStream on demand for reading!
	
	
	private ByteArrayOutputStream2 tmpBuffer = new ByteArrayOutputStream2();
	
	private EventCompressionContext eventCompressionContext = new EventCompressionContext();
	private boolean useObjectDataCompressionContext = true;
	
	// TODO useless / externalize in wrapper class CacheRecordEventStore
//	private WeakReference<IntList> cacheEventFilePositionArray = new WeakReference(new IntList());
	private WeakHashMap<Integer,Object> cacheEventObjectDataById = new WeakHashMap<Integer,Object>();
	
	// ------------------------------------------------------------------------

	public FileRecordEventStore(File eventDataFile) {
		super();
		this.eventDataFile = eventDataFile;

		// to call next... open();
	}

	// ------------------------------------------------------------------------
	
	public void setWriteBufferSize(int p) {
		this.writeBufferSize = p;
	}
	
	public int getWriteBufferSize() {
		return writeBufferSize;
	}
	
	public boolean isUseObjectDataCompressionContext() {
		return useObjectDataCompressionContext;
	}

	public void setUseObjectDataCompressionContext(boolean p) {
		if (canRead || canWriteAppend) {
			throw new UnsupportedOperationException("already open.. do not change config");
		}
		this.useObjectDataCompressionContext = p;
	}

	public long getLastFilePosition() {
		return lastFilePosition;
	}

	// -------------------------------------------------------------------------
	
	public void openRW() {
		open("rw");
	}
	
	/** implements RecordEventStore */
	public void open(String mode) {
		super.setMode(mode);

		boolean fileExists = eventDataFile.exists();
		boolean needReloadInit = false;
		boolean needCreateNew = false;
		if (mode.equals("ra")) {
			// mode read + append, if exists => reload else createnew
			if (fileExists) {
				needReloadInit = true;
			} else {
				needCreateNew = true;
			}
		} else if (mode.equals("rw")) {
			// mode read + append, if exists => erase(delete+createNew) else createnew
			if (fileExists) {
				deleteFile();
			}
			needCreateNew = true;
		} else if (mode.equals("r")) {
			// mode read + append, if exists => reload else error
			needReloadInit = true;
			if (!fileExists) {
				throw new RuntimeException("eventDataFile not found " + eventDataFile + ", can not open RecordEventStore in readonly");
			}
		} else {
			throw new IllegalArgumentException();
		}
		
		if (needCreateNew) {
			try {
				eventDataFile.createNewFile();
			} catch(Exception ex) {
				throw new RuntimeException("Failed to create eventDataFile " + eventDataFile, ex);
			}
		}
		
		try {
			if (canRead) {
				doOpenEventDataRandomAccessFile();
			}
			
			if (canWriteAppend) {
				FileOutputStream fileOut = new FileOutputStream(eventDataFile, true);
				this.eventDataFileAppender = new BufferedOutputStream(fileOut, writeBufferSize);
			}
			
		} catch(Exception ex) {
			close();
			throw new RuntimeException("Failed to open file " + eventDataFile, ex);
		}

		// initialize internal state
		this.lastFilePosition = 0;
		if (needReloadInit) {
			// reload file to restore internal state 
			doInitialReadFileHeaderAndLastMarker();
		} else if (needCreateNew) {
			this.lastFlushedEventId = 1;
			// write file header
			doWriteFileHeader();
		} else {
			// ??
			throw new IllegalStateException();
		}
		
//		log.info("open " + mode + " => " + toString());
	}
	
	/** implements RecordEventStore */
	public void close() {
		super.close();
		flushAndCloseWriter();
		doCloseEventDataRandomAccessFile();

//		log.info("close => " + toString());
	}

	private void doOpenEventDataRandomAccessFile() {
		try {
			this.eventDataRandomAccessFile = new RandomAccessFile(eventDataFile, "r");
		} catch(FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void doCloseEventDataRandomAccessFile() {
		if (eventDataRandomAccessFile != null) {
			try {
				eventDataRandomAccessFile.close();
			} catch(Exception ex) {
				log.error("Failed to close eventDataFile!", ex);
			}
			this.eventDataRandomAccessFile = null;
		}
	}

	private void flushAndCloseWriter() {
		if (eventDataFileAppender != null) {
			try {
				doWriteEventContextMarkerCurr();
				flush(); 
			} catch(Exception ex) {
				log.error("Failed to flush eventDataFile!", ex);
			}
			try {
				eventDataFileAppender.close();
			} catch(Exception ex) {
				log.error("Failed to close eventDataFile!", ex);
			}
			this.eventDataFileAppender = null;
		}
	}

	/**
	 * optim for <code>close() + open("r")</code>
	 */
	public void setReadonly() {
		if (canWriteAppend) {
			super.canWriteAppend = false;
			flushAndCloseWriter();
		}
	}

	public void renameFile(File dest) {
		doCloseEventDataRandomAccessFile();
		boolean ok = eventDataFile.renameTo(dest);
		if (!ok) {
			log.error("failed to rename file");
		}
		eventDataFile = dest;
		doOpenEventDataRandomAccessFile();
	}

	/** implements RecordEventStore */
	public void flush() {
		if (eventDataFileAppender != null) {
			try {
				eventDataFileAppender.flush();
				// eventDataRandomAccessFile.getChannel().force(true);
				lastFlushedEventId = getLastEventId();
			} catch(IOException ex) {
				throw new RuntimeException("failed to flush file " + eventDataFile, ex);
			}
		}
	}

	public void deleteFile() {
		try {
			eventDataFile.delete();
		} catch(Exception ex) {
			throw new RuntimeException("Failed to delete file " + eventDataFile, ex);
		}
	}
	
	/** purge for GC */
	public synchronized void purgeCache() {
		cacheEventObjectDataById.clear();
	}

	/** implements RecordEventStore */
	@Override
	public synchronized List<RecordEventSummary> getEvents(int fromEventId, int toEventId) {
		int eventIndex = fromEventId - getFirstEventId();
		if (eventIndex < 0) {
			throw new RuntimeException("event already purged");
		}
		int lastEventId = getLastEventId();
		if (toEventId == -1) {
			toEventId = lastEventId;
		} else if (toEventId < fromEventId) {
			throw new IllegalArgumentException("invalid toEventId:" + toEventId + " < fromEventId:" + fromEventId);
		} else if (toEventId > lastEventId) {
			throw new IllegalArgumentException("invalid toEventId:" + toEventId);
		}
		List<RecordEventSummary> res = new ArrayList<RecordEventSummary>(toEventId - fromEventId);
		
		long currEventPosition = firstEventFilePosition;
		int currEventId = getFirstEventId();
//		IntList filePosArray = cacheEventFilePositionArray.get();
//		if (filePosArray != null) {
//			currEventPosition = filePosArray.getAt(eventIndex);
//			currEventId = fromEventId;
//		} else {
//			// need to scan from before..
//			filePosArray = new IntList(); // restore weak reference
//			cacheEventFilePositionArray = new WeakReference(filePosArray);
//			currEventPosition = 0;
//			currEventId = getFirstEventId();
//		}

		try {
			synchronized (eventDataFileLock) {
				// scan/skip currEventId -> until fromEventId
				if (toEventId > lastFlushedEventId) {
					flush();
				}
				eventDataRandomAccessFile.seek(currEventPosition);
				for(; currEventId < fromEventId; currEventId++) {
					int eventTotalSize = eventDataRandomAccessFile.readInt();
					currEventPosition += eventTotalSize;
					eventDataRandomAccessFile.seek(currEventPosition);
				}
				// reached fromEventId ... now read until toEventId				
				for(; currEventId < toEventId; currEventId++) {
					RecordEventData eventData = 
						doReadEventData(currEventId, currEventPosition, true, null, false);
					res.add(eventData.getEventSummary());
				}
			}
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
		return res;
	}


	/** implements RecordEventStore */
	@Override
	public void purgeEvents(int toEventId) {
		// not supported on file... do nothing!
	}

	
	// ------------------------------------------------------------------------
	
	/** implements RecordEventStore */
	synchronized RecordEventData doAddEvent(RecordEventSummary eventInfo, Serializable objectData) {
		// prepare data to write in tmp buffer..
		// format: "<totalEventSize><eventSummarySize><encodedEventSummary><objectDataBytes>"
		tmpBuffer.reset();
		DataOutputStream tmpBufferDataOut = new DataOutputStream(tmpBuffer);
		int eventTotalSize;
		try {
			// "skip" 4 bytes for global size (header + encoded eventSummary + event data)
			// "skip" 4 bytes for size of encoded eventSummary
			tmpBufferDataOut.writeInt(0);
			tmpBufferDataOut.writeInt(0);
			eventCompressionContext.encodeContextualRecordEventSummary(eventInfo, tmpBufferDataOut);
			int eventSummarySize = tmpBuffer.getCount() - 8; 
			if (!useObjectDataCompressionContext) {
				ObjectOutputStream oout = new ObjectOutputStream(tmpBufferDataOut); 
				oout.writeObject(objectData);
			} else {
				eventCompressionContext.encodeContextualObjectData(objectData, tmpBufferDataOut);
			}
			
			eventTotalSize = tmpBuffer.getCount(); 

			// now tmp re-wind to write size of encoded eventSummary...
			tmpBuffer.setCount(0); // tmp re-wind
			tmpBufferDataOut.writeInt(eventTotalSize);
			tmpBufferDataOut.writeInt(eventSummarySize);
			tmpBuffer.setCount(eventTotalSize); // restore
		} catch(IOException ex) {
			throw new RuntimeException("should not occur on buffer!", ex);
		}
		
		// byte[] tmpOutBufferArray = tmpOutBuffer.toByteArray(); ... local copy
		byte[] eventBufferBytes = tmpBuffer.getBuffer();
		
		RecordEventData eventData = createNewEventData(eventInfo, objectData);
		
		doWriteEventData(eventData, eventBufferBytes, eventTotalSize);
		
		cacheEventObjectDataById.put(eventData.getEventId(), objectData);
		
		return eventData;
	}

	/** implements RecordEventStore */
	public synchronized RecordEventData getEventData(RecordEventSummary eventSummary) {
		Integer eventId = eventSummary.getEventId();
		Object objData = cacheEventObjectDataById.get(eventId);
		if (objData == null) {
			RecordEventData tmp = doReadEventData(
					eventSummary.getEventId(),
					eventSummary.getInternalEventStoreDataAddress(),
					false, eventSummary, 
					true);
			objData = tmp.getObjectData();
			cacheEventObjectDataById.put(eventId, objData);
		}
		return new RecordEventData(eventSummary, objData);
	}

	// ------------------------------------------------------------------------

	/**
	 * internal, to read file header to restore internal state
	 * 
	 * format: <<int firsEventId>> <<eventSummaryCompressionContext>> 
	 */
	private void doWriteFileHeader() {
		try {
			synchronized(eventDataFileLock) {
				if (lastFilePosition != 0) {
					throw new IllegalStateException();
				}

				tmpBuffer.reset();
				DataOutputStream tmpBufferDataOut = new DataOutputStream(tmpBuffer);
				
				tmpBufferDataOut.writeInt(getFirstEventId());
				eventCompressionContext.writeExternal2(tmpBufferDataOut);

				int tmpbytesCount = tmpBuffer.getCount();
				byte[] tmpbytes = tmpBuffer.getBuffer();
				eventDataFileAppender.write(tmpbytes, 0, tmpbytesCount);

				this.lastFilePosition = tmpbytesCount;
				
				this.firstEventFilePosition = lastFilePosition; 
			}
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * see corresponding doWriteFileHeader() for header file format
	 */
	private void doReadFileHeader() {
		try {
			synchronized(eventDataFileLock) {
				eventDataRandomAccessFile.seek(0);
				
				// read
				int firstEventId = eventDataRandomAccessFile.readInt();
				super.initSetFirstEventId(firstEventId);

				this.eventCompressionContext.readExternal2(eventDataRandomAccessFile);
				
				this.firstEventFilePosition = eventDataRandomAccessFile.getFilePointer();
			}
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}


	protected void doInitialReadFileHeaderAndLastMarker() {
		try {
			synchronized(eventDataFileLock) {
				doReadFileHeader();
				
				long fileLength = eventDataRandomAccessFile.length();
				this.lastFilePosition = fileLength;
				
				// rewind at beginning of this marker
				eventDataRandomAccessFile.seek(lastFilePosition - 4);
				int markerSize = eventDataRandomAccessFile.readInt();
				long beginMarkerPos = lastFilePosition - markerSize;
				eventDataRandomAccessFile.seek(beginMarkerPos);

				int checkReadMarker0 = eventDataRandomAccessFile.readInt();
				if (0 != checkReadMarker0) {
					throw new IllegalStateException();
				}
				// now read marker (lastEventId + context)
				EventContextMarker lastMarker = doReadEventContextMarker();
				
				// use marker to initialize self
				setLastEventId(lastMarker.getNextEventId());
				this.eventCompressionContext = lastMarker.getEventSummaryCompressionContext();
				
				assert lastFilePosition == fileLength; // .. already set above
				
			}
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	
	protected void doWriteEventData(RecordEventData eventData, byte[] preparedBytes, int preparedBytesLen) {
		try {
			synchronized(eventDataFileLock) {
				eventData.getEventSummary().setInternalEventStoreDataAddress(lastFilePosition);

//				long tmppos = eventDataRandomAccessFile.getFilePointer();
//				if (tmppos != lastFilePosition) {
//					eventDataRandomAccessFile.seek(lastFilePosition);
//				}
//				eventDataRandomAccessFile.write(preparedBytes, 0, preparedBytesLen);
//				// lastFilePosition = eventDataRandomAccessFile.getFilePointer()

				this.eventDataFileAppender.write(preparedBytes, 0, preparedBytesLen);
				
				
				lastFilePosition += preparedBytesLen;
			}
		} catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	

	protected RecordEventData doReadEventData(
			int eventId,
			long filePosition,
			boolean readRecordEventSummary,
			RecordEventSummary recordEventSummary, //... already read, reread??
			boolean readEventData) {
		RecordEventData res;
		synchronized(eventDataFileLock) {
			try {
				if (eventId > lastFlushedEventId) {
					flush();
				}
				eventDataRandomAccessFile.seek(filePosition);

				int eventTotalSize = eventDataRandomAccessFile.readInt();
				if (eventTotalSize == 0) {
					// special metadata marker!!
					int markerSize = eventDataRandomAccessFile.readInt();
					//=> skip!
					eventDataRandomAccessFile.skipBytes(markerSize-4); // -4 TOCHECK ??
					eventTotalSize = eventDataRandomAccessFile.readInt();
				}
				
				int eventSummarySize = eventDataRandomAccessFile.readInt();
				if (readRecordEventSummary && recordEventSummary == null) {
					tmpBuffer.reset();
					tmpBuffer.ensureCapacity(eventSummarySize);
					byte[] buffer = tmpBuffer.getBuffer();
					eventDataRandomAccessFile.read(buffer, 0, eventSummarySize);
					
					DataInputStream din = new DataInputStream(new ByteArrayInputStream(buffer, 0, eventSummarySize));
					recordEventSummary = eventCompressionContext.decodeContextualRecordEventSummary(eventId, din);
					// recordEventSummary.setEventId(eventId);// not possible... final => ctor copy!
					recordEventSummary = new RecordEventSummary(eventId, recordEventSummary);
					recordEventSummary.setInternalEventStoreDataAddress(filePosition);
				} else {
					// reread/seek/skip?
					int checkSkipped = eventDataRandomAccessFile.skipBytes(eventSummarySize);
					if (checkSkipped != eventSummarySize) {
						throw new RuntimeException(); // ???
					}
				}
				// read object data
				Object eventObjectData = null;
				int eventDataSize = eventTotalSize - eventSummarySize - 8;
				if (readEventData) {
					// TODO change RandomAccessFile to std InputStream!..
					byte[] eventObjectDataBytes = new byte[eventDataSize];
					eventDataRandomAccessFile.read(eventObjectDataBytes);
					if (!useObjectDataCompressionContext) {
						// ObjectInputStream oin = new ObjectInputStream(eventDataRandomAccessFile); 
						// eventObjectData = oin.readObject();
						eventObjectData = SerializableUtil.byteArrayToSerializable(eventObjectDataBytes);
					} else {
						try {
							ByteArrayInputStream tmpIn = new ByteArrayInputStream(eventObjectDataBytes); 
							eventObjectData = eventCompressionContext.decodeContextualObjectData(tmpIn);
						} catch(Exception ex) {
							throw new RuntimeException("Failed to decompress in memory obj data!", ex);
						}
					}
				} else { 
					// seek/skip
					int checkSkipped = eventDataRandomAccessFile.skipBytes(eventDataSize);
					if (checkSkipped != eventDataSize) {
						throw new RuntimeException(); // ???
					}
				}
				
				res = new RecordEventData(recordEventSummary, eventObjectData);
			} catch(IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		return res;
	}

	/**
	 * internal, to write a "metadata event" : a marker for lastEventId/contextual info
	 *
	 * format: <<0>> <<markerSize>> <<int lastEventId>> <<eventSummaryCompressionContext>> <<markerSize>>   
	 * .... TOADD could also write an index for all events offset
	 * 
	 * note that the first "0" is used as a special metadata marker, and can be ignored while reading for Events.
	 * also note that the markerSize is written both at beginning and end of the fragment, to allow reading it forward of from end of file
	 * 
	 */
	protected void doWriteEventContextMarker(EventContextMarker p) {
		synchronized(eventDataFileLock) {
			try {
				tmpBuffer.reset();
				DataOutputStream tmpBufferDataOut = new DataOutputStream(tmpBuffer);
				
				tmpBufferDataOut.writeInt(0); // 0 for special marker
				tmpBufferDataOut.writeInt(0xFFFF);// write next after size is known!
				tmpBufferDataOut.writeInt(p.getNextEventId());
				p.getEventSummaryCompressionContext().writeExternal2(tmpBufferDataOut);
				int markerSize = tmpBuffer.getCount() + 4;
				tmpBufferDataOut.writeInt(markerSize);

				// write markerSize at beginning (using tmp re-wind)
				tmpBuffer.setCount(4); // tmp...
				tmpBufferDataOut.writeInt(markerSize);
				tmpBuffer.setCount(markerSize); // ...restore
				
				byte[] tmpbytes = tmpBuffer.getBuffer();
				eventDataFileAppender.write(tmpbytes, 0, markerSize);				 
				lastFilePosition += markerSize;
				
			} catch(IOException ex) {
				throw new RuntimeException(ex);
			}
		}		
	}

	protected void doWriteEventContextMarkerCurr() {
		EventContextMarker marker = new EventContextMarker(
				getLastEventId(), eventCompressionContext);
		doWriteEventContextMarker(marker);
	}
	
	protected EventContextMarker doReadEventContextMarker() {
		synchronized(eventDataFileLock) {
			try {
				EventContextMarker res = new EventContextMarker();

				// read "0" : already consumed
				int markerSize = eventDataRandomAccessFile.readInt();
				res.nextEventId = eventDataRandomAccessFile.readInt();				
				res.eventCompressionContext.readExternal2(eventDataRandomAccessFile);
				int markerSize2 = eventDataRandomAccessFile.readInt();
				if (markerSize2 != markerSize) {
					throw new IllegalStateException();
				}
				
				return res;
			} catch(IOException ex) {
				throw new RuntimeException(ex);
			}
		}		
	}

	// override java.lang.Object 
	// -------------------------------------------------------------------------
	
	public String toString() {
		return "FileRecordEventSore[" 
			+ "firstEventId:" + getFirstEventId()
			+ " - lastEventId:" + getLastEventId()
			+ ", firstEventFilePosition:" + firstEventFilePosition
			+ ", lastFilePosition:" + lastFilePosition
			+ ", compressionContext:" + eventCompressionContext.toStringSizes()
			+ "]";
	}
	
	// -------------------------------------------------------------------------
	
	private static class EventContextMarker {
		private int nextEventId;
		private EventCompressionContext eventCompressionContext;

		public EventContextMarker() {
			this(-1, new EventCompressionContext());
		}

		public EventContextMarker(int nextEventId,
				EventCompressionContext eventCompressionContext) {
			this.nextEventId = nextEventId;
			this.eventCompressionContext = eventCompressionContext;
		}
		
		public int getNextEventId() {
			return nextEventId;
		}
		public EventCompressionContext getEventSummaryCompressionContext() {
			return eventCompressionContext;
		}
		
	}
	
}
