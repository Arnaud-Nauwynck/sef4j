package com.google.code.joto.eventrecorder.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * JUnit test for RollingFileRecordEventStore
 */
public class RollingFileRecordEventStoreTest extends AbstractRecordEventStoreTestHelper {

	private static final Logger log = LoggerFactory.getLogger(RollingFileRecordEventStoreTest.class);
	
	public RollingFileRecordEventStoreTest(String name) {
		super(name);
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

	public void testWrite1() {
		RollingFileRecordEventStore eventStore = 
			setupRollingFileTest("RollingFileTest1", 3, 1000);
		try {
			eventStore.open("rw");

			doTestWrite1CloseRead(eventStore);

		} catch(Exception ex) {
			eventStore.close();
			throw new RuntimeException(ex);
		} finally {
			tearDownRollingFileTest(eventStore);
		}
	}


	public void testReadWriteMultiple() {
		RollingFileRecordEventStore eventStore = 
			setupRollingFileTest("RollingFileTestReadWriteMult", 3, 1000);
		try {
			eventStore.open("rw");

			doTestReadWriteMany(eventStore, true);
			
			// finish
			eventStore.close();
		} finally {
			tearDownRollingFileTest(eventStore);
		}
	}

	public void testBenchmarkWriteSimple() {
		boolean deleteFile = true; // use false for debugging(?): showing file content
		log.info("benchmark RollingFileRecordEventStore ... First result is not significative because hotspot is lazy...");
		String baseName = "RollingFileRecord";
		int maxRollingFiles = 4;
		int maxFileSize = 1024*1024; // 1Mo
		doTestBenchmarkWriteSimple(baseName, maxRollingFiles, maxFileSize, 50, 50, deleteFile); 
		log.info("now benchmark with different repeatCount x size");
		
		doTestBenchmarkWriteSimple(baseName, maxRollingFiles, maxFileSize, 1000,   10, deleteFile);
		doTestBenchmarkWriteSimple(baseName, maxRollingFiles, maxFileSize,  100,  100, deleteFile);
//		doTestBenchmarkWriteSimple(baseName, maxRollingFiles, maxFileSize,   10, 1000, deleteFile); 
		
//		doTestBenchmarkWriteSimple(baseName, maxRollingFiles, maxFileSize, 10, 10000); 
//		doTestBenchmarkWriteSimple(baseName, maxRollingFiles, maxFileSize,5, 500000);

		log.info("benchmark RollingFileRecordEventStore finished");
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

			doRunBenchmarkWriteSimple(eventStore, repeatCount, writeCount);
			
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

}
