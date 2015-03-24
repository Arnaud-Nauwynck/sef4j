package org.sef4j.callstack.stattree.changecollector;

import java.util.Map;

public interface ICallTreeValueChangeCollector<T> {

	public Map<String,T> markAndCollectChanges();

}
