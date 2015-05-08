package org.sef4j.core.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * basic helper class for generating Handle
 * ... basically a thread-safe "int idGenerator" (AtomicInteger) autoincremented sequence
 */
public class HandleGenerator {

	private AtomicInteger idGenerator = new AtomicInteger(1);
	
	// ------------------------------------------------------------------------

	public HandleGenerator() {
	}

	// ------------------------------------------------------------------------

	public Handle generate() {
		int id = idGenerator.incrementAndGet();
		return new Handle(id);
	}
	
}
