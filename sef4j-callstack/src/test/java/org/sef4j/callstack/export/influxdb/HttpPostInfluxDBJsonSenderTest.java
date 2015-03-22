package org.sef4j.callstack.export.influxdb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;



public class HttpPostInfluxDBJsonSenderTest {

    @Test
    public void testSendEvent() throws IOException {
        // Prepare
        final HttpURLConnection mockCon = Mockito.mock(HttpURLConnection.class);
        ByteArrayOutputStream mockConOutput = new ByteArrayOutputStream();
        Mockito.when(mockCon.getOutputStream()).thenReturn(mockConOutput);
        Mockito.when(mockCon.getResponseCode()).thenReturn(200);
        
        HttpPostInfluxDBJsonSender sut = new HttpPostInfluxDBJsonSender("http://dummyURL", "dummyDb", "dummyUser", "dummyPassword") {
            @Override
            protected HttpURLConnection openHttpURLConnection(URL url) {
                return mockCon;
            }
        };
        String jsonFragment = "{ test: 1 }";
        // Perform
        sut.sendEvent(jsonFragment);
        // Post-check
        Mockito.verify(mockCon).getOutputStream();
        Mockito.verify(mockCon).getResponseCode();
        Assert.assertEquals("[\n" + jsonFragment + "\n]", mockConOutput.toString());
    }

    @Test
    public void testSendEvents() throws IOException {
        // Prepare
        final HttpURLConnection mockCon = Mockito.mock(HttpURLConnection.class);
        ByteArrayOutputStream mockConOutput = new ByteArrayOutputStream();
        Mockito.when(mockCon.getOutputStream()).thenReturn(mockConOutput);
        Mockito.when(mockCon.getResponseCode()).thenReturn(200);
        
        HttpPostInfluxDBJsonSender sut = new HttpPostInfluxDBJsonSender("http://dummyURL", "dummyDb", "dummyUser", "dummyPassword") {
            @Override
            protected HttpURLConnection openHttpURLConnection(URL url) {
                return mockCon;
            }
        };
        List<String> jsonFragments = Arrays.asList("{ test: 1 }", "{ test: 2}");
        // Perform
        sut.sendEvents(jsonFragments);
        // Post-check
        Mockito.verify(mockCon).getOutputStream();
        Mockito.verify(mockCon).getResponseCode();
        String expectedJsonBody = "[\n" + jsonFragments.get(0) + ",\n" + jsonFragments.get(1) + "\n]";
        Assert.assertEquals(expectedJsonBody, mockConOutput.toString());
    }

}
