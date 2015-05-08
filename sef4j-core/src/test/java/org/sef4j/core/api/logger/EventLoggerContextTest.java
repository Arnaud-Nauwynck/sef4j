package org.sef4j.core.api.logger;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.core.api.EventSender;
import org.sef4j.core.api.logger.EventLoggerContext;
import org.sef4j.core.api.logger.EventLoggerContext.EventLoggerContextListener;
import org.sef4j.core.helpers.senders.InMemoryEventSender;


public class EventLoggerContextTest {

    private static class E {}
    
	private EventSender<E> appender1 = new InMemoryEventSender<E>();
	private EventSender<E> appender2 = new InMemoryEventSender<E>();
	private EventLoggerContext sut = new EventLoggerContext();

    private static class InnerContextListener implements EventLoggerContextListener {
        List<String> changeLoggerNames = new ArrayList<String>();
        
    	public void onChangeInheritedLoggers(String eventLoggerName) {
    		changeLoggerNames.add(eventLoggerName);
        }
    	
    	public List<String> clearAndGetChangeLoggerNames() {
    		List<String> res = new ArrayList<String>(changeLoggerNames);
    		changeLoggerNames.clear();
    		return res;
    	}
    	
    }
    private InnerContextListener listener = new InnerContextListener();
    
	@Test
	public void testGetInheritedAppendersFor() {
		// Prepare
		// Perform
		sut.addAppender("appender1", appender1);
		sut.addLoggerToAppenderRef("a.b", "appender1", true);
		sut.addAppender("appender2", appender2);
		sut.addLoggerToAppenderRef("a.b.c", "appender2", true);
		sut.addLoggerToAppenderRef("a.b.c.d", "appender1", false);
		// Post-check
		EventSender<E>[] rootAppenders = sut.getInheritedAppendersFor("");
		Assert.assertEquals(0, rootAppenders.length);
		EventSender<E>[] aAppenders = sut.getInheritedAppendersFor("a");
		Assert.assertEquals(0, aAppenders.length);
		Assert.assertSame(rootAppenders, aAppenders); // memory optim check.. share array res
		EventSender<E>[] abAppenders = sut.getInheritedAppendersFor("a.b");
		Assert.assertEquals(1, abAppenders.length);
		Assert.assertSame(appender1, abAppenders[0]);
		EventSender<E>[] abcAppenders = sut.getInheritedAppendersFor("a.b.c");
		Assert.assertEquals(2, abcAppenders.length);
		Assert.assertSame(appender1, abcAppenders[0]);
		Assert.assertSame(appender2, abcAppenders[1]);
		EventSender<E>[] abcdAppenders = sut.getInheritedAppendersFor("a.b.c.d");
		Assert.assertEquals(1, abcdAppenders.length);
		Assert.assertSame(appender2, abcdAppenders[0]);

		EventSender<E>[] azAppenders = sut.getInheritedAppendersFor("a.z");
		Assert.assertSame(aAppenders, azAppenders);// memory optim check.. share array res

		EventSender<E>[] abczAppenders = sut.getInheritedAppendersFor("a.b.c.z");
		Assert.assertSame(abcAppenders, abczAppenders);// memory optim check.. share array res

		EventSender<E>[] abcdzAppenders = sut.getInheritedAppendersFor("a.b.c.d.z");
		Assert.assertSame(abcdAppenders, abcdzAppenders); // memory optim check.. share array res
		
	}
	

	
	@Test
	public void testAddContextListener() {
		// Prepare
		sut.addContextListener(listener);
		// Perform
		sut.addAppender("appender1", appender1);
		// Post-check		
		assertListenerChanges(listener);
		
		// Prepare
		// Perform
		sut.addLoggerToAppenderRef("a.b", "appender1", true);
		// Post-check
		assertListenerChanges(listener, "a.b");
		
		// Prepare
		// Perform
		sut.addAppender("appender2", appender2);
		// Post-check
		assertListenerChanges(listener);

		// Prepare
		// Perform
		sut.addLoggerToAppenderRef("a.b.c", "appender2", true);
		// Post-check
		assertListenerChanges(listener, "a.b.c");
		
		// Prepare
		// Perform
		sut.addLoggerToAppenderRef("a.b.c.d", "appender1", false);
		// Post-check
		assertListenerChanges(listener, "a.b.c.d");		
	}

	
	
	private static void assertListenerChanges(InnerContextListener listener, String... expectedChanges) {
		List<String> actualChanges = listener.clearAndGetChangeLoggerNames();
		int len = Math.min(expectedChanges.length, actualChanges.size()); // see assert below
		for (int i = 0; i < len; i++) {
			Assert.assertEquals(expectedChanges[i], actualChanges.get(i));
		}
		Assert.assertEquals(expectedChanges.length, actualChanges.size());
	}
	
}
