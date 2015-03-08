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
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.sef4j.callstack.LocalCallStack;
import org.sef4j.callstack.CallStackElt.StackPopper;

/**
 * java.sql.ResultSet proxy for instrumenting with LocalCallStack.push()/pop() 
 */
public class SefResultSetProxy implements ResultSet {

	private static final String CNAME = SefResultSetProxy.class.getName();
	
    private ResultSet to;
    private SefStatementProxy parent;
    
    // ------------------------------------------------------------------------
    
    public SefResultSetProxy(SefStatementProxy parent, ResultSet to) {
        this.to = to;
        this.parent = parent;
    }

    //  implements JDBC life-cycle management
    // ------------------------------------------------------------------------

    public void close() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "next").push();
        try {
            to.close();
        } catch(SQLException ex) {
            throw toPop.returnException(ex);
        } catch(RuntimeException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public boolean isClosed() throws SQLException {
        return to.isClosed();
    }


    //  implements JDBC dependent objects
    // ------------------------------------------------------------------------

    public Statement getStatement() throws SQLException {
        return parent; // instead of ... return to.getStatement();
    }

    public String getCursorName() throws SQLException {
        return to.getCursorName();
    }

    public SQLWarning getWarnings() throws SQLException {
        return to.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        to.clearWarnings();
    }
    

    public ResultSetMetaData getMetaData() throws SQLException {
        return to.getMetaData();
    }

    public int findColumn(String columnLabel) throws SQLException {
        return to.findColumn(columnLabel);
    }

    
    // implements JDBC ResultSet navigation
    // ------------------------------------------------------------------------
    
    
    public boolean next() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "next").push();
        try {
            boolean res = to.next();
            return toPop.returnValue(res);
        } catch(SQLException ex) {
            throw toPop.returnException(ex);
        } catch(RuntimeException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public boolean isBeforeFirst() throws SQLException {
        return to.isBeforeFirst();
    }

    public boolean isAfterLast() throws SQLException {
        return to.isAfterLast();
    }

    public boolean isFirst() throws SQLException {
        return to.isFirst();
    }

    public boolean isLast() throws SQLException {
        return to.isLast();
    }

    public void beforeFirst() throws SQLException {
        to.beforeFirst();
    }

    public void afterLast() throws SQLException {
        to.afterLast();
    }

    public boolean first() throws SQLException {
        return to.first();
    }

    public boolean last() throws SQLException {
        return to.last();
    }

    public int getRow() throws SQLException {
        return to.getRow();
    }

    public boolean absolute(int row) throws SQLException {
        return to.absolute(row);
    }

    public boolean relative(int rows) throws SQLException {
        return to.relative(rows);
    }

    public boolean previous() throws SQLException {
        return to.previous();
    }

    public void setFetchDirection(int direction) throws SQLException {
        to.setFetchDirection(direction);
    }

    public int getFetchDirection() throws SQLException {
        return to.getFetchDirection();
    }

    public void setFetchSize(int rows) throws SQLException {
        to.setFetchSize(rows);
    }

    public int getFetchSize() throws SQLException {
        return to.getFetchSize();
    }

    public int getType() throws SQLException {
        return to.getType();
    }

    public void moveToInsertRow() throws SQLException {
        to.moveToInsertRow();
    }

    public void moveToCurrentRow() throws SQLException {
        to.moveToCurrentRow();
    }

    public int getHoldability() throws SQLException {
        return to.getHoldability();
    }

    
    // ------------------------------------------------------------------------
    
    public boolean wasNull() throws SQLException {
        return to.wasNull();
    }

    public String getString(int columnIndex) throws SQLException {
        return to.getString(columnIndex);
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        return to.getBoolean(columnIndex);
    }

    public byte getByte(int columnIndex) throws SQLException {
        return to.getByte(columnIndex);
    }

    public short getShort(int columnIndex) throws SQLException {
        return to.getShort(columnIndex);
    }

    public int getInt(int columnIndex) throws SQLException {
        return to.getInt(columnIndex);
    }

    public long getLong(int columnIndex) throws SQLException {
        return to.getLong(columnIndex);
    }

    public float getFloat(int columnIndex) throws SQLException {
        return to.getFloat(columnIndex);
    }

    public double getDouble(int columnIndex) throws SQLException {
        return to.getDouble(columnIndex);
    }

    @Deprecated
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return to.getBigDecimal(columnIndex, scale);
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        return to.getBytes(columnIndex);
    }

    public Date getDate(int columnIndex) throws SQLException {
        return to.getDate(columnIndex);
    }

    public Time getTime(int columnIndex) throws SQLException {
        return to.getTime(columnIndex);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return to.getTimestamp(columnIndex);
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return to.getAsciiStream(columnIndex);
    }

    @Deprecated
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return to.getUnicodeStream(columnIndex);
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return to.getBinaryStream(columnIndex);
    }

    public String getString(String columnLabel) throws SQLException {
        return to.getString(columnLabel);
    }

    public boolean getBoolean(String columnLabel) throws SQLException {
        return to.getBoolean(columnLabel);
    }

    public byte getByte(String columnLabel) throws SQLException {
        return to.getByte(columnLabel);
    }

    public short getShort(String columnLabel) throws SQLException {
        return to.getShort(columnLabel);
    }

    public int getInt(String columnLabel) throws SQLException {
        return to.getInt(columnLabel);
    }

    public long getLong(String columnLabel) throws SQLException {
        return to.getLong(columnLabel);
    }

    public float getFloat(String columnLabel) throws SQLException {
        return to.getFloat(columnLabel);
    }

    public double getDouble(String columnLabel) throws SQLException {
        return to.getDouble(columnLabel);
    }

    @Deprecated
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return to.getBigDecimal(columnLabel, scale);
    }

    public byte[] getBytes(String columnLabel) throws SQLException {
        return to.getBytes(columnLabel);
    }

    public Date getDate(String columnLabel) throws SQLException {
        return to.getDate(columnLabel);
    }

    public Time getTime(String columnLabel) throws SQLException {
        return to.getTime(columnLabel);
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return to.getTimestamp(columnLabel);
    }

    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return to.getAsciiStream(columnLabel);
    }

    @Deprecated
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return to.getUnicodeStream(columnLabel);
    }

    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return to.getBinaryStream(columnLabel);
    }

    public Object getObject(int columnIndex) throws SQLException {
        return to.getObject(columnIndex);
    }

    public Object getObject(String columnLabel) throws SQLException {
        return to.getObject(columnLabel);
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return to.getCharacterStream(columnIndex);
    }

    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return to.getCharacterStream(columnLabel);
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return to.getBigDecimal(columnIndex);
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return to.getBigDecimal(columnLabel);
    }

    public RowId getRowId(int columnIndex) throws SQLException {
        return to.getRowId(columnIndex);
    }

    public RowId getRowId(String columnLabel) throws SQLException {
        return to.getRowId(columnLabel);
    }

    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return to.getObject(columnIndex, map);
    }

    public Ref getRef(int columnIndex) throws SQLException {
        return to.getRef(columnIndex);
    }

    public Blob getBlob(int columnIndex) throws SQLException {
        return to.getBlob(columnIndex);
    }

    public Clob getClob(int columnIndex) throws SQLException {
        return to.getClob(columnIndex);
    }

    public Array getArray(int columnIndex) throws SQLException {
        return to.getArray(columnIndex);
    }

    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return to.getObject(columnLabel, map);
    }

    public Ref getRef(String columnLabel) throws SQLException {
        return to.getRef(columnLabel);
    }

    public Blob getBlob(String columnLabel) throws SQLException {
        return to.getBlob(columnLabel);
    }

    public Clob getClob(String columnLabel) throws SQLException {
        return to.getClob(columnLabel);
    }

    public Array getArray(String columnLabel) throws SQLException {
        return to.getArray(columnLabel);
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return to.getDate(columnIndex, cal);
    }

    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return to.getDate(columnLabel, cal);
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return to.getTime(columnIndex, cal);
    }

    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return to.getTime(columnLabel, cal);
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return to.getTimestamp(columnIndex, cal);
    }

    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return to.getTimestamp(columnLabel, cal);
    }

    public URL getURL(int columnIndex) throws SQLException {
        return to.getURL(columnIndex);
    }

    public URL getURL(String columnLabel) throws SQLException {
        return to.getURL(columnLabel);
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return to.getObject(columnIndex, type);
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return to.getObject(columnLabel, type);
    }

    public String getNString(int columnIndex) throws SQLException {
        return to.getNString(columnIndex);
    }

    public String getNString(String columnLabel) throws SQLException {
        return to.getNString(columnLabel);
    }

    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return to.getNCharacterStream(columnIndex);
    }

    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return to.getNCharacterStream(columnLabel);
    }

    public NClob getNClob(int columnIndex) throws SQLException {
        return to.getNClob(columnIndex);
    }

    public NClob getNClob(String columnLabel) throws SQLException {
        return to.getNClob(columnLabel);
    }

    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return to.getSQLXML(columnIndex);
    }

    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return to.getSQLXML(columnLabel);
    }


    // implements ResultSet update
    // ------------------------------------------------------------------------


    public int getConcurrency() throws SQLException {
        return to.getConcurrency();
    }

    public void insertRow() throws SQLException {
        to.insertRow();
    }

    public void updateRow() throws SQLException {
        to.updateRow();
    }

    public void deleteRow() throws SQLException {
        to.deleteRow();
    }

    public void refreshRow() throws SQLException {
        to.refreshRow();
    }

    public void cancelRowUpdates() throws SQLException {
        to.cancelRowUpdates();
    }

    public boolean rowUpdated() throws SQLException {
        return to.rowUpdated();
    }

    public boolean rowInserted() throws SQLException {
        return to.rowInserted();
    }

    public boolean rowDeleted() throws SQLException {
        return to.rowDeleted();
    }

    public void updateNull(int columnIndex) throws SQLException {
        to.updateNull(columnIndex);
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        to.updateBoolean(columnIndex, x);
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        to.updateByte(columnIndex, x);
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        to.updateShort(columnIndex, x);
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        to.updateInt(columnIndex, x);
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        to.updateLong(columnIndex, x);
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        to.updateFloat(columnIndex, x);
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        to.updateDouble(columnIndex, x);
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        to.updateBigDecimal(columnIndex, x);
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        to.updateString(columnIndex, x);
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        to.updateBytes(columnIndex, x);
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        to.updateDate(columnIndex, x);
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        to.updateTime(columnIndex, x);
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        to.updateTimestamp(columnIndex, x);
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        to.updateAsciiStream(columnIndex, x, length);
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        to.updateBinaryStream(columnIndex, x, length);
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        to.updateCharacterStream(columnIndex, x, length);
    }

    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        to.updateObject(columnIndex, x, scaleOrLength);
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        to.updateObject(columnIndex, x);
    }

    public void updateNull(String columnLabel) throws SQLException {
        to.updateNull(columnLabel);
    }

    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        to.updateBoolean(columnLabel, x);
    }

    public void updateByte(String columnLabel, byte x) throws SQLException {
        to.updateByte(columnLabel, x);
    }

    public void updateShort(String columnLabel, short x) throws SQLException {
        to.updateShort(columnLabel, x);
    }

    public void updateInt(String columnLabel, int x) throws SQLException {
        to.updateInt(columnLabel, x);
    }

    public void updateLong(String columnLabel, long x) throws SQLException {
        to.updateLong(columnLabel, x);
    }

    public void updateFloat(String columnLabel, float x) throws SQLException {
        to.updateFloat(columnLabel, x);
    }

    public void updateDouble(String columnLabel, double x) throws SQLException {
        to.updateDouble(columnLabel, x);
    }

    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        to.updateBigDecimal(columnLabel, x);
    }

    public void updateString(String columnLabel, String x) throws SQLException {
        to.updateString(columnLabel, x);
    }

    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        to.updateBytes(columnLabel, x);
    }

    public void updateDate(String columnLabel, Date x) throws SQLException {
        to.updateDate(columnLabel, x);
    }

    public void updateTime(String columnLabel, Time x) throws SQLException {
        to.updateTime(columnLabel, x);
    }

    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        to.updateTimestamp(columnLabel, x);
    }

    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        to.updateAsciiStream(columnLabel, x, length);
    }

    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        to.updateBinaryStream(columnLabel, x, length);
    }

    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        to.updateCharacterStream(columnLabel, reader, length);
    }

    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        to.updateObject(columnLabel, x, scaleOrLength);
    }

    public void updateObject(String columnLabel, Object x) throws SQLException {
        to.updateObject(columnLabel, x);
    }
    
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        to.updateRef(columnIndex, x);
    }

    public void updateRef(String columnLabel, Ref x) throws SQLException {
        to.updateRef(columnLabel, x);
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        to.updateBlob(columnIndex, x);
    }

    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        to.updateBlob(columnLabel, x);
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        to.updateClob(columnIndex, x);
    }

    public void updateClob(String columnLabel, Clob x) throws SQLException {
        to.updateClob(columnLabel, x);
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        to.updateArray(columnIndex, x);
    }

    public void updateArray(String columnLabel, Array x) throws SQLException {
        to.updateArray(columnLabel, x);
    }

    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        to.updateRowId(columnIndex, x);
    }

    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        to.updateRowId(columnLabel, x);
    }

    public void updateNString(int columnIndex, String nString) throws SQLException {
        to.updateNString(columnIndex, nString);
    }

    public void updateNString(String columnLabel, String nString) throws SQLException {
        to.updateNString(columnLabel, nString);
    }

    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        to.updateNClob(columnIndex, nClob);
    }

    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        to.updateNClob(columnLabel, nClob);
    }

    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        to.updateSQLXML(columnIndex, xmlObject);
    }

    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        to.updateSQLXML(columnLabel, xmlObject);
    }

    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        to.updateNCharacterStream(columnIndex, x, length);
    }

    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        to.updateNCharacterStream(columnLabel, reader, length);
    }

    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        to.updateAsciiStream(columnIndex, x, length);
    }

    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        to.updateBinaryStream(columnIndex, x, length);
    }

    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        to.updateCharacterStream(columnIndex, x, length);
    }

    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        to.updateAsciiStream(columnLabel, x, length);
    }

    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        to.updateBinaryStream(columnLabel, x, length);
    }

    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        to.updateCharacterStream(columnLabel, reader, length);
    }

    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        to.updateBlob(columnIndex, inputStream, length);
    }

    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        to.updateBlob(columnLabel, inputStream, length);
    }

    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        to.updateClob(columnIndex, reader, length);
    }

    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        to.updateClob(columnLabel, reader, length);
    }

    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        to.updateNClob(columnIndex, reader, length);
    }

    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        to.updateNClob(columnLabel, reader, length);
    }

    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        to.updateNCharacterStream(columnIndex, x);
    }

    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        to.updateNCharacterStream(columnLabel, reader);
    }

    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        to.updateAsciiStream(columnIndex, x);
    }

    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        to.updateBinaryStream(columnIndex, x);
    }

    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        to.updateCharacterStream(columnIndex, x);
    }

    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        to.updateAsciiStream(columnLabel, x);
    }

    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        to.updateBinaryStream(columnLabel, x);
    }

    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        to.updateCharacterStream(columnLabel, reader);
    }

    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        to.updateBlob(columnIndex, inputStream);
    }

    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        to.updateBlob(columnLabel, inputStream);
    }

    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        to.updateClob(columnIndex, reader);
    }

    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        to.updateClob(columnLabel, reader);
    }

    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        to.updateNClob(columnIndex, reader);
    }

    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        to.updateNClob(columnLabel, reader);
    }

    
    // JDBC 4.2 - update override default (interface default since jdk 8)
    // ------------------------------------------------------------------------
    
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        to.updateObject(columnIndex, x, targetSqlType, scaleOrLength);
    }

    public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        to.updateObject(columnLabel, x, targetSqlType, scaleOrLength);
    }

    public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        to.updateObject(columnIndex, x, targetSqlType);
    }

    public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
        to.updateObject(columnLabel, x, targetSqlType);
    }


    // implements java.sql.Wrapper
    // ------------------------------------------------------------------------
    
    
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return to.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return to.isWrapperFor(iface);
    }
    
    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "SefResultSetProxy [to=" + to + "]";
    }

}
