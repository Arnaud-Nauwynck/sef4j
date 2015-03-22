package org.sef4j.callstack.export.influxdb;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class HttpPostInfluxDBJsonSender extends InfluxDBJsonSender {
    
    private static final Logger LOG = LoggerFactory.getLogger(HttpPostInfluxDBJsonSender.class);
    
	private URL seriesURL;
	
    // ------------------------------------------------------------------------
    
    public HttpPostInfluxDBJsonSender(String url, String dbName, String username, String password) {
        super(url);
        try {
			this.seriesURL = new URL(url + "/db/" + dbName + "/series?u=" + username + "&p=" + password);
		} catch (MalformedURLException ex) {
			throw new RuntimeException("Bad url", ex);
		}
    }

    // ------------------------------------------------------------------------
    
    @Override
    protected void doSendJSonBody(byte[] jsonData) {
        HttpURLConnection con = openHttpURLConnection(seriesURL);
        try {
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            
            OutputStream conOutput = con.getOutputStream();
            
            conOutput.write(jsonData);
            
            conOutput.close();
            
            // con.setReadTimeout(10000);
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                // OK!
            } else {
                String responseMsg = con.getResponseMessage();
                throw new RuntimeException("Failed to POST json to '" + displayUrl + "', response code:" + responseCode + " msg:" + responseMsg);
            }
            
        } catch(IOException ex) {
            throw new RuntimeException("Failed to POST json to '" + displayUrl + "', ex:" + ex.getMessage());
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    protected HttpURLConnection openHttpURLConnection(URL url) {
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch(IOException ex) {
            // do not display real seriesURL...it contains user/password!
            LOG.warn("Failed to connect to '" + displayUrl + "', ex:" + ex.getMessage() + " ... rethrow"); 
            throw new RuntimeException("Failed to connect to '" + displayUrl +"'", ex);
        }
        return con;
    }
    
}
