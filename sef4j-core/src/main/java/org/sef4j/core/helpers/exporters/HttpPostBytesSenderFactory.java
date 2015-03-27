package org.sef4j.core.helpers.exporters;

import java.net.URL;
import java.util.Map;

/**
 * Factory for HttpPostBytesSender
 * <p/>
 * 
 * the DEFAULT_FACTORY is a default, for using javax.net.HttpURLConnection (jdk) implementation
 * <br/>
 * You might prefer apache http component implementation
 */
public interface HttpPostBytesSenderFactory {

    public HttpPostBytesSender create(String displayName, URL url, String contentType, Map<String,String> headers);

    
    public static class DefaultHttpPostBytesSenderFactory implements HttpPostBytesSenderFactory {

        @Override
        public HttpPostBytesSender create(String displayName, URL url, String contentType, Map<String, String> headers) {
            return new HttpPostBytesSender(displayName, url, contentType, headers);
        }
        
    }
    
    public static final HttpPostBytesSenderFactory DEFAULT_FACTORY = new DefaultHttpPostBytesSenderFactory();
    
}
