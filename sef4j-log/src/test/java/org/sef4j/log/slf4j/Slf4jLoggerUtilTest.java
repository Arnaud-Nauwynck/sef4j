package org.sef4j.log.slf4j;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class Slf4jLoggerUtilTest {
	
	private Logger slf4jLogger = Mockito.mock(Logger.class);

	
	@Test
	public void test() {
		// Prepare
		// Perform
		Assert.assertEquals(LogLevel.TRACE, Slf4jLoggerUtil.slf4jLevelToLogLevel(ch.qos.logback.classic.Level.TRACE));
		Assert.assertEquals(LogLevel.DEBUG, Slf4jLoggerUtil.slf4jLevelToLogLevel(ch.qos.logback.classic.Level.DEBUG));
		Assert.assertEquals(LogLevel.INFO, Slf4jLoggerUtil.slf4jLevelToLogLevel(ch.qos.logback.classic.Level.INFO));
		Assert.assertEquals(LogLevel.WARN, Slf4jLoggerUtil.slf4jLevelToLogLevel(ch.qos.logback.classic.Level.WARN));
		Assert.assertEquals(LogLevel.ERROR, Slf4jLoggerUtil.slf4jLevelToLogLevel(ch.qos.logback.classic.Level.ERROR));
		// Post-check
	}
	
	@Test
	public void testIsEnabled() {
		// Prepare
		Mockito.when(slf4jLogger.isErrorEnabled()).thenReturn(true);
		Mockito.when(slf4jLogger.isWarnEnabled()).thenReturn(true);
		Mockito.when(slf4jLogger.isInfoEnabled()).thenReturn(true);
		Mockito.when(slf4jLogger.isDebugEnabled()).thenReturn(false);
		Mockito.when(slf4jLogger.isTraceEnabled()).thenReturn(false);
		// Perform
		Assert.assertTrue(Slf4jLoggerUtil.isEnabled(slf4jLogger, LogLevel.ERROR));
		Assert.assertTrue(Slf4jLoggerUtil.isEnabled(slf4jLogger, LogLevel.WARN));
		Assert.assertTrue(Slf4jLoggerUtil.isEnabled(slf4jLogger, LogLevel.INFO));
		Assert.assertFalse(Slf4jLoggerUtil.isEnabled(slf4jLogger, LogLevel.DEBUG));
		Assert.assertFalse(Slf4jLoggerUtil.isEnabled(slf4jLogger, LogLevel.TRACE));
		// Post-check
	}
	
	@Test
	public void testLogLevelText() {
		// Prepare
		String msg = "test";
		Mockito.doNothing().when(slf4jLogger).trace(Mockito.eq(msg));
		Mockito.doNothing().when(slf4jLogger).debug(Mockito.eq(msg));
		Mockito.doNothing().when(slf4jLogger).info(Mockito.eq(msg));
		Mockito.doNothing().when(slf4jLogger).warn(Mockito.eq(msg));
		Mockito.doNothing().when(slf4jLogger).error(Mockito.eq(msg));
		// Perform
		Slf4jLoggerUtil.logLevelText(slf4jLogger, LogLevel.TRACE, msg);
		Slf4jLoggerUtil.logLevelText(slf4jLogger, LogLevel.DEBUG, msg);
		Slf4jLoggerUtil.logLevelText(slf4jLogger, LogLevel.INFO, msg);
		Slf4jLoggerUtil.logLevelText(slf4jLogger, LogLevel.WARN, msg);
		Slf4jLoggerUtil.logLevelText(slf4jLogger, LogLevel.ERROR, msg);
		// Post-check
		Mockito.verify(slf4jLogger).trace(msg);
		Mockito.verify(slf4jLogger).debug(msg);
		Mockito.verify(slf4jLogger).info(msg);
		Mockito.verify(slf4jLogger).warn(msg);
		Mockito.verify(slf4jLogger).error(msg);
	}

	@Test
	public void testLogLevelTextEx() {
		// Prepare
		Exception ex = new Exception();
		String msg = "test ex";
		// Prepare
		Mockito.doNothing().when(slf4jLogger).trace(Mockito.eq(msg), Mockito.eq(ex));
		Mockito.doNothing().when(slf4jLogger).debug(Mockito.eq(msg), Mockito.eq(ex));
		Mockito.doNothing().when(slf4jLogger).info(Mockito.eq(msg), Mockito.eq(ex));
		Mockito.doNothing().when(slf4jLogger).warn(Mockito.eq(msg), Mockito.eq(ex));
		Mockito.doNothing().when(slf4jLogger).error(Mockito.eq(msg), Mockito.eq(ex));
		// Perform
		Slf4jLoggerUtil.logLevelTextException(slf4jLogger, LogLevel.TRACE, msg, ex);
		Slf4jLoggerUtil.logLevelTextException(slf4jLogger, LogLevel.DEBUG, msg, ex);
		Slf4jLoggerUtil.logLevelTextException(slf4jLogger, LogLevel.INFO, msg, ex);
		Slf4jLoggerUtil.logLevelTextException(slf4jLogger, LogLevel.WARN, msg, ex);
		Slf4jLoggerUtil.logLevelTextException(slf4jLogger, LogLevel.ERROR, msg, ex);
		// Post-check
		Mockito.verify(slf4jLogger).trace(msg, ex);
		Mockito.verify(slf4jLogger).debug(msg, ex);
		Mockito.verify(slf4jLogger).info(msg, ex);
		Mockito.verify(slf4jLogger).warn(msg, ex);
		Mockito.verify(slf4jLogger).error(msg, ex);
	}

}
