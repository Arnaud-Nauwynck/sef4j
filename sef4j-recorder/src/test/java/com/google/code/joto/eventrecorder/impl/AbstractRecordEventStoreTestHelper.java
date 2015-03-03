package com.google.code.joto.eventrecorder.impl;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.testobj.TestObjFactory;

/**
 *
 */
public class AbstractRecordEventStoreTestHelper extends TestCase {

	private static final Logger log = LoggerFactory.getLogger(AbstractRecordEventStoreTestHelper.class);
	
	private File targetTestDir;
	
	public AbstractRecordEventStoreTestHelper(String name) {
		super(name);
	}

	public void setUp() {
		targetTestDir = new File("target/test");
		if (!targetTestDir.exists()) {
			targetTestDir.mkdirs();
		}
	}
	
	protected File getTargetTestDir() {
		return targetTestDir;
	}
	
	protected File getTestFileToCreate(String fileName) {
		File file = new File(targetTestDir, fileName);
		if (file.exists()) {
			file.delete();
		}
		return file;
	}
	
	protected void deleteTestFile(File file) {
		file.delete();
	}

	protected void doTestWrite1CloseRead(RecordEventStore eventStore) {
		generateEvent(eventStore, 0);

		checkGetEvents(eventStore, 1, 2, true);
		
		eventStore.close();

		eventStore.open("r");
		checkGetEvents(eventStore, 1, 2, true);

		eventStore.close();

		eventStore.open("ra");
		checkGetEvents(eventStore, 1, 2, true);

	}

	protected void doTestReadWriteMany(RecordEventStore eventStore, boolean canPurge) {
		int count = 0;
		
		RecordEventData evt1 = generateEvent(eventStore, 0);
		count++;
		
		assertNotNull(evt1);
		assertEquals(1, evt1.getEventId());
		assertEquals(1, eventStore.getFirstEventId());
		assertEquals(count+1, eventStore.getLastEventId());
		assertEquals(count, eventStore.getEventsCount());
		
		RecordEventData evt2 = generateEvent(eventStore, 0);
		count++;
		
		assertNotNull(evt2);
		assertEquals(2, evt2.getEventId());
		assertEquals(count+1, eventStore.getLastEventId());
		assertEquals(1, eventStore.getFirstEventId());
		assertEquals(count, eventStore.getEventsCount());
		
		generateEvents(eventStore, 1000, 0);
		count += 1000;
		
		assertEquals(count+1, eventStore.getLastEventId());
		if (!canPurge) {
			assertEquals(1, eventStore.getFirstEventId());
			assertEquals(count, eventStore.getEventsCount());
		}
				
		// test re-reading past events

		int checkFromId = (!canPurge)? 1 : eventStore.getFirstEventId(); 
		checkGetEvents(eventStore, checkFromId, count+1, true);
		// equivalent to ... checkGetEvents(eventStore, 0, -1);
					
		if (!canPurge) {
			checkGetEvents(eventStore, 5, 10, true);
		} else {
			int checkFromId2 = Math.min(checkFromId + 5, count);
			int checkToId2 = Math.min(checkFromId2 + 10, count+1);
			if (checkFromId2 < checkToId2) {
				checkGetEvents(eventStore, checkFromId2, checkToId2, true);
			}
		}
		
		// alternate writing and re-reading
		Random rand = new Random(0);
		for (int i = 1; i < 5; i++) {
			int writeCount = i*2;

			generateEvents(eventStore, writeCount, 0);
			count += writeCount;
			
			assertEquals(count+1, eventStore.getLastEventId());
			if (!canPurge) {
				assertEquals(count, eventStore.getEventsCount());
			}
			
			int firstEventId = eventStore.getFirstEventId();
			int lastEventId = eventStore.getLastEventId();
			if (lastEventId != firstEventId) {
				int readFromEventId = firstEventId + rand.nextInt(eventStore.getEventsCount());
				if (count > readFromEventId) {
					int readToEventId = readFromEventId + rand.nextInt(count - readFromEventId);
					checkGetEvents(eventStore, readFromEventId, readToEventId, true);
				}
			}			
		}
			
		// close and reopen in readonly
		eventStore.close();
		eventStore.open("r");
		
		// re-read
		int readFromEventId = eventStore.getFirstEventId();
		checkGetEvents(eventStore, readFromEventId, count, true);

	}


	
	protected List<RecordEventSummary> checkGetEvents(RecordEventStore eventStore,
			int readFromEventId, int readToEventId,
			boolean checkGetEventData
	) {
		if (readFromEventId == 0) {
			readFromEventId = 1;
		}
		if (readToEventId == -1) {
			readToEventId = eventStore.getLastEventId();
		}
		if (readFromEventId < eventStore.getFirstEventId()) {
			readFromEventId = eventStore.getFirstEventId();
		}
		if (readToEventId > eventStore.getLastEventId()) {
			readToEventId = eventStore.getLastEventId();
		}

		List<RecordEventSummary> res = eventStore.getEvents(readFromEventId, readToEventId);
		assertNotNull(res);
		int readCount = readToEventId - readFromEventId; 
		assertEquals(readCount, res.size());
		if (readCount > 0) {
			assertEquals(readFromEventId, res.get(0).getEventId());
			if (readCount > 1) {
				assertEquals(readFromEventId+1, res.get(1).getEventId());
			}
			if (readCount > 2) {
				assertEquals(readFromEventId+2, res.get(2).getEventId());
				assertEquals(readToEventId-3, res.get(readCount-3).getEventId());
			}
			if (readCount > 1) {
				assertEquals(readToEventId-2, res.get(readCount-2).getEventId());
			}
			assertEquals(readToEventId-1, res.get(readCount-1).getEventId());
		}
		
		if (checkGetEventData) {
			checkGetEventDataList(eventStore, res);
		}
		
		return res;
	}


