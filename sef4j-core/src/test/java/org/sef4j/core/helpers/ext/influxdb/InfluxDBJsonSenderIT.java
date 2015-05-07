package org.sef4j.core.helpers.ext.influxdb;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.sef4j.core.helpers.ext.influxdb.InfluxDBJsonSender;
import org.sef4j.core.helpers.senders.http.HttpPostBytesSenderFactory;

@Ignore
public class InfluxDBJsonSenderIT extends AbstractInfluxDBSerieSenderIT {

    private InfluxDBJsonSender sut = new InfluxDBJsonSender(url, dbName, username, password, 
            HttpPostBytesSenderFactory.DEFAULT_FACTORY);

    @Test
    public void testSendJSonBody() {
        // Prepare
        String json = "[ { \"name\": \"metric1\", \"columns\": [\"field1\", \"field2\", \"stringField1\" ], \"points\": [ [11.5, 20.6, \"test1\"] ] } ]";
        // Perform
        sut.sendJSonBody(json.getBytes());
        // Post-check
    }

    @Test
    public void testSendJSonBody_invalidJson() {
        // Prepare
        String json = "[ invalid json ]";
        // Perform
        try {
            sut.sendJSonBody(json.getBytes());
            Assert.fail();
        } catch(RuntimeException ex) {
            // OK
            String exMsg = ex.getMessage();
            Assert.assertEquals("Failed to POST json to 'http://localhost:8086', response code:400 msg:Bad Request", exMsg);
        }
        // Post-check
    }

}
