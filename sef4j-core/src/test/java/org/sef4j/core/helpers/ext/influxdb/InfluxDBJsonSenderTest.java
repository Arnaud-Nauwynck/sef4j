package org.sef4j.core.helpers.ext.influxdb;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.ArrayEquals;
import org.sef4j.core.helpers.ext.influxdb.InfluxDBJsonSender;
import org.sef4j.core.helpers.senders.http.HttpPostBytesSender;
import org.sef4j.core.helpers.senders.http.HttpPostBytesSenderFactory;


public class InfluxDBJsonSenderTest {

    protected HttpPostBytesSender mockHttpPostSender = Mockito.mock(HttpPostBytesSender.class);
    
    protected InfluxDBJsonSender sut = new InfluxDBJsonSender("http://dummyurl", "dummyDb", "dummyUser", "dummyPassword",
        new HttpPostBytesSenderFactory() {
        @Override
        public HttpPostBytesSender create(String displayName, URL url, String contentType, Map<String, String> headers) {
            return mockHttpPostSender;
        }
    });

    @Test
    public void testSendEvent() throws IOException {
        // Prepare
        Mockito.doNothing().when(mockHttpPostSender).sendEvent(Mockito.any(byte[].class));
        String msg1 = "{ test: 1 }";
        // Perform
        sut.sendEvent(msg1);
        // Post-check
        byte[] expectedJsonBody = ("[\n" + msg1 + "\n]").getBytes();
        Mockito.verify(mockHttpPostSender).sendEvent((byte[]) Mockito.argThat(new ArrayEquals(expectedJsonBody)));
    }

    
    @Test
    public void testSendEvents() throws IOException {
        // Prepare
        Mockito.doNothing().when(mockHttpPostSender).sendEvent(Mockito.any(byte[].class));
        String msg1 = "{ test: 1 }";
        String msg2 = "{ test: 2 }";
        List<String> jsonFragments = Arrays.asList(msg1, msg2);
        // Perform
        sut.sendEvents(jsonFragments);
        // Post-check
        byte[] expectedJsonBody = ("[\n" + msg1 + ",\n" + msg2 + "\n]").getBytes();
        Mockito.verify(mockHttpPostSender).sendEvent((byte[]) Mockito.argThat(new ArrayEquals(expectedJsonBody)));
    }



}
