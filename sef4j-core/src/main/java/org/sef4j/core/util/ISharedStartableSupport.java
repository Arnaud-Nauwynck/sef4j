package org.sef4j.core.util;

public interface ISharedStartableSupport {

	public Handle registerStart();

	public void unregisterStop(Handle handle);

}