	protected void checkGetEventData(RecordEventStore eventStore, RecordEventSummary eventSummary) {
		RecordEventData eventData = eventStore.getEventData(eventSummary);
		assertNotNull(eventData);
	}

	protected void checkGetEventDataList(RecordEventStore eventStore, List<RecordEventSummary> events) {
		for (RecordEventSummary e : events) {
			checkGetEventData(eventStore, e);
		}
		// toadd: also test in random order?
	}

	protected void generateEvents(RecordEventStore eventStore, int count, int randSeed) {
		for (int i = 0; i < count; i++) {
			generateEvent(eventStore, randSeed++);
		}
	}
	
	protected RecordEventData generateEvent(RecordEventStore eventStore, int randSeed) {
		RecordEventSummary e = RecordEventSummary.snewDefault(
					"testEventType" + (randSeed%5), 
					"testEventSubType" + (randSeed%10),
					"testClass" + (randSeed%3),
					"testMeth" + (randSeed%20));
		Serializable objData = TestObjFactory.createAnySerializableBean(randSeed);
		RecordEventData res = eventStore.addEvent(e, objData);
		return res;
	}


	protected void generateEvents_SimpleIntFieldA(RecordEventStore eventStore, int count, int randSeed) {
		for (int i = 0; i < count; i++) {
			generateEvent_SimpleIntFieldA(eventStore, randSeed++);
		}
	}

	protected RecordEventData generateEvent_SimpleIntFieldA(RecordEventStore eventStore, int randSeed) {
		RecordEventSummary e = RecordEventSummary.snewDefault(
					"testEventType" + (randSeed%5), 
					"testEventSubType" + (randSeed%10), 
					"testClass" + (randSeed%3),
					"testMeth" + (randSeed%20));
		Serializable objData = TestObjFactory.createSimpleIntFieldA();
		RecordEventData res = eventStore.addEvent(e, objData);
		return res;
	}

	
	protected void doRunBenchmarkWriteSimple(RecordEventStore eventStore, int repeatCount, int writeCount) {
		long totalAddNanos = 0;
		long totalFlushNanos = 0;
		for (int i = 0; i < repeatCount; i++) {
			long nanos1 = System.nanoTime();
			generateEvents_SimpleIntFieldA(eventStore, writeCount, 0);
			long nanos2 = System.nanoTime();
			eventStore.flush();
			long nanos3 = System.nanoTime();

			totalAddNanos += (nanos2 - nanos1); 
			totalFlushNanos += (nanos3 - nanos2);
//				int millis10 = (int) (10*nanos / 1000000);
//				log.info("write+flush " + writeCount + " events with simple obj, " 
//						+ "took:" + (millis10*0.1) + " ms/" + writeCount
//						+ ", " + (millis10*0.1/writeCount) + " ms/u"
//						);
		}
		log.info("bench FileRecordEventStore: repeat " + repeatCount + " x write+flush " + writeCount + " events with simple obj\n"
				+ " total time: " + formatNanosTotalTime(totalAddNanos + totalFlushNanos, repeatCount, writeCount) + "\n"
				+ " time for addEvent(): " + formatNanosTotalTime(totalAddNanos, repeatCount, writeCount) + "\n"
				+ " time for flush(): " +  formatNanosTotalTime(totalFlushNanos, repeatCount, writeCount) + "\n"
				);
	}

	protected String formatNanosTotalTime(long totalNanos, int repeatCount, int size) {
		int totalMillis = (int) (totalNanos / 1000000); 
		String res = totalMillis + " ms" 
			+ ", " + (totalMillis/repeatCount) + " ms/" + size  + " in avg"
			+ ", " + ((double)totalMillis/repeatCount/size) + " ms/u in avg";
		return res;
	}
	
}
