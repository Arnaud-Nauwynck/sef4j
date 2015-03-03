package org.sef4j.jdbc.wrappers;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 */
public class SefPreparedStatementProxy extends SefStatementProxy implements PreparedStatement {

    /** redondant with ((PreparedStatement)super.to) */
    private PreparedStatement to;

    /** log info about indexed parameters **/
    private String sqlQuery;

    /** param by index (lazy created and filled by <code>paramFor(index)</code> */
    private Map<Integer,ParamInfo> paramByIndex;
    /** param by index (lazy created and filled by <code>paramFor(name)</code> */
    private Map<String,ParamInfo> paramByName;


    // constructor
    // ------------------------------------------------------------------------

    public SefPreparedStatementProxy(SefConnectionProxy owner, PreparedStatement to, String sql) {
        super(owner, to);
        this.to = to;
        this.sqlQuery = sql;
    }

    public SefPreparedStatementProxy(SefConnectionProxy owner,
                                PreparedStatement to,
                                String sql,
                                int resultSetType,
                                int resultSetConcurrency) {
        super(owner, to, resultSetType, resultSetConcurrency);
        this.to = to;
        this.sqlQuery = sql;
    }

    // ------------------------------------------------------------------------

    public final String getSqlQuery() {
        return sqlQuery;
    }
    
    public ParamInfo paramInfoFor(int index) {
        if (paramByIndex == null) {
            this.paramByIndex = new LinkedHashMap<Integer,ParamInfo>();
        }
        ParamInfo paramInfo = paramByIndex.get(index);
        if (paramInfo == null) {
            paramInfo = new ParamInfo(index);
            paramByIndex.put(index, paramInfo);
        }
        return paramInfo;
    }

    public ParamInfo paramInfoFor(String name) {
        if (paramByName == null) {
            this.paramByName = new LinkedHashMap<String,ParamInfo>();
        }
        ParamInfo paramInfo = paramByName.get(name);
        if (paramInfo == null) {
            paramInfo = new ParamInfo(name);
            paramByName.put(name, paramInfo);
        }
        return paramInfo;
    }

    /** internal */
    protected void resetParamInfos() {
        this.paramByName = null;
        this.paramByIndex = null;
    }

    // implements java.sql.PreparedStatement
    // ------------------------------------------------------------------------

