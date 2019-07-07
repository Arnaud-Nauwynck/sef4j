package org.sef4j.slf4jbridge.event2slf4j;

import org.slf4j.Logger;

/**
 * simple caller for <code>log.info(msgPrefix + event + msgSuffix)</code>
 * 
 * @param <T>
 */
public class DefaultToStringLogInfoCaller<T> implements EventToSlf4jLogCaller<T> {

    private String msgPrefix;
    private String msgSuffix;

    public DefaultToStringLogInfoCaller(String msgPrefix, String msgSuffix) {
	this.msgPrefix = (msgPrefix != null) ? msgPrefix : "";
	this.msgSuffix = (msgSuffix != null) ? msgSuffix : "";
    }

    @Override
    public void logTo(T event, Logger slf4jLogger) {
	String msg = msgPrefix + event + msgSuffix;
	slf4jLogger.info(msg);
    }

}