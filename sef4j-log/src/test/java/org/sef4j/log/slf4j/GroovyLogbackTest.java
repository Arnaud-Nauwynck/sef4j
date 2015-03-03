package org.sef4j.log.slf4j;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroovyLogbackTest {

    
    private static final Logger LOG = LoggerFactory.getLogger(GroovyLogbackTest.class);
    
    @Test
    public void testLog() {
        LOG.info("test");
    }
}
