package org.sef4j.core.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AsyncUtils {

	private static ScheduledExecutorService defaultScheduledThreadPool;
	
	public static ScheduledExecutorService defaultScheduledThreadPool() {
		if (defaultScheduledThreadPool == null) {
			defaultScheduledThreadPool = Executors.newScheduledThreadPool(1);
		}
		return defaultScheduledThreadPool;
	}

	
}
