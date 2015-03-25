package org.sef4j.core.helpers.proptree.changes;

import java.util.Map;

public interface IPropTreeValueChangeCollector<T> {

	public Map<String,T> markAndCollectChanges();

}
