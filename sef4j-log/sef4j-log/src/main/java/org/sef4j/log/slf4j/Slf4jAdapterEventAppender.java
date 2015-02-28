package org.sef4j.log.slf4j;

import org.sef4j.core.api.EventAppender;


/**
 * adapter EventLogger -> Slf4j
 */
public class Slf4jAdapterEventAppender extends EventAppender {
	
	public Slf4jAdapterEventAppender(String appenderName) {
		super(appenderName);
	}

	@Override
	public void handleEvent(Object event) {
		//TODO
		
	}

}
