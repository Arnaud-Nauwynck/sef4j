package org.sef4j.callstack.export.influxdb;

import org.junit.Test;


public class HttpPostInfluxDBSenderIT extends AbstractInfluxDBSerieSenderIT {

    @Test
    public void test() {
        // Prepare
        HttpPostInfluxDBSender sut = new HttpPostInfluxDBSender(url, dbName, username, password);
        String json = "[ { \"name\": \"metric1\", \"columns\": [\"field1\", \"field2\", \"stringField1\" ], \"points\": [ [11.5, 20.6, \"test1\"] ] } ]";
        // Perform
        sut.sendJSonBody(json.getBytes());
        // Post-check
    }
}
