package org.sef4j.core.helpers.files;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.sef4j.core.api.ioeventchain.DefaultInputEventChainDefs.ChangedFileWatchInputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChain;
import org.sef4j.core.api.ioeventchain.InputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChainFactory;
import org.sef4j.core.helpers.tasks.PeriodicTask;
import org.sef4j.core.util.AsyncUtils;
import org.sef4j.core.util.factorydef.ObjectByDefRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InputEventChain adapter for polling on file change
 * 
 * @seealso ChangedFileWatchToEventProvider
 */
public class ChangedFileWatchInputEventChain extends InputEventChain<FileWatchChangeEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(ChangedFileWatchInputEventChain.class);
	
	// private final ContentFileWatchInputEventChainDef def ... = (ContentFileWatchInputEventChainDef) super.def;

	private ChangedFileWatchToEventProvider fileWatchEventProvider;
	
	// ------------------------------------------------------------------------
	
	public ChangedFileWatchInputEventChain(ChangedFileWatchInputEventChainDef def, String displayName) {
		super(def, displayName);
		Path watchPath = Paths.get(getDef().getFilePath());
		PeriodicTask.Builder pollingPeriodBuilder = new PeriodicTask.Builder()
			.withPeriod(2).withScheduledExecutor(AsyncUtils.defaultScheduledThreadPool());
		this.fileWatchEventProvider = new ChangedFileWatchToEventProvider(watchPath, pollingPeriodBuilder);
	}

	// ------------------------------------------------------------------------

	public ChangedFileWatchInputEventChainDef getDef() {
		return (ChangedFileWatchInputEventChainDef) super.getDef(); 
	}
	
	@Override
	public boolean isStarted() {
		return fileWatchEventProvider.isStarted();
	}

	@Override
	public void start() {
		LOG.debug("start " + displayName);
		fileWatchEventProvider.start();
	}

	@Override
	public void stop() {
		LOG.debug("stop " + displayName);
		fileWatchEventProvider.stop();
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "ContentFileWatchInputEventChain [" + displayName + " " + fileWatchEventProvider + "]";
	}

	// ------------------------------------------------------------------------
	
	public static class Factory extends InputEventChainFactory<FileWatchChangeEvent> {
		
		public Factory() {
			super("ChangedFileWatchInputEventChain");
		}

		@Override
		public boolean accepts(InputEventChainDef def) {
			return def instanceof ChangedFileWatchInputEventChainDef;
		}

		@Override
		public InputEventChain<FileWatchChangeEvent> create(InputEventChainDef defObj, ObjectByDefRepository<InputEventChainDef, ?, InputEventChain<FileWatchChangeEvent>> repository) {
			ChangedFileWatchInputEventChainDef def = (ChangedFileWatchInputEventChainDef) defObj;
			ChangedFileWatchInputEventChain res = new ChangedFileWatchInputEventChain(def, "ChangedFileWatch");
			return res;
		}
		
	}
}
