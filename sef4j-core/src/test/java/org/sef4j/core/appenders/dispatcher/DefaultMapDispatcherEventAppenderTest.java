package org.sef4j.core.appenders.dispatcher;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.core.appenders.InMemoryEventAppender;
import org.sef4j.core.appenders.dispatcher.DefaultMapDispatcherEventAppender;
import org.sef4j.core.appenders.dispatcher.KeyEventPair;

public class DefaultMapDispatcherEventAppenderTest {

    protected static class E {
    }

    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";

    protected InMemoryEventAppender<E> targetDefaultSender = new InMemoryEventAppender<E>();
    protected InMemoryEventAppender<E> targetKey1Sender = new InMemoryEventAppender<E>();
    protected InMemoryEventAppender<E> targetKey2Sender = new InMemoryEventAppender<E>();
    protected DefaultMapDispatcherEventAppender<String, E> sut = new DefaultMapDispatcherEventAppender<String, E>();

    protected E e1 = new E();
    protected KeyEventPair<String, E> wrappedE1 = new KeyEventPair<String, E>(KEY1, e1);
    protected E e2 = new E();
    protected KeyEventPair<String, E> wrappedE2 = new KeyEventPair<String, E>(KEY2, e2);

    @Test
    public void testSendEvent_dispatchToDefault() {
	// Prepare
	// Perform
	sut.sendEvent(wrappedE1);
	// Post-check
	List<E> receivedDefaultEvents = targetDefaultSender.clearAndGet();
	Assert.assertEquals(0, receivedDefaultEvents.size());

	// Prepare
	sut.setDefaultEventSender(targetDefaultSender);
	// Perform
	sut.sendEvent(wrappedE1);
	// Post-check
	receivedDefaultEvents = targetDefaultSender.clearAndGet();
	Assert.assertEquals(1, receivedDefaultEvents.size());
	Assert.assertSame(e1, receivedDefaultEvents.get(0));

    }

    @Test
    public void testSendEvent_wrappedKey1_dispatchToTarget1() {
	// Prepare
	sut.putEventSenderDispatcher(KEY1, targetKey1Sender);
	// Perform
	sut.sendEvent(wrappedE1);
	// Post-check
	List<E> receivedTarget1Events = targetKey1Sender.clearAndGet();
	Assert.assertEquals(1, receivedTarget1Events.size());
	Assert.assertSame(e1, receivedTarget1Events.get(0));

	List<E> receivedTarget2Events = targetKey2Sender.clearAndGet();
	Assert.assertEquals(0, receivedTarget2Events.size());
    }

    @Test
    public void testSendEvent_wrappedKey1_dispatchE2ToDefault() {
	// Prepare
	sut.putEventSenderDispatcher(KEY1, targetKey1Sender);
	sut.setDefaultEventSender(targetDefaultSender);
	// Perform
	sut.sendEvent(wrappedE2);
	// Post-check
	List<E> receivedDefaultEvents = targetDefaultSender.clearAndGet();
	Assert.assertEquals(1, receivedDefaultEvents.size());
	Assert.assertSame(e2, receivedDefaultEvents.get(0));
    }

    @Test
    public void testSendEvents_wrappedKey1_dispatchToTarget1() {
	// Prepare
	sut.putEventSenderDispatcher(KEY1, targetKey1Sender);
	sut.putEventSenderDispatcher(KEY2, targetKey2Sender);
	sut.setDefaultEventSender(targetDefaultSender);
	// Perform
	sut.sendEvents(Arrays.asList(new KeyEventPair[] { wrappedE1, wrappedE1, wrappedE2 }));

	// Post-check
	List<E> receivedTarget1Events = targetKey1Sender.clearAndGet();
	Assert.assertEquals(2, receivedTarget1Events.size());
	Assert.assertSame(e1, receivedTarget1Events.get(0));
	Assert.assertSame(e1, receivedTarget1Events.get(1));

	List<E> receivedTarget2Events = targetKey2Sender.clearAndGet();
	Assert.assertEquals(1, receivedTarget2Events.size());
	Assert.assertSame(e2, receivedTarget2Events.get(0));

	List<E> receivedDefaultEvents = targetDefaultSender.clearAndGet();
	Assert.assertEquals(0, receivedDefaultEvents.size());
    }

}
