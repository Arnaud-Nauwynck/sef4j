package org.sef4j.jdbc.wrappers;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.jdbc.util.ConnectionUtils;

/**
 * java.sql.Connection proxy instrumented for using LocalCallStack.push()/pop()
 *
 * design pattern:
 *  - Bridge/Proxy to java.sql.Connection
 *  - factory of Sef4JStatement / Sef4JPreparedStatement / Sef4JCallableStatement
 *      (replacement for sql.Statement / sql.PreparedStatement / sql.CallableStatement)
 *
 */
public class SefConnectionProxy implements Connection {

    private SefDataSourceProxy owner;
    
    /** underlying for proxy */
    private final Connection to;
	
    protected int connId;
    
    // constructor
    // ------------------------------------------------------------------------
        
    public SefConnectionProxy(SefDataSourceProxy owner, Connection to) {
        this(owner, to, -1);
    }
    
    public SefConnectionProxy(SefDataSourceProxy owner, Connection to, int connId) {
        this.owner = owner;
        this.to = to;
        this.connId = connId;
    }

    // ------------------------------------------------------------------------
    
    public SefDataSourceProxy getOwner() {
        return owner;
    }
    
    public Connection getUnderlyingConnection() {
        return to;
    }

    public int getUnderlyingConnectionId() {
        return connId;
    }


    public void onChildStatementClose(SefStatementProxy statement) {
        StackPopper toPop = LocalCallStack.meth("onChildStatementClose")
                .withParam("statement", statement)
                .push();
        try {
            // TOADD: may decrement counter, update child List ...
        } finally {
            toPop.close();
        }
    }
    
    
    // implements java.sql.Connection
    // ------------------------------------------------------------------------

    public void close() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("close").push();
        try {
            to.close();
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
        if (owner != null) {
            owner.onChildConnectionClose(this);
        }
    }

    public boolean isClosed() throws SQLException {
        boolean res = to.isClosed();
        return res;
    }

    
    // create sub Statement/PreparedStatment... => create real statement + wrap in logger   
    // ------------------------------------------------------------------------

