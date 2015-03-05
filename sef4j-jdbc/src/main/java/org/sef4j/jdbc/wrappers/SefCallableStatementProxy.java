package org.sef4j.jdbc.wrappers;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.LocalCallStack;

/**
 * Proxy for java.sql.Statement + wrapp all calls with push()/pop() + set params 
 */
public class SefCallableStatementProxy extends SefPreparedStatementProxy implements CallableStatement {

    /** redondant with <code>((CallableStatement)super.to)</code> */
    protected final CallableStatement to;

    // constructor
    // ------------------------------------------------------------------------

    public SefCallableStatementProxy(SefConnectionProxy owner, CallableStatement to, String sql) {
        super(owner, to, sql);
        this.to = to;
    }

    public SefCallableStatementProxy(SefConnectionProxy owner,
            CallableStatement to,
            String sql,
            int resultSetType,
            int resultSetConcurrency) {
        super(owner, to, sql, resultSetType, resultSetConcurrency);
        this.to = to;
    }

    public SefCallableStatementProxy(SefConnectionProxy owner,
            CallableStatement to,
            String sql,
            int resultSetType,
            int resultSetConcurrency, int resultSetHoldability) {
        super(owner, to, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        this.to = to;
    }

    // internal
    // ------------------------------------------------------------------------

    /** internal */
    protected SefStatementParamInfo doRegisterOutputParamInfo(int parameterIndex) {
        return paramInfoFor(parameterIndex).output(true);
    }

    protected SefStatementParamInfo doRegisterOutputParamInfo(String parameterName) {
        return paramInfoFor(parameterName).output(true);
    }

    protected <T> T onGetParamReturn(int index, T value) {
        paramInfoFor(index).outResValue(value, null);
        return value;
    }

    protected <T> T onGetParamReturn(String name, T value) {
        paramInfoFor(name).outResValue(value, null);
        return value;
    }

    protected <T extends Exception> T onGetParamException(String name, T ex) {
        paramInfoFor(name).outResValue(null, ex);
        return ex;
    }

    protected <T extends Exception> T onGetParamException(int index, T ex) {
        paramInfoFor(index).outResValue(null, ex);
        return ex;
    }


    // implements java.sql.CallableStatement
    // ------------------------------------------------------------------------

    public boolean wasNull() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("wasNull").push();
        try {
            boolean res = to.wasNull();
            LocalCallStack.pushPopParentReturn(res);
            return res;
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    
    //implements java.sql.CallableStatement output parameters: registerOutParam + getXX
    // -------------------------------------------------------------

    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("registerOutParameter(int,int)")
                .withParam("parameterIndex", parameterIndex)
                .withParam("sqlType", sqlType)
                .push();
        try {
            to.registerOutParameter(parameterIndex, sqlType);
            doRegisterOutputParamInfo(parameterIndex).sqlType(sqlType);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("registerOutParameter(int,int,int)")
                .withParam("parameterIndex", parameterIndex)
                .withParam("sqlType", sqlType)
                .withParam("scale", scale)
                .push();
        try {
            to.registerOutParameter(parameterIndex, sqlType, scale);
            doRegisterOutputParamInfo(parameterIndex).sqlType(sqlType).scale(scale);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("registerOutParameter(int,int,String)")
                .withParam("parameterIndex", parameterIndex)
                .withParam("sqlType", sqlType)
                .withParam("typeName", typeName)
                .push();
        try {
            to.registerOutParameter(parameterIndex, sqlType, typeName);
            doRegisterOutputParamInfo(parameterIndex).sqlType(sqlType).typeName(typeName);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("registerOutParameter(String,int)")
                .withParam("parameterName", parameterName)
                .withParam("sqlType", sqlType)
                .push();
        try {
            to.registerOutParameter(parameterName, sqlType);
            doRegisterOutputParamInfo(parameterName).sqlType(sqlType);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("registerOutParameter(String,int,int)")
                .withParam("parameterName", parameterName)
                .withParam("sqlType", sqlType)
                .withParam("scale", scale)
                .push();
        try {
            to.registerOutParameter(parameterName, sqlType, scale);
            doRegisterOutputParamInfo(parameterName).sqlType(sqlType).scale(scale);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("registerOutParameter(String,int,String)")
                .withParam("parameterName", parameterName)
                .withParam("sqlType", sqlType)
                .withParam("typeName", typeName)
                .push();
        try {
            to.registerOutParameter(parameterName, sqlType, typeName);
            doRegisterOutputParamInfo(parameterName).sqlType(sqlType).typeName(typeName);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    
    //implements java.sql.CallableStatement get parameters by index
    // ------------------------------------------------------------------------

    public String getString(int parameterIndex) throws SQLException {
        try {
            String res = to.getString(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public boolean getBoolean(int parameterIndex) throws SQLException {
        try {
            boolean res = to.getBoolean(parameterIndex);
            onGetParamReturn(parameterIndex, Boolean.valueOf(res));
            return res;
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public byte getByte(int parameterIndex) throws SQLException {
        try {
            byte res = to.getByte(parameterIndex);
            onGetParamReturn(parameterIndex, Byte.valueOf(res));
            return res;
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public short getShort(int parameterIndex) throws SQLException {
        try {
            short res = to.getShort(parameterIndex);
            onGetParamReturn(parameterIndex, Short.valueOf(res));
            return res;
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public int getInt(int parameterIndex) throws SQLException {
        try {
            int res = to.getInt(parameterIndex);
            onGetParamReturn(parameterIndex, Integer.valueOf(res));
            return res;
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public long getLong(int parameterIndex) throws SQLException {
        try {
            long res = to.getLong(parameterIndex);
            onGetParamReturn(parameterIndex, Long.valueOf(res));
            return res;
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public float getFloat(int parameterIndex) throws SQLException {
        try {
            float res = to.getFloat(parameterIndex);
            onGetParamReturn(parameterIndex, new Float(res));
            return res;
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public double getDouble(int parameterIndex) throws SQLException {
        try {
            double res = to.getDouble(parameterIndex);
            onGetParamReturn(parameterIndex, new Double(res));
            return res;
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    @Deprecated
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        try {
            BigDecimal res = to.getBigDecimal(parameterIndex, scale);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public byte[] getBytes(int parameterIndex) throws SQLException {
        try {
            byte[] res = to.getBytes(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public Date getDate(int parameterIndex) throws SQLException {
        try {
            Date res = to.getDate(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public Time getTime(int parameterIndex) throws SQLException {
        try {
            Time res = to.getTime(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        try {
            Timestamp res = to.getTimestamp(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public Object getObject(int parameterIndex) throws SQLException {
        try {
            Object res = to.getObject(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        try {
            BigDecimal res = to.getBigDecimal(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
        try {
            Object res = to.getObject(parameterIndex, map);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public Ref getRef(int parameterIndex) throws SQLException {
        try {
            Ref res = to.getRef(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public Blob getBlob(int parameterIndex) throws SQLException {
        try {
            Blob res = to.getBlob(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public Clob getClob(int parameterIndex) throws SQLException {
        try {
            Clob res = to.getClob(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public Array getArray(int parameterIndex) throws SQLException {
        try {
            Array res = to.getArray(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        try {
            Date res = to.getDate(parameterIndex, cal);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        try {
            Time res = to.getTime(parameterIndex, cal);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        try {
            Timestamp res = to.getTimestamp(parameterIndex, cal);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public URL getURL(int parameterIndex) throws SQLException {
        try {
            URL res = to.getURL(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        try {
            Reader res = to.getCharacterStream(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        try {
            Reader res = to.getNCharacterStream(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public NClob getNClob(int parameterIndex) throws SQLException {
        try {
            NClob res = to.getNClob(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public String getNString(int parameterIndex) throws SQLException {
        try {
            String res = to.getNString(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public RowId getRowId(int parameterIndex) throws SQLException {
        try {
            RowId res = to.getRowId(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        try {
            SQLXML res = to.getSQLXML(parameterIndex);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }

    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        try {
            T res = to.getObject(parameterIndex, type);
            return onGetParamReturn(parameterIndex, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterIndex, ex);
        }
    }



    // implements java.sql.CallableStatement getXX(paramName) : get parameters by names
    // -------------------------------------------------------------

    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        try {
            Object res = to.getObject(parameterName, map);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public Array getArray(String parameterName) throws SQLException {
        try {
            Array res = to.getArray(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        try {
            BigDecimal res = to.getBigDecimal(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public Blob getBlob(String parameterName) throws SQLException {
        try {
            Blob res = to.getBlob(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public boolean getBoolean(String parameterName) throws SQLException {
        try {
            boolean res = to.getBoolean(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public byte getByte(String parameterName) throws SQLException {
        try {
            byte res = to.getByte(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public byte[] getBytes(String parameterName) throws SQLException {
        try {
            byte[] res = to.getBytes(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public Clob getClob(String parameterName) throws SQLException {
        try {
            Clob res = to.getClob(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public Date getDate(String parameterName) throws SQLException {
        try {
            Date res = to.getDate(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        try {
            Date res = to.getDate(parameterName, cal);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public double getDouble(String parameterName) throws SQLException {
        try {
            double res = to.getDouble(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public float getFloat(String parameterName) throws SQLException {
        try {
            float res = to.getFloat(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public int getInt(String parameterName) throws SQLException {
        try {
            int res = to.getInt(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public long getLong(String parameterName) throws SQLException {
        try {
            long res = to.getLong(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public Object getObject(String parameterName) throws SQLException {
        try {
            Object res = to.getObject(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public Ref getRef(String parameterName) throws SQLException {
        try {
            Ref res = to.getRef(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public short getShort(String parameterName) throws SQLException {
        try {
            short res = to.getShort(parameterName);
            onGetParamReturn(parameterName, Short.valueOf(res));
            return res;
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public String getString(String parameterName) throws SQLException {
        try {
            String res = to.getString(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public Time getTime(String parameterName) throws SQLException {
        try {
            Time res = to.getTime(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        try {
            Time res = to.getTime(parameterName, cal);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public Timestamp getTimestamp(String parameterName) throws SQLException {
        try {
            Timestamp res = to.getTimestamp(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        try {
            Timestamp res= to.getTimestamp(parameterName, cal);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public URL getURL(String parameterName) throws SQLException {
        try {
            URL res = to.getURL(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }


    public Reader getCharacterStream(String parameterName) throws SQLException {
        try {
            Reader res = to.getCharacterStream(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public Reader getNCharacterStream(String parameterName) throws SQLException {
        try {
            Reader res = to.getNCharacterStream(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public NClob getNClob(String parameterName) throws SQLException {
        try {
            NClob res = to.getNClob(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public String getNString(String parameterName) throws SQLException {
        try {
            String res = to.getNString(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public RowId getRowId(String parameterName) throws SQLException {
        try {
            RowId res = to.getRowId(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public SQLXML getSQLXML(String parameterName) throws SQLException {
        try {
            SQLXML res = to.getSQLXML(parameterName);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }

    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        try {
            T res = to.getObject(parameterName, type);
            return onGetParamReturn(parameterName, res);
        } catch (SQLException ex) {
            throw onGetParamException(parameterName, ex);
        }
    }


    // implements java.sql.CallableStatement setXX(paramName, value) : set parameter value by name
    // ------------------------------------------------------------------------

    public final void setNull(String parameterName, int sqlType) throws SQLException {
        onSetParamValue(parameterName, null).sqlType(sqlType);
        to.setNull(parameterName, sqlType);
    }

    public final void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        onSetParamValue(parameterName, null).sqlType(sqlType).typeName(typeName);
        to.setNull(parameterName, sqlType, typeName);
    }


    public final void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        onSetParamValue(parameterName, new SefStatementParamInfo.PairValueWithLength(x, length));
        to.setAsciiStream(parameterName, x, length);
    }

    public final void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setBigDecimal(parameterName, x);
    }

    public final void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        onSetParamValue(parameterName, new SefStatementParamInfo.PairValueWithLength(x, length));
        to.setBinaryStream(parameterName, x, length);
    }

    public final void setBoolean(String parameterName, boolean x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setBoolean(parameterName, x);
    }

    public final void setByte(String parameterName, byte x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setByte(parameterName, x);
    }

    public final void setBytes(String parameterName, byte[] x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setBytes(parameterName, x);
    }

    public final void setCharacterStream(String parameterName, Reader x, int length) throws SQLException {
        onSetParamValue(parameterName, new SefStatementParamInfo.PairValueWithLength(x, length));
        to.setCharacterStream(parameterName, x, length);
    }

    public final void setDate(String parameterName, Date x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setDate(parameterName, x);
    }

    public final void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        onSetParamValue(parameterName, new SefStatementParamInfo.PairDateWithCalendar(x, cal));
        to.setDate(parameterName, x, cal);
    }

    public final void setDouble(String parameterName, double x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setDouble(parameterName, x);
    }

    public final void setFloat(String parameterName, float x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setFloat(parameterName, x);
    }

    public final void setInt(String parameterName, int x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setInt(parameterName, x);
    }

    public final void setLong(String parameterName, long x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setLong(parameterName, x);
    }

    public final void setObject(String parameterName, Object x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setObject(parameterName, x);
    }

    public final void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        onSetParamValue(parameterName, x).targetSqlType(targetSqlType);
        to.setObject(parameterName, x, targetSqlType);
    }

    public final void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        onSetParamValue(parameterName, x).targetSqlType(targetSqlType).scale(scale);
        to.setObject(parameterName, x, targetSqlType, scale);
    }

    public final void setShort(String parameterName, short x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setShort(parameterName, x);
    }

    public final void setString(String parameterName, String x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setString(parameterName, x);
    }

    public final void setTime(String parameterName, Time x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setTime(parameterName, x);
    }

    public final void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        onSetParamValue(parameterName, new SefStatementParamInfo.PairDateWithCalendar(x, cal));
        to.setTime(parameterName, x, cal);
    }

    public final void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setTimestamp(parameterName, x);
    }

    public final void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        onSetParamValue(parameterName, new SefStatementParamInfo.PairDateWithCalendar(x, cal));
        to.setTimestamp(parameterName, x, cal);
    }

    public final void setURL(String parameterName, URL x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setURL(parameterName, x);
    }

    public final void setRowId(String parameterName, RowId x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setRowId(parameterName, x);
    }

    public final void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setAsciiStream(parameterName, x);
    }

    public final void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        onSetParamValue(parameterName, new SefStatementParamInfo.PairValueWithLength(x, length));
        to.setAsciiStream(parameterName, x, length);
    }

    public final void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setBinaryStream(parameterName, x);
    }

    public final void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        onSetParamValue(parameterName, new SefStatementParamInfo.PairValueWithLength(x, length));
        to.setBinaryStream(parameterName, x, length);
    }

    public final void setBlob(String parameterName, Blob x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setBlob(parameterName, x);
    }

    public final void setBlob(String parameterName, InputStream x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setBlob(parameterName, x);
    }

    public final void setBlob(String parameterName, InputStream x, long length) throws SQLException {
        onSetParamValue(parameterName, new SefStatementParamInfo.PairValueWithLength(x, length));
        to.setBlob(parameterName, x, length);
    }

    public final void setCharacterStream(String parameterName, Reader x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setCharacterStream(parameterName, x);
    }

    public final void setCharacterStream(String parameterName, Reader x, long length) throws SQLException {
        onSetParamValue(parameterName, new SefStatementParamInfo.PairValueWithLength(x, length));
        to.setCharacterStream(parameterName, x, length);
    }

    public final void setClob(String parameterName, Clob x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setClob(parameterName, x);
    }

    public final void setClob(String parameterName, Reader x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setClob(parameterName, x);
    }

    public final void setClob(String parameterName, Reader x, long length) throws SQLException {
        onSetParamValue(parameterName, new SefStatementParamInfo.PairValueWithLength(x, length));
        to.setClob(parameterName, x, length);
    }

    public final void setNCharacterStream(String parameterName, Reader x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setNCharacterStream(parameterName, x);
    }

    public final void setNCharacterStream(String parameterName, Reader x, long length) throws SQLException {
        onSetParamValue(parameterName, new SefStatementParamInfo.PairValueWithLength(x, length));
        to.setNCharacterStream(parameterName, x, length);
    }

    public final void setNClob(String parameterName, NClob x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setNClob(parameterName, x);
    }

    public final void setNClob(String parameterName, Reader x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setNClob(parameterName, x);
    }

    public final void setNClob(String parameterName, Reader x, long length) throws SQLException {
        onSetParamValue(parameterName, new SefStatementParamInfo.PairValueWithLength(x, length));
        to.setNClob(parameterName, x, length);
    }

    public final void setNString(String parameterName, String x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setNString(parameterName, x);
    }

    public final void setSQLXML(String parameterName, SQLXML x) throws SQLException {
        onSetParamValue(parameterName, x);
        to.setSQLXML(parameterName, x);
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "SefCallableStatementProxy [to=" + to + "]";
    }

}
