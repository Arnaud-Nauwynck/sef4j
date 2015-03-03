package com.google.code.joto.eventrecorder.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit test for FileRecordEventStore
 */
public class FileRecordEventStoreTest extends AbstractRecordEventStoreTestHelper {

	private static final Logger log = LoggerFactory.getLogger(FileRecordEventStoreTest.class);
	
	public FileRecordEventStoreTest(String name) {
		super(name);
	}

	public void testWrite1() {
		File file = getTestFileToCreate("FileTest1.tmp");
		FileRecordEventStore eventStore = new FileRecordEventStore(file);
		try {
			eventStore.open("rw");

			doTestWrite1CloseRead(eventStore);
		
		} catch(Exception ex) {
			eventStore.close();
			throw new RuntimeException(ex);
		} finally {
			deleteTestFile(file);
		}
	}
	
	public void testReadWriteMultiple() {
		File file = getTestFileToCreate("FileTestReadWriteMultiple.tmp");
		try {
			FileRecordEventStore eventStore = new FileRecordEventStore(file);
			eventStore.open("rw");

			doTestReadWriteMany(eventStore, false);
			
			// finish
			eventStore.close();
		} finally {
			deleteTestFile(file);
		}
	}


	public void testBenchmarkWriteSimple() {
		boolean deleteFile = true; // use false for debugging(?): showing file content
		// 
		log.info("benchmark FileRecordEventStore ... First result is not significative because hotspot is lazy...");
		String baseName = "FileRecordStoreBench";
		doTestBenchmarkWriteSimple(baseName, 50, 50, deleteFile); 
		log.info("now benchmark with different repeatCount x size");
		
		doTestBenchmarkWriteSimple(baseName, 1000,   10, deleteFile);
		doTestBenchmarkWriteSimple(baseName,  100,  100, deleteFile);
//		doTestBenchmarkWriteSimple(baseName,   10, 1000, deleteFile); 
		
//		doTestBenchmarkWriteSimple(baseName,   10, 10000); 
//		doTestBenchmarkWriteSimple(baseName,  5, 500000);

		log.info("benchmark FileRecordEventStore finished");
	}
	
	protected void doTestBenchmarkWriteSimple(String baseName, final int repeatCount, final int writeCount, boolean deleteFile) {
		System.gc();
		System.gc();
		String fileName = baseName + "x" + repeatCount + "-" + writeCount + ".tmp";
		File file = getTestFileToCreate(fileName);
		FileRecordEventStore eventStore = new FileRecordEventStore(file);
		try {
			eventStore.open("rw");

			doRunBenchmarkWriteSimple(eventStore, repeatCount, writeCount);
			
			// finish
			eventStore.close();
		} catch(Exception ex) {
			eventStore.close();
			throw new RuntimeException(ex);
		} finally {
			if (deleteFile) {
				deleteTestFile(file);
			}
		}
	}
	
}
