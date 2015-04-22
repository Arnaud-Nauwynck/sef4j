package org.sef4j.core.helpers.exporters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;



public class HttpPostBytesSenderTest {

    private final HttpURLConnection mockCon = Mockito.mock(HttpURLConnection.class);
    private HttpPostBytesSender sut;
    
    @Before
    public void setup() {
        URL dummyURL;
        try {
            dummyURL = new URL("http://dummyURL/dummyResource?user=toto&password=toto");
        } catch (MalformedURLException e) {
            throw new RuntimeException();
        }
        this.sut = new HttpPostBytesSender("sender to http://dummyURL/dummyResource", dummyURL, null, null) {
            @Override
            protected HttpURLConnection openHttpURLConnection() {
                return mockCon;
            }
        };
    }
    
    @Test
    public void testSendEvent() throws IOException {
        // Prepare
        ByteArrayOutputStream mockConOutput = new ByteArrayOutputStream();
        Mockito.when(mockCon.getOutputStream()).thenReturn(mockConOutput);
        Mockito.when(mockCon.getResponseCode()).thenReturn(200);
        
        String jsonFragment = "{ test: 1 }";
        // Perform
        sut.sendEvent(jsonFragment.getBytes());
        // Post-check
        Mockito.verify(mockCon).getOutputStream();
        Mockito.verify(mockCon).getResponseCode();
        Assert.assertEquals(jsonFragment, mockConOutput.toString());
    }

}
