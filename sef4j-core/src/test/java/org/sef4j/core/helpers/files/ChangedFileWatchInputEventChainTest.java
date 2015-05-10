package org.sef4j.core.helpers.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sef4j.core.MockEventSender;
import org.sef4j.core.api.ioeventchain.DefaultInputEventChainDefs.ChangedFileWatchInputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChain.ListenerHandle;
import org.sef4j.core.helpers.tasks.PeriodicityDef;


public class ChangedFileWatchInputEventChainTest {

	protected MockEventSender<FileChangeEvent> mockResult = new MockEventSender<FileChangeEvent>();
	
	protected Path watchDirPath = Paths.get("target", "tests", "watchdir1");
	protected Path file1Path = watchDirPath.resolve("file1");
	protected ChangedFileWatchInputEventChainDef def = new ChangedFileWatchInputEventChainDef(watchDirPath.toString(),
			new PeriodicityDef(50, TimeUnit.MILLISECONDS, "default"));
	protected ChangedFileWatchInputEventChain sut = new ChangedFileWatchInputEventChain(def, "test");

	@Before
	public void setup() throws IOException {
		if (!Files.exists(watchDirPath)) {
			Files.createDirectories(watchDirPath);
		}
	}

	@Test
	public void testRegisterEventListener_underlyingSendEvent_unregisterEventListener() throws Exception {	
		// Prepare
		Assert.assertFalse(sut.isStarted());
		ListenerHandle<FileChangeEvent> subscr = sut.registerEventListener(mockResult);
		Assert.assertTrue(sut.isStarted());
		// Perform
		Files.write(file1Path, "Hello".getBytes());
		Thread.sleep(100); // wait for polling timer in separate thread!!
		
		// Post-check
		List<FileChangeEvent> res;
		res = mockResult.clearAndGet();
		if (res.isEmpty()) {
			Thread.sleep(100);
			res = mockResult.clearAndGet();
		}
		// sut.poll(); // useless!..
		
		Assert.assertTrue(res.size() >= 1);
		FileChangeEvent event0 = res.get(0);
		Assert.assertEquals(StandardWatchEventKinds.ENTRY_MODIFY, event0.getEventKind());
		Assert.assertEquals(file1Path.toString(), event0.getFilePath().toString());
		
		sut.unregisterEventListener(subscr);
	}

}
