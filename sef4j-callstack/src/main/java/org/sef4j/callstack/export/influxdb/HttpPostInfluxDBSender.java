package org.sef4j.callstack.export.influxdb;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class HttpPostInfluxDBSender extends InfluxDBJsonSender {

    
    private static final Logger LOG = LoggerFactory.getLogger(HttpPostInfluxDBSender.class);
    
    private static final Charset UTF8 = Charset.forName("UTF-8");
    
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
    public void sendJSonBody(String json) {
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
            OutputStreamWriter outWriter = new OutputStreamWriter(conOutput, UTF8);
            outWriter.write(json);
            outWriter.flush();
            // conOutput.write(json.getBytes());
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
