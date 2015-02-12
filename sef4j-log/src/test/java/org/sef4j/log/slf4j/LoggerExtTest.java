package org.sef4j.log.slf4j;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.sef4j.log.slf4j.LoggerExt;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class LoggerExtTest {
	
	@Mock
	private Logger mockLogger;
	
	@InjectMocks
	protected LoggerExt sut;
	
	@Before
	public void setup() {
		sut = new LoggerExt(mockLogger);
	}
	
	@Test
	public void testParseEndingName() {
		Assert.assertEquals("param1", LoggerExt.parseEndingName("hello param1:"));
		Assert.assertEquals("param1", LoggerExt.parseEndingName("hello param1: "));
		Assert.assertEquals("param1", LoggerExt.parseEndingName("hello param1="));
		Assert.assertEquals("param1", LoggerExt.parseEndingName("hello param1= "));
		Assert.assertEquals("param1", LoggerExt.parseEndingName("hello {{param1}}"));
		Assert.assertEquals("param1", LoggerExt.parseEndingName("hello {{param1}} "));
	}

	@Test
	public void testParseEndingName_shouldReturnNull() {
		Assert.assertNull(LoggerExt.parseEndingName("hello param1"));
		Assert.assertNull(LoggerExt.parseEndingName("hello param1 "));
	}
	
	@Test
	public void testInfoNV_1param() {
		// Mockito.reset(mockLogger);
		Mockito.when(mockLogger.isInfoEnabled()).thenReturn(true);
		Mockito.doNothing().when(mockLogger).info("test param1: 1");
		
		sut.infoNV("test param1: ", 1);
		
		Mockito.verify(mockLogger).isInfoEnabled();
		Mockito.verify(mockLogger).info("test param1: 1");
	}
	
	@Test
	public void testInfoNV_234params() {
		Mockito.when(mockLogger.isInfoEnabled()).thenReturn(true);
		Mockito.doNothing().when(mockLogger).info("test param1: 1, param2: 2");
		sut.infoNV("test param1: ", 1, ", param2: ", 2);
		Mockito.verify(mockLogger).info("test param1: 1, param2: 2");

		Mockito.when(mockLogger.isInfoEnabled()).thenReturn(true);
		Mockito.doNothing().when(mockLogger).info("test param1: 1, param2: 2, p3: 3");
		sut.infoNV("test param1: ", 1, ", param2: ", 2, ", p3: {{param3}}", 3);
		Mockito.verify(mockLogger).info("test param1: 1, param2: 2, p3: 3");

		Mockito.when(mockLogger.isInfoEnabled()).thenReturn(true);
		Mockito.doNothing().when(mockLogger).info("test param1: 1, param2: 2, p3:3, p4:4");
		sut.infoNV("test param1: ", 1, ", param2: ", 2, ", p3:{{param3}}", 3, ", p4:", 4);
		Mockito.verify(mockLogger).info("test param1: 1, param2: 2, p3:3, p4:4");
	}

	@Test
	public void testInfoNV_5params() {
		Mockito.when(mockLogger.isInfoEnabled()).thenReturn(true);
		Mockito.doNothing().when(mockLogger).info("test p1:1, p2:2, p3:3, p4:4, p5:5");

		sut.infoNV("test p1:", 1, ", p2:", 2, ", p3:", 3, ", p4:", 4, ", p5:", 5);
		
		Mockito.verify(mockLogger).info("test p1:1, p2:2, p3:3, p4:4, p5:5");
	}
	
}
