package org.sef4j.core.helpers.files;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.sef4j.core.helpers.tasks.PollingEventProvider.AbstractPollingEventProvider;
import org.sef4j.core.util.IStartableSupport;
import org.sef4j.core.util.factorydef.AbstractSharedObjByDefFactory;
import org.sef4j.core.util.factorydef.DependencyObjectCreationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * java.nio helper class for watching on dir (not file in jdk?!), and convert calls to poll() into EventSender.sendEvent()
 * 
 * <PRE>
 * 
 *   start() = register watch
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
 *   stop = unregister watch
 *    ----->
 * 
 * </PRE>
 * 
 */
public class ChangedFileWatchPollingEventProvider extends AbstractPollingEventProvider<FileChangeEvent> implements IStartableSupport {

	private static final Logger LOG = LoggerFactory.getLogger(ChangedFileWatchPollingEventProvider.class);
	
	private Path watchPath;
	
	private WatchService watchService;	
	private WatchKey watchKey;
	
	// ------------------------------------------------------------------------
	
	public ChangedFileWatchPollingEventProvider(Path watchPath) {
		super("FileWatch");
		this.watchPath = watchPath;
	}

	// ------------------------------------------------------------------------

	@Override
	public boolean isStarted() {
		return watchKey != null;
	}
	
	@Override
	public void start() {
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

	@Override
	public void stop() {
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
	
	@Override
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
	
	// ------------------------------------------------------------------------
	
	public static class Factory extends AbstractSharedObjByDefFactory<ChangedFileWatchPollingEventProviderDef,ChangedFileWatchPollingEventProvider> {
		
		public Factory() {
			super("ChangedFileWatchPollingEventProvider", ChangedFileWatchPollingEventProviderDef.class);
		}

		@Override
		public ChangedFileWatchPollingEventProvider create(ChangedFileWatchPollingEventProviderDef def, DependencyObjectCreationContext ctx) {
			return new ChangedFileWatchPollingEventProvider(Paths.get(def.getFilePath()));
		}			
	}
	
}
