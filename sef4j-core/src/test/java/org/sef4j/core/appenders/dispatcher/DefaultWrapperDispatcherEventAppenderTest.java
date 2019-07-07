package org.sef4j.core.appenders.dispatcher;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.api.EventAppender;
import org.sef4j.core.appenders.InMemoryEventAppender;
import org.sef4j.core.appenders.dispatcher.DefaultWrapperDispatcherEventAppender;
import org.sef4j.core.appenders.dispatcher.KeyEventPair;

public class DefaultWrapperDispatcherEventAppenderTest {

    protected static class E {
    }

    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    protected E e1 = new E();
    protected E e2 = new E();

    protected InMemoryEventAppender<KeyEventPair<String, E>> targetSender = new InMemoryEventAppender<KeyEventPair<String, E>>();
    protected DefaultWrapperDispatcherEventAppender<String, E> sut = new DefaultWrapperDispatcherEventAppender<String, E>(
	    targetSender);

    @Test
    public void testMultiplexSendEvent() {
	// Prepare
	// Perform
	sut.multiplexSendEvent(KEY1, e1);
	sut.multiplexSendEvent(KEY2, e2);
	// Post-check
	List<KeyEventPair<String, E>> res = targetSender.clearAndGet();
	Assert.assertEquals(2, res.size());
	KeyEventPair<String, E> res0 = res.get(0);
	Assert.assertEquals(KEY1, res0.getKey());
	Assert.assertSame(e1, res0.getWrappedEvent());

	KeyEventPair<String, E> res1 = res.get(1);
	Assert.assertEquals(KEY2, res1.getKey());
	Assert.assertSame(e2, res1.getWrappedEvent());
    }

    @Test
    public void testMultiplexSendEvents() {
	// Prepare
	// Perform
	sut.multiplexSendEvents(KEY1, Arrays.asList(new E[] { e1, e2 }));
	// Post-check
	List<KeyEventPair<String, E>> res = targetSender.clearAndGet();
	Assert.assertEquals(2, res.size());
	KeyEventPair<String, E> res0 = res.get(0);
	Assert.assertEquals(KEY1, res0.getKey());
	Assert.assertSame(e1, res0.getWrappedEvent());

	KeyEventPair<String, E> res1 = res.get(1);
	Assert.assertEquals(KEY1, res1.getKey());
	Assert.assertSame(e2, res1.getWrappedEvent());
    }

    @Test
    public void testEventSenderFor_sendEvent() {
	// Prepare
	EventAppender<E> senderForKey1 = sut.eventSenderFor(KEY1);
	// Perform
	senderForKey1.sendEvent(e1);
	// Post-check
	List<KeyEventPair<String, E>> res = targetSender.clearAndGet();
	Assert.assertEquals(1, res.size());
	KeyEventPair<String, E> res0 = res.get(0);
	Assert.assertEquals(KEY1, res0.getKey());
	Assert.assertSame(e1, res0.getWrappedEvent());
    }

}
