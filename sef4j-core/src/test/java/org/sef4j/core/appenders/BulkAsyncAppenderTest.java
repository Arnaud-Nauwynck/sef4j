package org.sef4j.core.appenders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.api.EventAppender;
import org.sef4j.core.appenders.BulkAsyncAppender;

public class BulkAsyncAppenderTest {

    private static class E {
	public final int id;

	private E(int id) {
	    this.id = id;
	}

	@Override
	public String toString() {
	    return "E [" + id + "]";
	}
    }

    protected static class InMemoryBulkEventSender<T> implements EventAppender<T> {

	private List<Collection<T>> eventsBulks = new ArrayList<Collection<T>>();

	@Override
	public void sendEvent(T event) {
	    eventsBulks.add(Collections.singleton(event));
	}

	@Override
	public void sendEvents(Collection<T> events) {
	    List<T> eventsCopy = new ArrayList<T>(events); // copy safety
	    eventsBulks.add(eventsCopy);
	}

	public List<Collection<T>> clearAndGet() {
	    List<Collection<T>> res = eventsBulks;
	    this.eventsBulks = new ArrayList<Collection<T>>();
	    return res;
	}

    }

    protected InMemoryBulkEventSender<E> targetSender = new InMemoryBulkEventSender<E>();

    protected int maxBulkEventsCount = 10;

    protected BulkAsyncAppender<E> sut = new BulkAsyncAppender.Builder<E>().flushPeriod(60) // flush after 60 seconds
	    .maxBulkEventsCount(maxBulkEventsCount).maxBulkByteLength(4000).build(targetSender);

    @Test
    public void testSend_maxBulkEventsCount_flushInThread() throws InterruptedException {
	doTestSend_maxBulkEventsCount_thread(true);
    }

    @Test
    public void testSend_maxBulkEventsCount_flushAsync() throws InterruptedException {
	doTestSend_maxBulkEventsCount_thread(false);
    }

    protected void doTestSend_maxBulkEventsCount_thread(boolean flushInCurrentThread) throws InterruptedException {
	// Prepare
	sut.setFlushFilledBulkInCurrentThread(flushInCurrentThread);

	int bulksCount = 2;
	int extraEventCount = 3;
	List<E> eList = new ArrayList<E>();
	for (int i = 0; i < bulksCount * maxBulkEventsCount + extraEventCount; i++) {
	    eList.add(new E(i));
	}
	// Perform
	for (int i = 0; i < bulksCount * maxBulkEventsCount + extraEventCount; i++) {
	    sut.sendEvent(eList.get(i));
	}
	// Post-check
	// check bulks
	// need to wait few millis for async "immediate" flush on other thread!
	waitImmediateFlush(sut, false);
	Assert.assertEquals(0, sut.getCurrentAsyncEventsBulksQueueSize());
	Assert.assertEquals(extraEventCount, sut.getCurrentBufferedEventsSize());

	List<Collection<E>> resBulks = targetSender.clearAndGet();
	Assert.assertEquals(bulksCount, resBulks.size());
	int index = 0;
	for (Collection<E> bulk : resBulks) {
	    Assert.assertEquals(maxBulkEventsCount, bulk.size());
	    for (E event : bulk) {
		Assert.assertEquals(eList.get(index), event);
		index++;
	    }
	}
	// check still buffered
	int bufferedEventsSize = sut.getCurrentBufferedEventsSize();
	Assert.assertEquals(extraEventCount, bufferedEventsSize);

	// now flush...
	// Perform
	sut.flush(true, true);
	// Post-check
	waitImmediateFlush(sut, true);
	Assert.assertEquals(0, sut.getCurrentBufferedEventsSize());
	resBulks = targetSender.clearAndGet();
	Assert.assertEquals(1, resBulks.size());
	Collection<E> bulk = resBulks.get(0);
	Assert.assertEquals(extraEventCount, bulk.size());
	for (E event : bulk) {
	    Assert.assertEquals(eList.get(index), event);
	    index++;
	}
    }

    private void waitImmediateFlush(BulkAsyncAppender<E> sut, boolean flushPartialBulk) {
	int remain = sut.getCurrentAsyncEventsBulksQueueSize();
	if (remain != 0) {
	    sut.waitAsyncEventBulksQueueFlushed(3, 100);
	    remain = sut.getCurrentAsyncEventsBulksQueueSize();
	}
	if (flushPartialBulk) {
	    // ? should not occurs
	    sut.flush(false, flushPartialBulk);
	}
    }

}
