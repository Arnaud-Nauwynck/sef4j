package com.google.code.joto.testapp;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * default implementation for IFooService
 */
@Component
public class FooServiceImpl implements IFooService {
	
	private static Logger log = LoggerFactory.getLogger(FooServiceImpl.class);

	// ------------------------------------------------------------------------

	public FooServiceImpl() {
	}

	// ------------------------------------------------------------------------
	
	public void foo() {
		log.info("foo");
	}
	
	public int methInt(int arg1, int arg2) {
		log.info("methInt " + arg1 + ", " +arg2);
		return arg1 + arg2;
	}

	public double methDouble(double arg1, double arg2) {
		log.info("methDouble " + arg1 + ", " +arg2);
		return arg1 + arg2;
	}

	public java.util.Date methDate(java.util.Date arg1, int shift) {
		log.info("methDate " + arg1 + ", " + shift);
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(arg1);
		cal.add(Calendar.DAY_OF_MONTH, shift);
		Date res = cal.getTime();
		return res;
	}

	public Object methObj(Object obj) {
		log.info("methObj " + obj);
		return obj;
	}

}