    public Statement createStatement() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("createStatment").push();
        try {
            Statement tmpres = to.createStatement();
            SefStatementProxy res = new SefStatementProxy(this, tmpres);
            return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("prepareStatement").push();
        try {
            PreparedStatement p = to.prepareStatement(sql);
            SefPreparedStatementProxy res = new SefPreparedStatementProxy(this, p, sql);
            return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("prepareCall").push();
        try {
            CallableStatement tmpres = to.prepareCall(sql);
            SefCallableStatementProxy res = new SefCallableStatementProxy(this, tmpres, sql);
            return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("createStatment(int,int)")
                .withParam("resultSetType", resultSetType)
                .withParam("resultSetConcurrency", resultSetConcurrency)
                .push();
        try {
            Statement p = to.createStatement(resultSetType, resultSetConcurrency);
            SefStatementProxy res = new SefStatementProxy(this, p, resultSetType, resultSetConcurrency);
            return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }

    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("prepareStatement(String,int,int)")
                .withParam("resultSetType", resultSetType)
                .withParam("resultSetConcurrency", resultSetConcurrency)
                .push();
        try {
            PreparedStatement tmpres = to.prepareStatement(sql, resultSetType, resultSetConcurrency);
            SefPreparedStatementProxy res = new SefPreparedStatementProxy(this, tmpres, sql, resultSetType, resultSetConcurrency);
            return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }        
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("prepareCall(String,int,int)")
                .withParam("resultSetType", resultSetType)
                .withParam("resultSetConcurrency", resultSetConcurrency)
                .push();
        try {
            CallableStatement tmpres = to.prepareCall(sql, resultSetType, resultSetConcurrency);
            SefCallableStatementProxy res = new SefCallableStatementProxy(this, tmpres, sql, resultSetType, resultSetConcurrency);
            return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("createStatement(int,int,int)")
                .withParam("resultSetType", resultSetType)
                .withParam("resultSetConcurrency", resultSetConcurrency)
                .withParam("resultSetHoldability", resultSetHoldability)
                .push();
        try {
            Statement tmpres = to.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
            SefStatementProxy res = new SefStatementProxy(this, tmpres, resultSetType, resultSetConcurrency, resultSetHoldability);
            return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("prepareCall(String,int,int,int)")
                .withParam("resultSetType", resultSetType)
                .withParam("resultSetConcurrency", resultSetConcurrency)
                .withParam("resultSetHoldability", resultSetHoldability)
                .push();
        try {
            CallableStatement tmpres = to.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
            SefCallableStatementProxy res = new SefCallableStatementProxy(this, tmpres, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
            return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        PreparedStatement stmt = to.prepareStatement(sql, autoGeneratedKeys);
        return new SefPreparedStatementProxy(this, stmt, sql);
    }

    public PreparedStatement prepareStatement(String sql,
                                              int resultSetType,
                                              int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        PreparedStatement stmt = to.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        return new SefPreparedStatementProxy(this, stmt, sql, resultSetType, resultSetConcurrency); // TODO resultSetHoldability;
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        PreparedStatement stmt = to.prepareStatement(sql, columnIndexes);
        return new SefPreparedStatementProxy(this, stmt, sql);
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        PreparedStatement stmt = to.prepareStatement(sql, columnNames);
        return new SefPreparedStatementProxy(this, stmt, sql);
    }

    // implements java.sql.Connection : delegate to underlying connection + log pre/post 
    // ------------------------------------------------------------------------

    public String nativeSQL(String sql) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("nativeSQL").withParam("sql", sql).push();
        try {
            String res = to.nativeSQL(sql);
            
            return LocalCallStack.pushPopParentReturn(res);
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        // distinguish true/false as pseudo method name for stats
        String pseudoMeth = (autoCommit)? "setAutoCommit_true" : "setAutoCommit_false";
        StackPopper toPop = LocalCallStack.meth(pseudoMeth).push();
        try {
            to.setAutoCommit(autoCommit);
            
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public boolean getAutoCommit() throws SQLException {
        return to.getAutoCommit();
    }

    public void setTransactionIsolation(int level) throws SQLException {
        String pseudoMeth = "setTransactionIsolation_" + ConnectionUtils.transactionLevelToString(level);
        StackPopper toPop = LocalCallStack.meth(pseudoMeth).push();
        try {
            to.setTransactionIsolation(level);
            
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public int getTransactionIsolation() throws SQLException {
        return to.getTransactionIsolation();
    }

    public void commit() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("commit").push();
        try {
            to.commit();
            
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public void rollback() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("rollback").push();
        try {
            to.rollback();
            
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("releaseSavepoint").push();
        try {
            to.releaseSavepoint(savepoint);
            
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("rollback_savepoint").push();
        try {
            to.rollback(savepoint);
            
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public Savepoint setSavepoint() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("setSavepoint").push();
        try {
            return to.setSavepoint();
            
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("setSavepoint_name").withParam("name", name).push();
        try {
            return to.setSavepoint(name);
            
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public void abort(Executor executor) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("abort")
                // .withParam("executor", executor)
                .push();
        try {
            to.abort(executor);
            
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        String pseudoMeth = (readOnly)? "setReadOnly_true" : "setReadOnly_false";
        StackPopper toPop = LocalCallStack.meth(pseudoMeth).push();
        try {
            to.setReadOnly(readOnly);
            
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public boolean isReadOnly() throws SQLException {
        return to.isReadOnly();
    }


    public int getHoldability() throws SQLException {
        return to.getHoldability();
    }

    public void setHoldability(int holdability) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("setHoldability").withParam("holdability", holdability).push();
        try {
            to.setHoldability(holdability);
            
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    
    public DatabaseMetaData getMetaData() throws SQLException {
        return to.getMetaData();
    }

    public void setCatalog(String catalog) throws SQLException {
        to.setCatalog(catalog);
    }

    public String getCatalog() throws SQLException {
        return to.getCatalog();
    }

    public SQLWarning getWarnings() throws SQLException {
        return to.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        to.clearWarnings();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map getTypeMap() throws SQLException {
        return to.getTypeMap();
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        to.setTypeMap(map);
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return to.createArrayOf(typeName, elements);
    }

    public Blob createBlob() throws SQLException {
        return to.createBlob();
    }

    public Clob createClob() throws SQLException {
        return to.createClob();
    }

    public NClob createNClob() throws SQLException {
        return to.createNClob();
    }

    public SQLXML createSQLXML() throws SQLException {
        return to.createSQLXML();
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return to.createStruct(typeName, attributes);
    }

    public Properties getClientInfo() throws SQLException {
        return to.getClientInfo();
    }

    public String getClientInfo(String name) throws SQLException {
        return to.getClientInfo(name);
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        to.setClientInfo(properties);
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        to.setClientInfo(name, value);        
    }

    public boolean isValid(int timeout) throws SQLException {
        return to.isValid(timeout);
    }
    
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return to.isWrapperFor(iface);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return to.unwrap(iface);
    }

    public void setSchema(String schema) throws SQLException {
        to.setSchema(schema);
    }

    public String getSchema() throws SQLException {
        return to.getSchema();
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        to.setNetworkTimeout(executor, milliseconds);
    }

    public int getNetworkTimeout() throws SQLException {
        return to.getNetworkTimeout();
    }

    // override Object
    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof SefConnectionProxy))
            return false;
        SefConnectionProxy o = (SefConnectionProxy) obj;
        return to.equals(o.to);
    }
    
    @Override
    public String toString() {
        return "SefConnectionProxy[" + connId + "]";
    }

    
}
