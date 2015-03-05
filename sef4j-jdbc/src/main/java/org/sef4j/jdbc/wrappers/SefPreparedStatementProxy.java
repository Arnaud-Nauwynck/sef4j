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

import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.LocalCallStack;

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

    public SefPreparedStatementProxy(SefConnectionProxy owner,
            PreparedStatement to,
            String sql,
            int resultSetType,
            int resultSetConcurrency,
            int resultSetHoldability) {
        super(owner, to, resultSetType, resultSetConcurrency, resultSetHoldability);
        this.to = to;
        this.sqlQuery = sql;
    }


    // ------------------------------------------------------------------------

    public final String getSqlQuery() {
        return sqlQuery;
    }
    
    /** internal */
    protected ParamInfo paramInfoFor(int index) {
        if (paramByIndex == null) {
            this.paramByIndex = new LinkedHashMap<Integer,ParamInfo>();
        }
        ParamInfo paramInfo = paramByIndex.get(index);
        if (paramInfo == null) {
            paramInfo = new ParamInfo(null, index);
            paramByIndex.put(index, paramInfo);
        }
        return paramInfo;
    }

    /** internal */
    protected ParamInfo paramInfoFor(String name) {
        if (paramByName == null) {
            this.paramByName = new LinkedHashMap<String,ParamInfo>();
        }
        ParamInfo paramInfo = paramByName.get(name);
        if (paramInfo == null) {
            paramInfo = new ParamInfo(name, 0);
            paramByName.put(name, paramInfo);
        }
        return paramInfo;
    }

    protected ParamInfo onSetParamValue(int index, Object value) {
        return paramInfoFor(index).value(value);
    }

    protected ParamInfo onSetParamValue(String name, Object value) {
        return paramInfoFor(name).value(value);
    }
    
    
    /** internal */
    protected void resetParamInfos() {
        this.paramByName = null;
        this.paramByIndex = null;
    }

    // implements java.sql.PreparedStatement
    // ------------------------------------------------------------------------

    public final ResultSet executeQuery() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("executeQuery").push();
        try {
            ResultSet tmpres = to.executeQuery();

            SefResultSetProxy res = new SefResultSetProxy(this, tmpres);
            return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } catch(RuntimeException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int executeUpdate() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("executeUpdate").push();
        try {
            int res = to.executeUpdate();

            return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } catch(RuntimeException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public final boolean execute() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("execute").push();
        try {
            boolean res = to.execute();

            return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } catch(RuntimeException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public final void addBatch() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("addBatch").push();
        try {
            to.addBatch();

        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } catch(RuntimeException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public final ResultSetMetaData getMetaData() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("getMetaData").push();
        try {
            ResultSetMetaData tmpres = to.getMetaData();

            // may wrap ... new SefResultSetMetaDataProxy(tmpres);
            return tmpres;
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } catch(RuntimeException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public final ParameterMetaData getParameterMetaData() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("getParameterMetaData").push();
        try {
            return to.getParameterMetaData();

            // may wrap ... new SefParameterMetaDataProxy(tmpres);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } catch(RuntimeException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    // implements java.sql.PreparedStatement, save param value + delegate to underlying  
    // -------------------------------------------------------------------------

    public final void clearParameters() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("clearParameters").push();
        try {
            to.clearParameters();
            clearParameters(); // also clear in-memory ParamInfo

        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } catch(RuntimeException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public final void setNull(int parameterIndex, int sqlType) throws SQLException {
        onSetParamValue(parameterIndex, null).sqlType(sqlType);
        to.setNull(parameterIndex, sqlType);
    }

    public final void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        onSetParamValue(parameterIndex, null).sqlType(sqlType).typeName(typeName);
        to.setNull(parameterIndex, sqlType, typeName);
    }

    public final void setBoolean(int parameterIndex, boolean x) throws SQLException {
        onSetParamValue(parameterIndex, Boolean.valueOf(x));
        to.setBoolean(parameterIndex, x);
    }

    public final void setByte(int parameterIndex, byte x) throws SQLException {
        onSetParamValue(parameterIndex, Byte.valueOf(x));
        to.setByte(parameterIndex, x);
    }

    public final void setShort(int parameterIndex, short x) throws SQLException {
        onSetParamValue(parameterIndex, Short.valueOf(x));
        to.setShort(parameterIndex, x);
    }

    public final void setInt(int parameterIndex, int x) throws SQLException {
        onSetParamValue(parameterIndex, Integer.valueOf(x));
        to.setInt(parameterIndex, x);
    }

    public final void setLong(int parameterIndex, long x) throws SQLException {
        onSetParamValue(parameterIndex, Long.valueOf(x));
        to.setLong(parameterIndex, x);
    }

    public final void setFloat(int parameterIndex, float x) throws SQLException {
        onSetParamValue(parameterIndex, Float.valueOf(x));
        to.setFloat(parameterIndex, x);
    }

    public final void setDouble(int parameterIndex, double x) throws SQLException {
        onSetParamValue(parameterIndex, Double.valueOf(x));
        to.setDouble(parameterIndex, x);
    }

    public final void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setBigDecimal(parameterIndex, x);
    }

    public final void setString(int parameterIndex, String x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setString(parameterIndex, x);
    }

    public final void setBytes(int parameterIndex, byte[] x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setBytes(parameterIndex, x);
    }
    
    public final void setDate(int parameterIndex, Date x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setDate(parameterIndex, x);
    }

    public final void setTime(int parameterIndex, Time x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setTime(parameterIndex, x);
    }

    public final void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setTimestamp(parameterIndex, x);
    }

    public final void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        onSetParamValue(parameterIndex, new ParamInfo.PairDateWithCalendar(x, cal));
        to.setDate(parameterIndex, x, cal);
    }

    public final void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        onSetParamValue(parameterIndex, new ParamInfo.PairDateWithCalendar(x, cal));
        to.setTime(parameterIndex, x, cal);
    }

    public final void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        onSetParamValue(parameterIndex, new ParamInfo.PairDateWithCalendar(x, cal));
        to.setTimestamp(parameterIndex, x, cal);
    }

    public final void setArray(int parameterIndex, Array x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setArray(parameterIndex, x);
    }

    public final void setRef(int parameterIndex, Ref x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setRef(parameterIndex, x);
    }

    public final void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
        onSetParamValue(parameterIndex, x).targetSqlType(targetSqlType).scale(scale);
        to.setObject(parameterIndex, x, targetSqlType, scale);
    }

    public final void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        onSetParamValue(parameterIndex, x).targetSqlType(targetSqlType);
        to.setObject(parameterIndex, x, targetSqlType);
    }

    public final void setObject(int parameterIndex, Object x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setObject(parameterIndex, x);
    }

    public final void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        onSetParamValue(parameterIndex, new ParamInfo.PairValueWithLength(x, length));
        to.setAsciiStream(parameterIndex, x, length);
    }

    @Deprecated
    public final void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        onSetParamValue(parameterIndex, new ParamInfo.PairValueWithLength(x, length));
        to.setUnicodeStream(parameterIndex, x, length); // deprecated
    }

    public final void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        onSetParamValue(parameterIndex, new ParamInfo.PairValueWithLength(x, length));
        to.setBinaryStream(parameterIndex, x, length);
    }

    public final void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        onSetParamValue(parameterIndex, new ParamInfo.PairValueWithLength(reader, length));
        to.setCharacterStream(parameterIndex, reader, length);
    }

    public final void setBlob(int parameterIndex, Blob x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setBlob(parameterIndex, x);
    }

    public final void setClob(int parameterIndex, Clob x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setClob(parameterIndex, x);
    }

    public final void setURL(int parameterIndex, URL x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setURL(parameterIndex, x);
    }

    public final void setNString(int parameterIndex, String x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setNString(parameterIndex, x);
    }

    public final void setRowId(int parameterIndex, RowId x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setRowId(parameterIndex, x);
    }

    public final void setSQLXML(int parameterIndex, SQLXML x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setSQLXML(parameterIndex, x);
    }
    
    public final void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setAsciiStream(parameterIndex, x);
    }

    public final void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        onSetParamValue(parameterIndex, new ParamInfo.PairValueWithLength(x, length));
        to.setAsciiStream(parameterIndex, x, length);
    }

    public final void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setBinaryStream(parameterIndex, x);
    }

    public final void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        onSetParamValue(parameterIndex, new ParamInfo.PairValueWithLength(x, length));
        to.setBinaryStream(parameterIndex, x, length);
    }

    public final void setBlob(int parameterIndex, InputStream x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setBlob(parameterIndex, x);
    }

    public final void setBlob(int parameterIndex, InputStream x, long length) throws SQLException {
        onSetParamValue(parameterIndex, new ParamInfo.PairValueWithLength(x, length));
        to.setBlob(parameterIndex, x, length);
    }

    public final void setCharacterStream(int parameterIndex, Reader x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setCharacterStream(parameterIndex, x);
    }

    public final void setCharacterStream(int parameterIndex, Reader x, long length) throws SQLException {
        onSetParamValue(parameterIndex, new ParamInfo.PairValueWithLength(x, length));
        to.setCharacterStream(parameterIndex, x, length);
    }

    public final void setClob(int parameterIndex, Reader x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setClob(parameterIndex, x);
    }

    public final void setClob(int parameterIndex, Reader x, long length) throws SQLException {
        onSetParamValue(parameterIndex, new ParamInfo.PairValueWithLength(x, length));
        to.setClob(parameterIndex, x, length);
    }

    public final void setNCharacterStream(int parameterIndex, Reader x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setNCharacterStream(parameterIndex, x);
    }

    public final void setNCharacterStream(int parameterIndex, Reader x, long length) throws SQLException {
        onSetParamValue(parameterIndex, new ParamInfo.PairValueWithLength(x, length));
        to.setNCharacterStream(parameterIndex, x, length);
    }

    public final void setNClob(int parameterIndex, NClob x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setNClob(parameterIndex, x);
    }

    public final void setNClob(int parameterIndex, Reader x) throws SQLException {
        onSetParamValue(parameterIndex, x);
        to.setNClob(parameterIndex, x);
    }

    public final void setNClob(int parameterIndex, Reader x, long length) throws SQLException {
        onSetParamValue(parameterIndex, new ParamInfo.PairValueWithLength(x, length));
        to.setNClob(parameterIndex, x, length);
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "SefPreparedStatementProxy [sqlQuery=" + sqlQuery + "]";
    }

}
