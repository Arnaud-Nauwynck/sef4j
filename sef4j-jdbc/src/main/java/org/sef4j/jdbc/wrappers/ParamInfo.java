package org.sef4j.jdbc.wrappers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * info on a sql parameter 
 */
public class ParamInfo {
    
    int paramIndex;
    String paramName;
    
    int sqlType;
    int targetSqlType;
    String typeName;
    int scale;
    Object value; // input value.. may differ from OutputParamInfo.runTimeResValue ...

    boolean isOutput; // default to false
    boolean isRuntimeResAlreadyGet;
    Object outResValue;
    Exception outResException;

    // ------------------------------------------------------------------------
    
    /* Ctor */
    public ParamInfo(Object value) {
        this.value = value;
    }

    public ParamInfo(boolean isOutput, int sqlType) {
        this.isOutput = isOutput;
        this.sqlType = sqlType;
    }

    // ------------------------------------------------------------------------

    public String getParamNameOrIndex() {
        return (paramIndex > 0)? "?" + Integer.toString(paramIndex) : paramName;
    }

    public int getParamIndex() {
        return paramIndex;
    }
    
    public void setParamIndex(int paramIndex) {
        this.paramIndex = paramIndex;
    }
    
    public String getParamName() {
        return paramName;
    }
    
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
    
    public String getTypeName() {
		return typeName;
	}

    public ParamInfo withTypeName(String typeName) {
		this.typeName = typeName;
		return this;
	}

	public int getScale() {
		return scale;
	}

	public ParamInfo withScale(int scale) {
		this.scale = scale;
		return this;
	}
	
    public int getTargetSqlType() {
		return targetSqlType;
	}

	public ParamInfo withTargetSqlType(int targetSqlType) {
		this.targetSqlType = targetSqlType;
		return this;
	}
	
    // ------------------------------------------------------------------------
    
	/** extends Object.toString */
    public String toString() {
        return toStringPrePost(1);
    }

	/**
     * @param prePost: 0: in param value(pre), 1:out param value , -1 (declaration?),
     */
    public String toStringPrePost(int prePost) {
        String res;
        if (!isOutput) {
            res = getParamNameOrIndex() + "=" + ((value != null) ? value.toString() : "null");
        } else {
            if (!isRuntimeResAlreadyGet) {
                res = "out @" + getParamNameOrIndex();
            } else {
                res = "out @" + getParamNameOrIndex() + "=> ";
                if ((outResException != null)) {
                    res += (outResValue != null) ? outResValue.toString() : "null";
                } else {
                    res += (outResException != null) ? outResException.toString() : "null";
                }
            }
        } //else isOutput
        return res;
    } //toString
    
    
    // ------------------------------------------------------------------------
    

    private static final String DATEFORMAT_DATE_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    private static final String DATEFORMAT_TIME_HMS = "HH:mm:ss";
    private static final String DATEFORMAT_TIMESTAMP_YMD_HMS_SS = "yyyy-MM-dd HH:mm:ss.sss";
    
    public static final String ORACLE_FMT_YMD_HMS = "yyyy-mm-dd hh24:mi:ss";
    public static final String ORACLE_FMT_HMS = "hh24:mi:ss";
    public static final String ORACLE_FMT_YMD_HMS_SSS = "yyyy-mm-dd hh24:mi:ss.sss";

    private static final DateFormat _DATEFMT_YMD_HMS = new SimpleDateFormat(DATEFORMAT_DATE_YMD_HMS);
    private static final DateFormat _DATEFMT_HMS = new SimpleDateFormat(DATEFORMAT_TIME_HMS);
    private static final DateFormat _DATEFMT_YMD_HMS_SS = new SimpleDateFormat(DATEFORMAT_TIMESTAMP_YMD_HMS_SS);
        
    public static String fmtDate_YMD_HMS(java.util.Date date) {
        synchronized(_DATEFMT_YMD_HMS) {
            return _DATEFMT_YMD_HMS.format(date);
        }
    }
    
    public static String fmtTimestamp_YMD_HMS_SS(java.util.Date date) {
        synchronized(_DATEFMT_YMD_HMS_SS) {
            return _DATEFMT_YMD_HMS_SS.format(date);
        }
    }
    
    public static String fmtTime_HMS(java.util.Date date) {
        synchronized(_DATEFMT_HMS) {
            return _DATEFMT_HMS.format(date);
        }
    }

    public static String fmtDate_YMD_HMS(java.util.Date date, java.util.Calendar cal) {
        SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT_DATE_YMD_HMS);
        df.setCalendar(cal);
        String res = df.format(date);
        return res;
    }

    public static String fmtTimestamp_YMD_HMS_SS(java.util.Date date, java.util.Calendar cal) {
        SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT_TIMESTAMP_YMD_HMS_SS);
        df.setCalendar(cal);
        String res = df.format(date);
        return res;
    }

    public static String fmtTime_HMS(java.util.Date date, java.util.Calendar cal) {
        SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT_TIME_HMS);
        df.setCalendar(cal);
        String res = df.format(date);
        return res;
    }
    
    public static String formatOracleToDate(java.util.Date p) {
        return "to_date('" + fmtDate_YMD_HMS(p) + "', '" + ORACLE_FMT_YMD_HMS + "')";
    }
    public static String formatOracleToTime(java.sql.Time p) {
        return "to_time('" + fmtTime_HMS(p) + "', '" + ORACLE_FMT_HMS + "')";
    }
    public static String formatOracleToTimestamp(java.util.Date p) {
        return "to_timestamp('" + fmtTimestamp_YMD_HMS_SS(p) + "', '" + ORACLE_FMT_YMD_HMS_SSS + "')";
    }
    
    public static String formatOracleToDate(java.util.Date p, java.util.Calendar cal) {
        return "to_date('" + fmtDate_YMD_HMS(p, cal) + "', '" + ORACLE_FMT_YMD_HMS + "')";
    }
    public static String formatOracleToTime(java.sql.Time p, java.util.Calendar cal) {
        return "to_time('" + fmtTime_HMS(p, cal) + "', '" + ORACLE_FMT_HMS + "')";
    }
    public static String formatOracleToTimestamp(java.util.Date p, java.util.Calendar cal) {
        return "to_timestamp('" + fmtTimestamp_YMD_HMS_SS(p, cal) + "', '" + ORACLE_FMT_YMD_HMS_SSS + "')";
    }
    
    
    public static String replaceStringRegion(String str, int idx, int nbChars, String by) {
        final int size = str.length();
        String before = str.substring(0, idx);
        String after = (idx + nbChars < size) ? str.substring(idx + nbChars) : "";
        return before + by + after;
    }

    public static String replaceStringChar(String str, int idx, String by) {
        return replaceStringRegion(str, idx, 1, by);
    }
    
}