    public final ResultSet executeQuery() throws SQLException {
        ResultSet res;
        pre("executeQuery", "");
        try {
            res = to.executeQuery();
            if (res != null) {
                res = wrapResultSet(res);
                postDefaultRes(res);
            } else {
                postRes("NULL ResultSet");
            }
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final int executeUpdate() throws SQLException {
        int res;
        pre("executeUpdate", "");
        try {
            res = to.executeUpdate();
            ownerSefWrappedConnection.incrCountExecuteUpdate(sqlQuery); //unformatted prepared statment, without params
            postRes(new Integer(res));
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final boolean execute() throws SQLException {
        boolean res;
        pre("execute", "");
        try {
            res = to.execute();
            ownerSefWrappedConnection.incrCountExecute(sqlQuery); //unformatted prepared statment, without params
            if (res)
                postDefaultRes((res) ? Boolean.TRUE : Boolean.FALSE);
            else
                postRes("false");
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final void addBatch() throws SQLException {
        pre("addBatch", "");
        try {
            to.addBatch();
            postVoid();
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
    }

    public final ResultSetMetaData getMetaData() throws SQLException {
        ResultSetMetaData res;
        pre("getMetaData", "");
        try {
            res = to.getMetaData();
            postDefaultRes(res);
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final ParameterMetaData getParameterMetaData() throws SQLException {
        return to.getParameterMetaData();
    }

    // implements java.sql.PreparedStatement, save param value + delegate to underlying  
    // -------------------------------------------------------------------------

    public final void clearParameters() throws SQLException {
        params.clear();
        setParamChanged();
        to.clearParameters();
    }

    public final void setNull(int parameterIndex, int sqlType) throws SQLException {
        ParamInfo p = new ParamInfo(false, sqlType);
        set(parameterIndex, p);
        to.setNull(parameterIndex, sqlType);
    }

    public final void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        ParamInfo p = new ParamInfo(false, sqlType);
        p.setTypeName(typeName);
        to.setNull(parameterIndex, sqlType, typeName);
    }

    public final void setBoolean(int parameterIndex, boolean x) throws SQLException {
        set(parameterIndex, (x)? "'1'" : "'0'");
        to.setBoolean(parameterIndex, x);
    }

    public final void setByte(int parameterIndex, byte x) throws SQLException {
        set(parameterIndex, new Byte(x));
        to.setByte(parameterIndex, x);
    }

    public final void setShort(int parameterIndex, short x) throws SQLException {
        set(parameterIndex, new Short(x));
        to.setShort(parameterIndex, x);
    }

    public final void setInt(int parameterIndex, int x) throws SQLException {
        set(parameterIndex, new Integer(x));
        to.setInt(parameterIndex, x);
    }

    public final void setLong(int parameterIndex, long x) throws SQLException {
        set(parameterIndex, new Long(x));
        to.setLong(parameterIndex, x);
    }

    public final void setFloat(int parameterIndex, float x) throws SQLException {
        set(parameterIndex, new Float(x));
        to.setFloat(parameterIndex, x);
    }

    public final void setDouble(int parameterIndex, double x) throws SQLException {
        set(parameterIndex, new Double(x));
        to.setDouble(parameterIndex, x);
    }

    public final void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        set(parameterIndex, x);
        to.setBigDecimal(parameterIndex, x);
    }

    public final void setString(int parameterIndex, String x) throws SQLException {
        set(parameterIndex, "'" + x + "'");
        to.setString(parameterIndex, x);
    }

    public final void setBytes(int parameterIndex, byte[] x) throws SQLException {
        set(parameterIndex, x);
        to.setBytes(parameterIndex, x);
    }

    
    public final void setDate(int parameterIndex, Date x) throws SQLException {
        set(parameterIndex, ParamInfo.formatOracleToDate(x));
        to.setDate(parameterIndex, x);
    }

    public final void setTime(int parameterIndex, Time x) throws SQLException {
        set(parameterIndex, ParamInfo.formatOracleToTime(x));
        to.setTime(parameterIndex, x);
    }

    public final void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        set(parameterIndex, ParamInfo.formatOracleToTimestamp(x));
        to.setTimestamp(parameterIndex, x);
    }

    public final void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        set(parameterIndex, ParamInfo.formatOracleToDate(x, cal));
        //        set(parameterIndex, new ValueDateWithCal(x,cal));
        to.setDate(parameterIndex, x, cal);
    }

    public final void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        set(parameterIndex, ParamInfo.formatOracleToTime(x, cal));
        //        set(parameterIndex, new ValueDateWithCal(x,cal));
        to.setTime(parameterIndex, x, cal);
    }

    public final void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        set(parameterIndex, ParamInfo.formatOracleToTimestamp(x, cal));
        //        set(parameterIndex, new ValueDateWithCal(x,cal));
        to.setTimestamp(parameterIndex, x, cal);
    }

    public final void setArray(int parameterIndex, Array x) throws SQLException {
        set(parameterIndex, objToString(x.getArray()));
        to.setArray(parameterIndex, x);
    }

    public final void setRef(int parameterIndex, Ref x) throws SQLException {
        set(parameterIndex, x);
        to.setRef(parameterIndex, x);
    }

    public final void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
        ParamInfo p = new ParamInfo(x);
        p.setTargetSqlType(targetSqlType);
        p.setScale(scale);
        set(parameterIndex, p);
        to.setObject(parameterIndex, x, targetSqlType, scale);
    }

    public final void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        ParamInfo p = new ParamInfo(x);
        p.setTargetSqlType(targetSqlType);
        set(parameterIndex, p);
        to.setObject(parameterIndex, x, targetSqlType);
    }

    public final void setObject(int parameterIndex, Object x) throws SQLException {
        String text;
        if (x == null) {
            text = "'null'";
        } else {
            Class cl = x.getClass();
            if (cl.isPrimitive()) {
                text = x.toString();
            } else {
                text = objToString(x);
            }
        }
        set(parameterIndex, text);

        to.setObject(parameterIndex, x);
    }

    public final void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        set(parameterIndex, new ParamInfo("'--AsciiStream.. (not  supported  in log) length:" + length + "--'"));
        to.setAsciiStream(parameterIndex, x, length);
    }

