package org.sef4j.core.appenders;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.core.appenders.InMemoryEventAppender;
import org.sef4j.core.appenders.AbstractFilterEventAppender.PredicateFilterEventSender;

public class AbstractFilterEventAppenderTest {

    private static class E {
	public final String value;

	public E(String value) {
	    this.value = value;
	}
    }

    protected Predicate<E> predicate1 = (x) -> x.value.endsWith("1");
    protected InMemoryEventAppender<E> targetEventSender = new InMemoryEventAppender<E>();
    protected PredicateFilterEventSender<E> sut = new PredicateFilterEventSender<E>(targetEventSender, predicate1);

    E e1 = new E("event1");
    E e2 = new E("event2");

    @Test
    public void testSendEvent() {
	// Prepare
	// Perform
	sut.sendEvent(e1);
	// Post-check
	List<E> filteredEvents = targetEventSender.clearAndGet();
	Assert.assertEquals(1, filteredEvents.size());
	Assert.assertSame(e1, filteredEvents.get(0));

	// Perform
	sut.sendEvent(e2);
	// Post-check
	filteredEvents = targetEventSender.clearAndGet();
	Assert.assertEquals(0, filteredEvents.size());
    }

    @Test
    public void testSendEvents() {
	// Prepare
	// Perform
	sut.sendEvents(Arrays.asList(e1, e2, e1, e2));
	// Post-check
	List<E> filteredEvents = targetEventSender.clearAndGet();
	Assert.assertEquals(2, filteredEvents.size());
	Assert.assertSame(e1, filteredEvents.get(0));
	Assert.assertSame(e1, filteredEvents.get(1));
    }

}
