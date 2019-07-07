package org.sef4j.core.appenders.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.sef4j.api.EventAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EventSender for sending byte[] as http POST requests
 * 
 * <PRE>
 *          EventSender(byte[])           
 *              +---------------------+
 * sendEvent    |    - toURL          |      http POST /url
 *  ----->      |    - headers        |   --------------------->           Http Server           
 *  byte[]      |    - pooledCon      |        Content-Type=contentType       (Elasticsearch, InfluxDB, MongoDB, others ...)
 *              +---------------------+        .
 *                                             "byte[]..."
 * </PRE>
 */
public class HttpPostBytesSender implements EventAppender<byte[]> {

    private static final Logger LOG = LoggerFactory.getLogger(HttpPostBytesSender.class);

    public static final String CONTENT_TYPE_JSON = "application/json";

    private String displayName;

    private URL toURL;

    private String contentType;
    private Map<String, String> headers;

    // ?? may keep open connection with java.net.HttpURLConnection
    private boolean closeImmediateCon = true;
    private HttpURLConnection pooledCon;

    // ------------------------------------------------------------------------

    public HttpPostBytesSender(String displayName, URL toURL, String contentType, Map<String, String> headers) {
	this.displayName = displayName;
	this.toURL = toURL;
	this.contentType = contentType;
	this.headers = headers;
    }

    // ------------------------------------------------------------------------

    public void sendEvent(byte[] data) {
	HttpURLConnection con = openHttpURLConnection();
	try {
	    doSendContent(con, data);
	} finally {
	    releaseHttpURLConnection(con);
	}
    }

    public void sendEvents(Collection<byte[]> datas) {
	HttpURLConnection con = openHttpURLConnection();
	try {
	    for (byte[] data : datas) {
		doSendContent(con, data);
	    }
	} finally {
	    releaseHttpURLConnection(con);
	}
    }

    protected void doSendContent(HttpURLConnection con, byte[] data) {
	try {
	    con.setDoOutput(true);
	    con.setRequestMethod("POST");
	    if (contentType != null) {
		con.setRequestProperty("Content-Type", contentType);
	    }
	    if (headers != null) {
		for (Map.Entry<String, String> e : headers.entrySet()) {
		    con.setRequestProperty(e.getKey(), e.getValue());
		}
	    }

	    OutputStream conOutput = con.getOutputStream();

	    conOutput.write(data);

	    conOutput.close();

	    // con.setReadTimeout(10000);
	    int responseCode = con.getResponseCode();
	    if (responseCode == 200) {
		// OK!
	    } else {
		String responseMsg = con.getResponseMessage();
		throw new RuntimeException("Failed to POST json to '" + displayName + "', response code:" + responseCode
			+ " msg:" + responseMsg);
	    }

	} catch (IOException ex) {
	    throw new RuntimeException("Failed to POST json to '" + displayName + "', ex:" + ex.getMessage());
	}
    }

    protected HttpURLConnection openHttpURLConnection() {
	HttpURLConnection con;
	if (pooledCon != null) {
	    con = pooledCon;
	} else {
	    try {
		con = (HttpURLConnection) toURL.openConnection();
		pooledCon = con;
	    } catch (IOException ex) {
		// do not display real seriesURL...it contains user/password!
		LOG.warn("Failed to connect to '" + displayName + "', ex:" + ex.getMessage() + " ... rethrow");
		throw new RuntimeException("Failed to connect to '" + displayName + "'", ex);
	    }
	}
	return con;
    }

    protected void releaseHttpURLConnection(HttpURLConnection con) {
	if (con != null) {
	    if (closeImmediateCon) {
		pooledCon = null;
		try {
		    con.disconnect();
		} catch (Exception ex) {
		    // ignore, no rethrow!
		}
	    }
	}
    }
}
