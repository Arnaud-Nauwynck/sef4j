package com.google.code.joto.value2java.converters;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import com.google.code.joto.ast.beanstmt.BeanAST.BeanExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.LiteralExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.MethodApplyExpr;
import com.google.code.joto.ast.beanstmt.BeanAST.SimpleNameExpr;
import com.google.code.joto.ast.valueholder.ValueHolderAST.AbstractObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.FieldValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveFieldValueHolder;
import com.google.code.joto.reflect.ReflectUtils;
import com.google.code.joto.value2java.ObjectVHToStmtConverter;
import com.google.code.joto.value2java.VHToStmt;
import com.google.code.joto.value2java.VHToStmtConverterLookup;
import com.google.code.joto.value2java.impl.ObjectStmtInfo;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;

public class JavaUtilConverters {
	
	private static final DateConverter DateConverterInstance = new DateConverter();
	
	public static void registerDefaultConverters(VHToStmtConverterLookup p) {
		p.registerConverter(DateConverterInstance, 0);
	}

	// -------------------------------------------------------------------------

	public static class DateConverter implements ObjectVHToStmtConverter {

		protected DateConverter() {
		}

		@Override
		public boolean canConvert(Class<?> type) {
			return java.util.Date.class.isAssignableFrom(type)
				|| java.sql.Date.class.isAssignableFrom(type);
		}

		@Override
		public void convert(VHToStmt owner, AbstractObjectValueHolder obj,
				ObjectStmtInfo objInfo) {
			if (obj instanceof ObjectValueHolder) {
				ObjectValueHolder vh = (ObjectValueHolder) obj;
				Class<?> objClass = obj.getObjClass();
				Map<Field, FieldValueHolder> fieldsValuesMap = vh.getFieldsValuesMap();
				final ReflectionProvider reflectionProvider = ReflectUtils.getReflectionProvider();
				Field timeField = reflectionProvider.getField(java.util.Date.class, "fastTime");
				PrimitiveFieldValueHolder timeFVH = (PrimitiveFieldValueHolder) fieldsValuesMap.get(timeField);
				long timeValue = (Long) timeFVH.getValue();
				
				BeanExpr expr = doConvertValue(owner, obj, objInfo, timeValue);
				objInfo.setTypeAndInitExpr(objClass, expr);
			}
		}

		protected BeanExpr doConvertValue(VHToStmt owner,
				AbstractObjectValueHolder obj, ObjectStmtInfo objInfo,
				long timeValue) {
			List<BeanExpr> args = new ArrayList<BeanExpr>();
			Class<?> objClass = obj.getObjClass();
			
			String dateUtilClassName = "DateUtil";
			if (java.sql.Date.class.isAssignableFrom(objClass)) {
				dateUtilClassName = "SqlDateUtil";
			}
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(new Date(timeValue));
			
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DAY_OF_MONTH);
			args.add(new LiteralExpr(year));
			args.add(new LiteralExpr(month));
			args.add(new LiteralExpr(day));
			
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int minutes = cal.get(Calendar.MINUTE);
			int seconds = cal.get(Calendar.SECOND);
			if (hour == 0 && minutes == 0 && seconds == 0) {
				// midnight date day
			} else {
				args.add(new LiteralExpr(hour));
				args.add(new LiteralExpr(minutes));
				args.add(new LiteralExpr(seconds));
			}
			return new MethodApplyExpr(new SimpleNameExpr(dateUtilClassName), "toDate", args);
		}

		@Override
		public String toString() {
			return "DateConverter";
		}

	}

}
