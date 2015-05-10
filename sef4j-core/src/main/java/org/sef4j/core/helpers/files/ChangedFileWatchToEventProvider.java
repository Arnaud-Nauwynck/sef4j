package org.sef4j.core.helpers.files;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.sef4j.core.helpers.senders.DefaultEventProvider;
import org.sef4j.core.helpers.tasks.PeriodicTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * java.nio helper class for watching on dir (not file in jdk?!), and convert calls to poll() into EventSender.sendEvent()
 * 
 * <PRE>
 * 
 *   registerWatch()
 *    ----->
 *                                                   <---addEventListener
 *   poll
 *    ----->              +------------------+
 *   poll                 |filePath          |
 *    ----->              |eventListeners    |    ---> sendEvent()
 *   poll                 |java.nio.watch    |
 *    ----->              +------------------+    ---> sendEvent()
 *   poll
 *    ----->
 *                                                   <---removeEventListener
 *   unregisterWatch()
 *    ----->
 * 
 * </PRE>
 * 
 */
public class ChangedFileWatchToEventProvider extends DefaultEventProvider<FileChangeEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(ChangedFileWatchToEventProvider.class);
	
	private Path watchPath;
	
	private WatchService watchService;	
	private WatchKey watchKey;

	/**
	 * optional ... when unset, calls to start() and stop() are not allowed
	 */
	private PeriodicTask pollingPeriodicTask;
	
	// ------------------------------------------------------------------------
	
	public ChangedFileWatchToEventProvider(Path watchPath, PeriodicTask.Builder pollingPeriodicTaskBuilder) {
		this.watchPath = watchPath;
		if (pollingPeriodicTaskBuilder != null) {
			pollingPeriodicTaskBuilder.withTask(() -> poll());
			this.pollingPeriodicTask = pollingPeriodicTaskBuilder.build();
		}
	}

	// ------------------------------------------------------------------------

	public boolean isStarted() {
		return isWatchRegistered() && pollingPeriodicTask != null && pollingPeriodicTask.isStarted();
	}

	public void start() {
		if (! isWatchRegistered()) {
			registerWatch();
		}
		this.pollingPeriodicTask.start();
	}

	public void stop() {
		this.pollingPeriodicTask.stop();
		if (isWatchRegistered()) {
			unregisterWatch();
		}
	}

	public boolean isWatchRegistered() {
		return watchKey != null;
	}
	
	public void registerWatch() {
		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException ex) {
			LOG.error("Failed to wath for file change '" + watchPath + "': can not get file WatchService", ex);
			return;
		}
		try {
			watchKey = watchPath.register(watchService, 
					StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException ex) {
			LOG.error("Failed to wath for file change '" + watchPath + "': can not get file WatchService", ex);
			return;
		}
	}
	
	public void unregisterWatch() {
		if (watchKey != null) {
			try {
				watchKey.cancel();
			} catch(Exception ex) {
				LOG.error("Failed to unregister file watch? .. ignore, no rethrow!", ex);
			}
			watchKey = null;
			watchService = null;
		}
	}
	
	public void poll() {
		if (watchKey == null) {
			return;
		}
		// get event if any is present, or null if none (non blocking)
		WatchKey polledKey = watchService.poll();
	    if (polledKey == null) {
	    	return;
	    }
	    
	    for (WatchEvent<?> event : polledKey.pollEvents()) {
	        // get event type
	        WatchEvent.Kind<?> kind = event.kind();
	 
	        // get file name
	        @SuppressWarnings("unchecked")
	        WatchEvent<Path> ev = (WatchEvent<Path>) event;
	        Path relativeChangedPath = ev.context();
	 
	        Path changedPath = watchPath.resolve(relativeChangedPath);
	        LOG.debug("detected watch file change:" + kind.name() + ": " + changedPath);
	 
	        if (kind == StandardWatchEventKinds.OVERFLOW) {
	            continue;
	        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY
	        		|| kind == StandardWatchEventKinds.ENTRY_CREATE
	        		|| kind == StandardWatchEventKinds.ENTRY_DELETE) {
	        	FileChangeEvent fileChangeEvent = new FileChangeEvent(
	        			changedPath.toString(), kind);
	        	// *** sendEvent ***
	        	super.sendEvent(fileChangeEvent);
	        }
	        
	        watchKey.reset();
	    }
	}
	
}
