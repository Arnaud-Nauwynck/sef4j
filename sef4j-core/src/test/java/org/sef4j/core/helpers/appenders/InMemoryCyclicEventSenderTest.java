package org.sef4j.core.helpers.appenders;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


public class InMemoryCyclicEventSenderTest {

    private InMemoryCyclicEventSender<Integer> sut = new InMemoryCyclicEventSender<Integer>();
    
    @Test
    public void testSendEvent() {
        // Prepare
        Integer e1 = Integer.valueOf(1);
        // Perform
        sut.sendEvent(e1);
        // Post-check
        List<Integer> ls = sut.getCopy();
        Assert.assertEquals(1, ls.size());
        Assert.assertSame(e1, ls.get(0));

        // Prepare
        Integer e2 = Integer.valueOf(2);
        // Perform
        sut.sendEvent(e2);
        // Post-check
        ls = sut.getCopy();
        Assert.assertEquals(2, ls.size());
        Assert.assertSame(e1, ls.get(0));
        Assert.assertSame(e2, ls.get(1));
    }
    
    @Test
    public void testSendEvents() {
        // Prepare
        Integer e1 = Integer.valueOf(1);
        Integer e2 = Integer.valueOf(2);
        // Perform
        sut.sendEvents(Arrays.asList(e1, e2));
        // Post-check
        List<Integer> ls = sut.getCopy();
        Assert.assertEquals(2, ls.size());
        Assert.assertSame(e1, ls.get(0));
        Assert.assertSame(e2, ls.get(1));
    }
    
    @Test
    public void testSendEvent_modulo() {
        // Prepare
        int len = sut.getMaxEventLen();
        // Perform
        int maxI = (2*len+3);
        for (int i = 0; i < maxI; i++) {
            Integer e = Integer.valueOf(i);
            sut.sendEvent(e);
        }
        // Post-check
        List<Integer> ls = sut.getCopy();
        Assert.assertEquals(len, ls.size());
        int firstInBuffer = maxI - len;
        Assert.assertEquals(firstInBuffer, (int) ls.get(0));
        for (int i = 0; i < len; i++) {
            Assert.assertEquals(firstInBuffer+i, (int) ls.get(i));
        }
    }
    
    @Test
    public void testClearAndGet() {
        // Prepare
        Integer e1 = Integer.valueOf(1);
        Integer e2 = Integer.valueOf(2);
        // Perform
        sut.sendEvent(e1);
        sut.sendEvent(e2);
        // Post-check
        List<Integer> ls = sut.clearAndGet();
        Assert.assertEquals(2, ls.size());
        Assert.assertSame(e1, ls.get(0));
        Assert.assertSame(e2, ls.get(1));
        Assert.assertEquals(0, sut.getCopy().size());
    }
    
}
