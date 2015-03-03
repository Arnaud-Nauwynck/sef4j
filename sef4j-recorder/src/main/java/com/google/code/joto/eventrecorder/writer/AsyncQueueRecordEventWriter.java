package com.google.code.joto.eventrecorder.writer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 * Asynchronous implementation for RecordEventWriter
 */
public class AsyncQueueRecordEventWriter extends AbstractRecordEventWriter {

	private static class QueueEventData {
		RecordEventSummary event;
		Serializable objData;
		RecordEventWriterCallback callback;

		public QueueEventData(RecordEventSummary event, Serializable objData,
				RecordEventWriterCallback callback) {
			super();
			this.event = event;
			this.objData = objData;
			this.callback = callback;
		}
		
	}
	
	private static enum ThreadStatus {
		stopped,
		running,
		running_interrupting
	}
	

	/** underlying proxy target object */
	private RecordEventWriter target;
	
	private Object lock = new Object(); 

	private Queue<QueueEventData> queue = new LinkedList<QueueEventData>();
	
	private ThreadStatus currThreadStatus = ThreadStatus.stopped;

	
	//-------------------------------------------------------------------------

	public AsyncQueueRecordEventWriter(RecordEventWriter target) {
		this.target = target;
	}

	//-------------------------------------------------------------------------

	public void startQueue() {
		synchronized(lock) {
			switch(currThreadStatus) {
			case stopped: 
				new Thread(new Runnable() {
					public void run() {
						doRunThreadLoop();
					}
				}).start();
				break;
			case running: 
				// do nothing
				break;
			case running_interrupting: 
				currThreadStatus = ThreadStatus.running; // reset 
				break;
			}
		}
	}

	public void stopQueue() {
		synchronized(lock) {
			switch(currThreadStatus) {
			case stopped: 
				// do nothing
				break;
			case running: 
				currThreadStatus = ThreadStatus.running_interrupting;
				break;
			case running_interrupting: 
				// do nothing
				break;
			}
		}
	}

	public void waitEmptyQueue() {
		for(;;) {
			synchronized(lock) {
				if (queue.isEmpty()) {
					break;
				} else {
					try {
						lock.wait(500);
					} catch (InterruptedException e) {
						throw new RuntimeException(e); 
					}
				}
			}
		}
	}


	public void waitThreadStopped() {
		for(;;) {
			synchronized(lock) {
				if (currThreadStatus == ThreadStatus.stopped) {
					break;
				} else {
					try {
						lock.wait(500);
					} catch (InterruptedException e) {
						throw new RuntimeException(e); 
					}
				}
			}
		}
	}

	@Override
	public void addEvent(RecordEventSummary event, Serializable objData,
			RecordEventWriterCallback callback) {
		if (!isEnable()) {
			return;
		}
		if (!isEnable(event)) {
			return;
		}
		
		QueueEventData queueObj = new QueueEventData(event, objData, callback); 
		synchronized(lock) {
			queue.add(queueObj);
			if (queue.size() == 1) { // was empty
				lock.notify();
			}
		}
	}


	protected void doRunThreadLoop() {
		for(;;) {
			List<QueueEventData> tmpToProcess = null; 
			synchronized(lock) {
				if (currThreadStatus == ThreadStatus.running_interrupting) {
					currThreadStatus = ThreadStatus.stopped;
					return; // stop main loop!
				}
				if (!queue.isEmpty()) {
					tmpToProcess = new ArrayList<QueueEventData>(queue.size());
					for(; !queue.isEmpty(); ) {
						tmpToProcess.add(queue.poll());
					}
				} else {
					lock.notify(); // wake up thread for waitEmptyQueue()...
					try {
						lock.wait();
					} catch (InterruptedException e) {
						currThreadStatus = ThreadStatus.stopped;
						return; // stop main loop!
					}
					if (!queue.isEmpty()) {
						tmpToProcess = new ArrayList<QueueEventData>(queue.size());
						for(; !queue.isEmpty(); ) {
							tmpToProcess.add(queue.poll());
						}
					} else {
						continue; // wait more..
					}
				}
			}

			for(QueueEventData tmp : tmpToProcess) {
				target.addEvent(tmp.event, tmp.objData, tmp.callback); 
			}
		}
	}

	// override java.lang.Object 
	// -------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "AsyncQueueEventStoreWriter[" 
			+ "currThreadStatus=" + currThreadStatus 
			+ ", queue length=" + queue.size()
			+ "]";
	}
	
}
