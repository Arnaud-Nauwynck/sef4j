package org.sef4j.core.helpers.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sef4j.core.helpers.senders.InMemoryEventSender;


public class ChangedFileWatchToEventProviderTest {

	protected Path watchDirPath = Paths.get("target", "tests", "watchdir1");
	protected Path file1Path = watchDirPath.resolve("file1");
		// Paths.get("target", "tests", "watchdir1", "file1");
	protected ChangedFileWatchToEventProvider sut = new ChangedFileWatchToEventProvider(watchDirPath, null);
	protected InMemoryEventSender<FileChangeEvent> resultEvents = new InMemoryEventSender<FileChangeEvent>();
	
	
	@Before
	public void setup() throws IOException {
		sut.addEventListener(resultEvents);
		if (!Files.exists(watchDirPath)) {
			Files.createDirectories(watchDirPath);
		}
	}
	
	@Test
	public void testCreateFile() throws Exception {
		// Prepare
		if (Files.exists(file1Path)) {
			Files.delete(file1Path);
		}
		sut.registerWatch();
		// Perform
		Files.write(file1Path, "Hello".getBytes());
		Thread.sleep(10);
		sut.poll();
		// Post-check
		sut.unregisterWatch();
		List<FileChangeEvent> ls = resultEvents.clearAndGet();
		Assert.assertTrue(1 <= ls.size()); // may got 2 events ??!
		FileChangeEvent e = (FileChangeEvent) ls.get(0);
		Assert.assertEquals(StandardWatchEventKinds.ENTRY_CREATE, e.getEventKind());
		Assert.assertEquals(file1Path.toString(), e.getFilePath());
	}

	@Test
	public void testUpdateFile() throws Exception {
		// Prepare
		if (! Files.exists(file1Path)) {
			Files.createFile(file1Path);
		}
		sut.registerWatch();
		// Perform
		Files.write(file1Path, "Hello2".getBytes());
		Thread.sleep(10);
		sut.poll();
		// Post-check
		List<FileChangeEvent> ls = resultEvents.clearAndGet();
		Assert.assertEquals(1, ls.size());
		FileChangeEvent e = (FileChangeEvent) ls.get(0);
		Assert.assertEquals(StandardWatchEventKinds.ENTRY_MODIFY, e.getEventKind());
		Assert.assertEquals(file1Path.toString(), e.getFilePath());

		// Perform (poll no change)
		sut.poll();
		// Post-check
		ls = resultEvents.clearAndGet();
		Assert.assertEquals(0, ls.size());
		
		// Perform (poll no change)
		Files.write(file1Path, "Hello3".getBytes());
		Files.write(file1Path, "Hello4".getBytes());
		Thread.sleep(10);
		sut.poll();
		// Post-check
		ls = resultEvents.clearAndGet();
		Assert.assertEquals(1, ls.size());
		
		// finish
		sut.unregisterWatch();
	}

	@Test
	public void testDeleteFile() throws Exception {
		// Prepare
		if (! Files.exists(file1Path)) {
			Files.createFile(file1Path);
		}
		sut.registerWatch();
		// Perform
		Files.delete(file1Path);
		Thread.sleep(10);
		sut.poll();
		// Post-check
		sut.unregisterWatch();
		List<FileChangeEvent> ls = resultEvents.clearAndGet();
		Assert.assertEquals(1, ls.size());
		FileChangeEvent e = (FileChangeEvent) ls.get(0);
		Assert.assertEquals(StandardWatchEventKinds.ENTRY_DELETE, e.getEventKind());
		Assert.assertEquals(file1Path.toString(), e.getFilePath());
	}

}
