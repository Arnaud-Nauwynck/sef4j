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

/**
 * Proxy for java.sql.Statement + wrapp all calls with pre()/log.post() + set params 
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

    // internal
    // ------------------------------------------------------------------------

    /** internal */
    protected void registerOut(int parameterIndex, ParamInfo p) {
        super.set(parameterIndex, p);
    }

    /** internal */
    protected void fillOutRes(int parameterIndex, Object value) {
        // TODO NOT IMPLEMENTED
        //        ParamInfo p = getParamInfo(parameterIndex);
        //        p.isRuntimeResAlreadyGet = true;
        //        p.outResValue = value;
        //        p.outResException = null;
    }

    protected void fillOutRes(String parameterName, Object value) {
        // TODO NOT IMPLEMENTED
        //    ParamInfo p = getParamInfo(parameterName);
        //    p.isRuntimeResAlreadyGet = true;
        //    p.outResValue = value;
        //    p.outResException = null;        
    }

    protected void fillOutEx(int parameterIndex, Exception ex) {
        // do nothing / log?
    }

    protected void fillOutEx(String parameterName, Exception ex) {
        // do nothing / log?
    }

    /** internal */
    protected void set(String parameterName, ParamInfo paramInfo) {
        //         TODO NOT IMPLEMENTED

        //    try {
        //      if (index >= 0) {
        //        setParamChanged();
        //        if (index >= params.size()) {
        //            // params.ensureSize..
        //            params.ensureCapacity(index);            
        //            // TOCHECK?? 
        //            int nb = params.size() - index;
        //            for (int i = 0; i <  nb; i++) params.add(null);
        //        }
        //        params.set(index, paramInfo);
        //      } else {
        //        // ignore error here in log
        //      }
        //    } catch (Exception ex) {
        //      log.error("set", ex);
        //    }

        // TODO NOT IMPLEMENTED
    }

    // implements java.sql.CallableStatement, wrapp by logging / storing param..
    // ------------------------------------------------------------------------

    
    public boolean wasNull() throws SQLException {
        boolean res;
        pre("wasNull", "");
        try {
            res = to.wasNull();
            if (res)
                postRes("true");
            else
                postDefaultRes("false");
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    //implements java.sql.CallableStatement get/set parameters by index
    // ------------------------------------------------------------------------

    public String getString(int parameterIndex) throws SQLException {
        String res;
        try {
            res = to.getString(parameterIndex);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public boolean getBoolean(int parameterIndex) throws SQLException {
        boolean res;
        try {
            res = to.getBoolean(parameterIndex);
            fillOutRes(parameterIndex, new Boolean(res));
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public byte getByte(int parameterIndex) throws SQLException {
        byte res;
        try {
            res = to.getByte(parameterIndex);
            fillOutRes(parameterIndex, new Byte(res));
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public short getShort(int parameterIndex) throws SQLException {
        short res;
        try {
            res = to.getShort(parameterIndex);
            fillOutRes(parameterIndex, new Short(res));
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public int getInt(int parameterIndex) throws SQLException {
        int res;
        try {
            res = to.getInt(parameterIndex);
            fillOutRes(parameterIndex, new Integer(res));
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public long getLong(int parameterIndex) throws SQLException {
        long res;
        try {
            res = to.getLong(parameterIndex);
            fillOutRes(parameterIndex, new Long(res));
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public float getFloat(int parameterIndex) throws SQLException {
        float res;
        try {
            res = to.getFloat(parameterIndex);
            fillOutRes(parameterIndex, new Float(res));
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public double getDouble(int parameterIndex) throws SQLException {
        double res;
        try {
            res = to.getDouble(parameterIndex);
            fillOutRes(parameterIndex, new Double(res));
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    @Deprecated
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        BigDecimal res;
        try {
            res = to.getBigDecimal(parameterIndex, scale);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public byte[] getBytes(int parameterIndex) throws SQLException {
        byte[] res;
        try {
            res = to.getBytes(parameterIndex);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public Date getDate(int parameterIndex) throws SQLException {
        Date res;
        try {
            res = to.getDate(parameterIndex);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public Time getTime(int parameterIndex) throws SQLException {
        Time res;
        try {
            res = to.getTime(parameterIndex);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        Timestamp res;
        try {
            res = to.getTimestamp(parameterIndex);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public Object getObject(int parameterIndex) throws SQLException {
        Object res;
        try {
            res = to.getObject(parameterIndex);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        BigDecimal res;
        try {
            res = to.getBigDecimal(parameterIndex);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }
    
    public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
        Object res;
        try {
            res = to.getObject(parameterIndex, map);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        Object res;
        try {
            res = to.getObject(parameterName, map);
            fillOutRes(parameterName, res);
        } catch (SQLException ex) {
            fillOutEx(parameterName, ex);
            throw ex;
        }
        return res;
    }


    public Ref getRef(int parameterIndex) throws SQLException {
        Ref res;
        try {
            res = to.getRef(parameterIndex);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public Blob getBlob(int parameterIndex) throws SQLException {
        Blob res;
        try {
            res = to.getBlob(parameterIndex);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public Clob getClob(int parameterIndex) throws SQLException {
        Clob res;
        try {
            res = to.getClob(parameterIndex);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public Array getArray(int parameterIndex) throws SQLException {
        Array res;
        try {
            res = to.getArray(parameterIndex);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        Date res;
        try {
            res = to.getDate(parameterIndex, cal);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        Time res;
        try {
            res = to.getTime(parameterIndex, cal);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        Timestamp res;
        try {
            res = to.getTimestamp(parameterIndex, cal);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    public URL getURL(int parameterIndex) throws SQLException {
        URL res;
        try {
            res = to.getURL(parameterIndex);
            fillOutRes(parameterIndex, res);
        } catch (SQLException ex) {
            fillOutEx(parameterIndex, ex);
            throw ex;
        }
        return res;
    }

    //implements java.sql.CallableStatement get/set parameters by names
    // -------------------------------------------------------------

    public Array getArray(String parameterName) throws SQLException {
        return to.getArray(parameterName);
    }

    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        return to.getBigDecimal(parameterName);
    }

    public Blob getBlob(String parameterName) throws SQLException {
        return to.getBlob(parameterName);
    }

    public boolean getBoolean(String parameterName) throws SQLException {
        return to.getBoolean(parameterName);
    }

    public byte getByte(String parameterName) throws SQLException {
        return to.getByte(parameterName);
    }

    public byte[] getBytes(String parameterName) throws SQLException {
        return to.getBytes(parameterName);
    }

    public Clob getClob(String parameterName) throws SQLException {
        return to.getClob(parameterName);
    }

    public Date getDate(String parameterName) throws SQLException {
        return to.getDate(parameterName);
    }

    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        return to.getDate(parameterName, cal);
    }

    public double getDouble(String parameterName) throws SQLException {
        return to.getDouble(parameterName);
    }

    public float getFloat(String parameterName) throws SQLException {
        return to.getFloat(parameterName);
    }

    public int getInt(String parameterName) throws SQLException {
        return to.getInt(parameterName);
    }

    public long getLong(String parameterName) throws SQLException {
        return to.getLong(parameterName);
    }

    public Object getObject(String parameterName) throws SQLException {
        return to.getObject(parameterName);
    }

    public Ref getRef(String parameterName) throws SQLException {
        return to.getRef(parameterName);
    }

    public short getShort(String parameterName) throws SQLException {
        short res;
        try {
            res = to.getShort(parameterName);
            fillOutRes(parameterName, new Short(res));
        } catch (SQLException ex) {
            fillOutEx(parameterName, ex);
            throw ex;
        }
        return res;

    }

    public String getString(String parameterName) throws SQLException {
        String res;
        try {
            res = to.getString(parameterName);
            fillOutRes(parameterName, res);
        } catch (SQLException ex) {
            fillOutEx(parameterName, ex);
            throw ex;
        }
        return res;
    }

    public Time getTime(String parameterName) throws SQLException {
        Time res;
        try {
            res = to.getTime(parameterName);
            fillOutRes(parameterName, res);
        } catch (SQLException ex) {
            fillOutEx(parameterName, ex);
            throw ex;
        }
        return res;
    }

    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        Time res;
        try {
            res = to.getTime(parameterName, cal);
            fillOutRes(parameterName, res);
        } catch (SQLException ex) {
            fillOutEx(parameterName, ex);
            throw ex;
        }
        return res;
    }

    public Timestamp getTimestamp(String parameterName) throws SQLException {
        Timestamp res;
        try {
            res = to.getTimestamp(parameterName);
            fillOutRes(parameterName, res);
        } catch (SQLException ex) {
            fillOutEx(parameterName, ex);
            throw ex;
        }
        return res;
    }

    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        Timestamp res;
        try {
            res = to.getTimestamp(parameterName, cal);
            fillOutRes(parameterName, res);
        } catch (SQLException ex) {
            fillOutEx(parameterName, ex);
            throw ex;
        }
        return res;
    }

    public URL getURL(String parameterName) throws SQLException {
        URL res;
        try {
            res = to.getURL(parameterName);
            fillOutRes(parameterName, res);
        } catch (SQLException ex) {
            fillOutEx(parameterName, ex);
            throw ex;
        }
        return res;
    }

    // ------------------------------------------------------------------------

    
    public final void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        set(parameterName, new ParamInfo("'--AsciiStream.. (not  supported  in log) length:" + length + "--'"));
        to.setAsciiStream(parameterName, x, length);
    }

    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        to.setBigDecimal(parameterName, x);
    }

    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        to.setBinaryStream(parameterName, x, length);
    }

    public void setBoolean(String parameterName, boolean x) throws SQLException {
        to.setBoolean(parameterName, x);
    }

    public void setByte(String parameterName, byte x) throws SQLException {
        to.setByte(parameterName, x);
    }

    public void setBytes(String parameterName, byte[] x) throws SQLException {
        to.setBytes(parameterName, x);
    }

    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        to.setCharacterStream(parameterName, reader, length);
    }

    public void setDate(String parameterName, Date x) throws SQLException {
        to.setDate(parameterName, x);
    }

    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        to.setDate(parameterName, x, cal);
    }

    public void setDouble(String parameterName, double x) throws SQLException {
        to.setDouble(parameterName, x);
    }

    public void setFloat(String parameterName, float x) throws SQLException {
        to.setFloat(parameterName, x);
    }

    public void setInt(String parameterName, int x) throws SQLException {
        to.setInt(parameterName, x);
    }

    public void setLong(String parameterName, long x) throws SQLException {
        to.setLong(parameterName, x);
    }

    public void setNull(String parameterName, int sqlType) throws SQLException {
        to.setNull(parameterName, sqlType);
    }

    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        to.setNull(parameterName, sqlType, typeName);
    }

    public void setObject(String parameterName, Object x) throws SQLException {
        to.setObject(parameterName, x);
    }

    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        to.setObject(parameterName, x, targetSqlType);
    }

    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        to.setObject(parameterName, x, targetSqlType, scale);
    }

    public void setShort(String parameterName, short x) throws SQLException {
        to.setShort(parameterName, x);
    }

    public void setString(String parameterName, String x) throws SQLException {
        to.setString(parameterName, x);
    }

    public void setTime(String parameterName, Time x) throws SQLException {
        to.setTime(parameterName, x);
    }

    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        to.setTime(parameterName, x, cal);
    }

    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        to.setTimestamp(parameterName, x);
    }

    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        to.setTimestamp(parameterName, x, cal);
    }

    public void setURL(String parameterName, URL val) throws SQLException {
        to.setURL(parameterName, val);
    }

    //implements java.sql.CallableStatement output parameters
    // -------------------------------------------------------------

    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        ParamInfo p = new ParamInfo(true, sqlType);
        registerOut(parameterIndex, p);
        to.registerOutParameter(parameterIndex, sqlType);
    }

    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        ParamInfo p = new ParamInfo(true, sqlType);
        p.setScale(scale);
        registerOut(parameterIndex, p);
        to.registerOutParameter(parameterIndex, sqlType);
    }

    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        ParamInfo p = new ParamInfo(true, sqlType);
        p.setTypeName(typeName);
        registerOut(parameterIndex, p);
        to.registerOutParameter(parameterIndex, sqlType);
    }

    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        to.registerOutParameter(parameterName, sqlType);
    }

    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        to.registerOutParameter(parameterName, sqlType, scale);
    }

    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        to.registerOutParameter(parameterName, sqlType, typeName);
    }


    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        return to.getCharacterStream(parameterIndex);
    }

    public Reader getCharacterStream(String parameterName) throws SQLException {
        return to.getCharacterStream(parameterName);
    }

    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        return to.getNCharacterStream(parameterIndex);
    }

    public Reader getNCharacterStream(String parameterName) throws SQLException {
        return to.getNCharacterStream(parameterName);
    }

    public NClob getNClob(int parameterIndex) throws SQLException {
        return to.getNClob(parameterIndex);
    }

    public NClob getNClob(String parameterName) throws SQLException {
        return to.getNClob(parameterName);
    }

    public String getNString(int parameterIndex) throws SQLException {
        return to.getNString(parameterIndex);
    }

    public String getNString(String parameterName) throws SQLException {
        return to.getNString(parameterName);
    }
    
    public RowId getRowId(int parameterIndex) throws SQLException {
        return to.getRowId(parameterIndex);
    }

    public void setRowId(String parameterName, RowId x) throws SQLException {
        to.setRowId(parameterName, x);
    }

    public RowId getRowId(String parameterName) throws SQLException {
        return to.getRowId(parameterName);
    }
    
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        return to.getSQLXML(parameterIndex);
    }

    public SQLXML getSQLXML(String parameterName) throws SQLException {
        return to.getSQLXML(parameterName);
    }

    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        to.setAsciiStream(parameterName, x);
    }

    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        to.setAsciiStream(parameterName, x, length);
    }

    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        to.setBinaryStream(parameterName, x);
    }

    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        to.setBinaryStream(parameterName, x, length);
    }

    public void setBlob(String parameterName, Blob x) throws SQLException {
        to.setBlob(parameterName, x);
    }

    public void setBlob(String parameterName, InputStream x) throws SQLException {
        to.setBlob(parameterName, x);
    }

    public void setBlob(String parameterName, InputStream x, long length) throws SQLException {
        to.setBlob(parameterName, x, length);
    }

    public void setCharacterStream(String parameterName, Reader x) throws SQLException {
        to.setCharacterStream(parameterName, x);
    }

    public void setCharacterStream(String parameterName, Reader x, long length) throws SQLException {
        to.setCharacterStream(parameterName, x, length);
    }

    public void setClob(String parameterName, Clob x) throws SQLException {
        to.setClob(parameterName, x);
    }

    public void setClob(String parameterName, Reader x) throws SQLException {
        to.setClob(parameterName, x);
    }

    public void setClob(String parameterName, Reader x, long length) throws SQLException {
        to.setClob(parameterName, x, length);
    }

    public void setNCharacterStream(String parameterName, Reader x) throws SQLException {
        to.setNCharacterStream(parameterName, x);
    }

    public void setNCharacterStream(String parameterName, Reader x, long length) throws SQLException {
        to.setNCharacterStream(parameterName, x, length);
    }

    public void setNClob(String parameterName, NClob x) throws SQLException {
        to.setNClob(parameterName, x);
    }

    public void setNClob(String parameterName, Reader x) throws SQLException {
        to.setNClob(parameterName, x);
    }

    public void setNClob(String parameterName, Reader x, long length) throws SQLException {
        to.setNClob(parameterName, x, length);
    }

    public void setNString(String parameterName, String x) throws SQLException {
        to.setNString(parameterName, x);
    }

    public void setSQLXML(String parameterName, SQLXML x) throws SQLException {
        to.setSQLXML(parameterName, x);
    }

    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        return to.getObject(parameterIndex, type);
    }

    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        return to.getObject(parameterName, type);
    }

    
}
