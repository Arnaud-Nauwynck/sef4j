package com.google.code.joto.eventrecorder.writer;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.impl.AbstractRecordEventStoreTestHelper;
import com.google.code.joto.eventrecorder.impl.CyclicBufferRecordEventStore;
import com.google.code.joto.eventrecorder.impl.RollingFileRecordEventStore;
import com.google.code.joto.testobj.TestObjFactory;

/**
 * JUnit test for AsyncRecordEventWriter
 */
public class AsyncRecordEventWriterTest extends AbstractRecordEventStoreTestHelper {

	private static final Logger log = LoggerFactory.getLogger(AsyncRecordEventWriterTest.class);
	
	public AsyncRecordEventWriterTest(String name) {
		super(name);
	}

	public void test1() {
		CyclicBufferRecordEventStore eventStore = new CyclicBufferRecordEventStore();
		AsyncQueueRecordEventWriter writer = new AsyncQueueRecordEventWriter(eventStore.getEventWriter());

		int count = 0;
		assertEquals(1, eventStore.getLastEventId());
		count += writeRandomEvents(writer, null, 0, 1);
		assertEquals(1, eventStore.getLastEventId());
		
		writer.startQueue();
		writer.waitEmptyQueue();
		assertEquals(1 + count, eventStore.getLastEventId());
		
		count += writeRandomEvents(writer, null, 0, 100);
		
		writer.waitEmptyQueue();
		assertEquals(1 + count, eventStore.getLastEventId());
		
		writer.stopQueue();
	}

	public void testBenchmarkWriteSimple() {
		boolean deleteFile = true; // use false for debugging(?): showing file content
		log.info("benchmark Async+RollingFileRecordEventStore ... First result is not significative because hotspot is lazy...");
		String baseName = "AsyncRollingFileRecord";
		int maxRollingFiles = 4;
		int maxFileSize = 1024*1024; // 1Mo
		doTestBenchmarkWriteSimple(baseName, maxRollingFiles, maxFileSize, 50, 50, deleteFile); 
		log.info("now benchmark with different repeatCount x size");
		
		doTestBenchmarkWriteSimple(baseName, maxRollingFiles, maxFileSize, 1000,   10, deleteFile);
		doTestBenchmarkWriteSimple(baseName, maxRollingFiles, maxFileSize,  100,  100, deleteFile);
//		doTestBenchmarkWriteSimple(baseName, maxRollingFiles, maxFileSize,   10, 1000, deleteFile);
		
//		doTestBenchmarkWriteSimple(baseName, maxRollingFiles, maxFileSize, 10, 10000); 
//		doTestBenchmarkWriteSimple(baseName, maxRollingFiles, maxFileSize,5, 500000);

		log.info("benchmark Async+RollingFileRecordEventStore finished");
	}

	
	protected void doTestBenchmarkWriteSimple(
			String benchBaseName,  
			int maxRollingFiles, int maxFileSize, 
			final int repeatCount, final int writeCount, boolean deleteFile) {
		System.gc();
		System.gc();
		int sizeInMo = maxFileSize / (1024*1024);
		String fileName = benchBaseName + "_max" + maxRollingFiles 
			+ "-" + sizeInMo + "M" 
			+ "-bench-x" + repeatCount + "-" + writeCount + ".tmp";
		RollingFileRecordEventStore eventStore = 
			setupRollingFileTest(fileName, maxRollingFiles, maxFileSize);
		try {
			eventStore.open("rw");
			AsyncQueueRecordEventWriter writer = new AsyncQueueRecordEventWriter(eventStore.getEventWriter()); 
			writer.startQueue();
			
			doRunBenchmarkWriteSimple(eventStore, repeatCount, writeCount);
			
			writer.waitEmptyQueue();
			writer.stopQueue();
			writer.waitThreadStopped();
			
			// finish
			eventStore.close();
		} catch(Exception ex) {
			eventStore.close();
			throw new RuntimeException(ex);
		} finally {
			if (deleteFile) {
				eventStore.deleteFiles();
			}
		}
		
	}

