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
public class HttpPostInfluxDBSender extends InfluxDBJsonSender {
    
    private static final Logger LOG = LoggerFactory.getLogger(HttpPostInfluxDBSender.class);
    
	private URL seriesURL;
	
    // ------------------------------------------------------------------------
    
    public HttpPostInfluxDBSender(String url, String dbName, String username, String password) {
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
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) seriesURL.openConnection();
        } catch(IOException ex) {
            LOG.warn("Failed to connect to '" + url + "', ex:" + ex.getMessage() + " ... rethrow");
            throw new RuntimeException("Failed to connect to '" + url +"'", ex);
        }
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
                throw new RuntimeException("Failed to POST json to '" + url + "', response code:" + responseCode + " msg:" + responseMsg);
            }
            
        } catch(IOException ex) {
            throw new RuntimeException("Failed to POST json to '" + url + "', ex:" + ex.getMessage());
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }
    
}