    public final void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        set(parameterIndex, new ParamInfo("'--UnicodeStream.. (not  supported  in log) length:" + length + "--'"));
        to.setUnicodeStream(parameterIndex, x, length); // deprecated
    }

    public final void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        set(parameterIndex, new ParamInfo("'--BinaryStream.. (not  supported  in log) length:" + length + "--'"));
        to.setBinaryStream(parameterIndex, x, length);
    }

    public final void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        set(parameterIndex, new ParamInfo("'--CharacterStream.. (not  supported  in log) length:" + length + "--'"));
        to.setCharacterStream(parameterIndex, reader, length);
    }

    public final void setBlob(int parameterIndex, Blob x) throws SQLException {
        set(parameterIndex, new ParamInfo("'--Blob.. (not  supported  in log)--'"));
        to.setBlob(parameterIndex, x);
    }

    public final void setClob(int parameterIndex, Clob x) throws SQLException {
        set(parameterIndex, new ParamInfo("'--Clob.. (not  supported  in log)--'"));
        to.setClob(parameterIndex, x);
    }

    public final void setURL(int parameterIndex, URL x) throws SQLException {
        set(parameterIndex, x);
        to.setURL(parameterIndex, x);
    }

    // ------------------------------------------------------------------------
    
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        to.setAsciiStream(parameterIndex, x);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        to.setAsciiStream(parameterIndex, x, length);
    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        to.setBinaryStream(parameterIndex, x);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        to.setBinaryStream(parameterIndex, x, length);
    }

    public void setBlob(int parameterIndex, InputStream x) throws SQLException {
        to.setBlob(parameterIndex, x);
    }

    public void setBlob(int parameterIndex, InputStream x, long length) throws SQLException {
        to.setBlob(parameterIndex, x, length);
    }

    public void setCharacterStream(int parameterIndex, Reader x) throws SQLException {
        to.setCharacterStream(parameterIndex, x);
    }

    public void setCharacterStream(int parameterIndex, Reader x, long length) throws SQLException {
        to.setCharacterStream(parameterIndex, x, length);
    }

    public void setClob(int parameterIndex, Reader x) throws SQLException {
        to.setClob(parameterIndex, x);
    }

    public void setClob(int parameterIndex, Reader x, long length) throws SQLException {
        to.setClob(parameterIndex, x, length);
    }

    public void setNCharacterStream(int parameterIndex, Reader x) throws SQLException {
        to.setNCharacterStream(parameterIndex, x);
    }

    public void setNCharacterStream(int parameterIndex, Reader x, long length) throws SQLException {
        to.setNCharacterStream(parameterIndex, x, length);
    }

    public void setNClob(int parameterIndex, NClob x) throws SQLException {
        to.setNClob(parameterIndex, x);
    }

    public void setNClob(int parameterIndex, Reader x) throws SQLException {
        to.setNClob(parameterIndex, x);
    }

    public void setNClob(int parameterIndex, Reader x, long length) throws SQLException {
        to.setNClob(parameterIndex, x, length);
    }

    public void setNString(int parameterIndex, String x) throws SQLException {
        to.setNString(parameterIndex, x);
    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        to.setRowId(parameterIndex, x);
    }

    public void setSQLXML(int parameterIndex, SQLXML x) throws SQLException {
        to.setSQLXML(parameterIndex, x);
    }

    
//
//    private static String objToString(Object x) {
//        String textValue;
//        if (x == null) {
//            textValue = "null";
//        } else if (x instanceof String) {
//            textValue = "'" + x + "'";
//        } else if (x instanceof Character) {
//            textValue = "'" + x + "'";
//        } else if (x instanceof Boolean) {
//            boolean b = ((Boolean) x).booleanValue();
//            textValue = (b)? "'1'" : "'0'";
//        } else if (x instanceof Long) {
//            textValue = ((Long) x).toString();
//        } else if (x instanceof Integer) {
//            textValue = ((Integer) x).toString();
//        } else if (x instanceof BigDecimal) {
//            textValue = ((BigDecimal) x).toString();
//        } else if (x instanceof java.sql.Timestamp) {
//            textValue = ParamInfo.formatOracleToTimestamp((java.sql.Timestamp) x);
//        } else if (x instanceof java.sql.Time) {
//            textValue = ParamInfo.formatOracleToTime((java.sql.Time) x);;
//        } else if (x instanceof java.sql.Date) {
//            textValue = ParamInfo.formatOracleToDate((java.sql.Date) x);
//        } else if (x instanceof java.util.Date) {
//            textValue = ParamInfo.formatOracleToDate((java.util.Date) x);
//        } else if (x instanceof long[]) {
//            long[] arr = (long[]) x;
//            StringBuffer sb = new StringBuffer(arr.length * 7);
//            sb.append("[ ");
//            int len = arr.length;
//            int displayLen = (len > 10)? 10 : len; 
//            for (int i = 0; i < displayLen; i++) {
//                sb.append(Long.toString(arr[i]));
//                if (i + 1 < len)
//                    sb.append(',');
//            }
//            if (displayLen != len) {
//                sb.append(" /* ... truncated " + displayLen + "/" + len +  "... */ ");
//                sb.append("\n");
//            }
//            sb.append(" ]");
//            textValue = sb.toString();
//        } else if (x instanceof BigDecimal[]) {
//            BigDecimal[] arr = (BigDecimal[]) x;
//            StringBuffer sb = new StringBuffer(arr.length * 7);
//            sb.append("[ ");
//            int len = arr.length;
//            int displayLen = (len > 10)? 10 : len; 
//            for (int i = 0; i < displayLen; i++) {
//                sb.append(arr[i]);
//                if (i + 1 < len)
//                    sb.append(',');
//            }
//            if (displayLen != len) {
//                sb.append(" /* ... truncated " + displayLen + "/" + len +  "... */ ");
//                sb.append("\n");
//            }
//            sb.append(" ]");
//            textValue = sb.toString();
//        } else {
//            textValue = x.toString();
//        }
//        return textValue;
//    }
//
//    
}