	protected RollingFileRecordEventStore setupRollingFileTest(
			String testFileName,
			int maxRollingFiles, int maxFileSize) {
		RollingFileRecordEventStore eventStore = 
			new RollingFileRecordEventStore(getTargetTestDir(), testFileName, ".tmp");
		eventStore.setMaxRollingFiles(maxRollingFiles);
		eventStore.setMaxFileSize(maxFileSize);
		return eventStore;
	}

	protected void tearDownRollingFileTest(RollingFileRecordEventStore rollingFile) {
		rollingFile.deleteFiles();
	}


	protected void doRunBenchmarkWriteSimple(AsyncQueueRecordEventWriter eventWriter, int repeatCount, int writeCount) {
		long totalAddNanos = 0;
		long totalFlushNanos = 0;
		for (int i = 0; i < repeatCount; i++) {
			long nanos1 = System.nanoTime();
			generateEvents_SimpleIntFieldA(eventWriter, writeCount, 0);
			long nanos2 = System.nanoTime();
			eventWriter.waitEmptyQueue();
			long nanos3 = System.nanoTime();

			totalAddNanos += (nanos2 - nanos1); 
			totalFlushNanos += (nanos3 - nanos2);
//				int millis10 = (int) (10*nanos / 1000000);
//				log.info("write+flush " + writeCount + " events with simple obj, " 
//						+ "took:" + (millis10*0.1) + " ms/" + writeCount
//						+ ", " + (millis10*0.1/writeCount) + " ms/u"
//						);
		}
		log.info("bench Async+RollingFileRecordEventStore: repeat " + repeatCount + " x write+flush " + writeCount + " events with simple obj\n"
				+ " total time: " + formatNanosTotalTime(totalAddNanos + totalFlushNanos, repeatCount, writeCount) + "\n"
				+ " time for addEvent(): " + formatNanosTotalTime(totalAddNanos, repeatCount, writeCount) + "\n"
				+ " time for flush(): " +  formatNanosTotalTime(totalFlushNanos, repeatCount, writeCount) + "\n"
				);
	}

	protected void generateEvents_SimpleIntFieldA(RecordEventWriter eventWriter, int count, int randSeed) {
		for (int i = 0; i < count; i++) {
			generateEvent_SimpleIntFieldA(eventWriter, randSeed++);
		}
	}

	protected void generateEvent_SimpleIntFieldA(RecordEventWriter eventWriter, int randSeed) {
		RecordEventSummary e = RecordEventSummary.snewDefault(
					"testEventType" + (randSeed%5), 
					"testEventSubType" + (randSeed%10), 
					"testClass" + (randSeed%3), 
					"testMeth" + (randSeed%20));
		Serializable objData = TestObjFactory.createSimpleIntFieldA();
		eventWriter.addEvent(e, objData, null);
	}

	
	protected String formatNanosTotalTime(long totalNanos, int repeatCount, int size) {
		int totalMillis = (int) (totalNanos / 1000000); 
		String res = totalMillis + " ms" 
			+ ", " + (totalMillis/repeatCount) + " ms/" + size  + " in avg"
			+ ", " + ((double)totalMillis/repeatCount/size) + " ms/u in avg";
		return res;
	}
	

	
	protected int writeRandomEvents(RecordEventWriter writer, RecordEventWriterCallback callback, int randSeed, int count) {
		for (int i = 0; i < count; i++) {
			writeRandomEvent(writer, callback, randSeed+1);
		}
		return count;
	}
	
	protected void writeRandomEvent(RecordEventWriter writer, RecordEventWriterCallback callback, int randSeed) {
		RecordEventSummary e = RecordEventSummary.snewDefault(
				"testEventType" + (randSeed%5), 
				"testEventSubType" + (randSeed%10), 
				"testClass" + (randSeed%3), 
				"testMeth" + (randSeed%20));
		Serializable objData = TestObjFactory.createAnySerializableBean(randSeed);
		writer.addEvent(e, objData, callback);
	}
		
}
