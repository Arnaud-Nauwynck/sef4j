package org.sef4j.jdbc.wrappers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.jdbc.util.StatementUtils;

/**
 * Proxy for java.sql.Statement + wrapp all calls with push()/pop()
 *
 */
public class SefStatementProxy implements Statement {

    /** underlying of proxy */
    protected Statement to;

    /** parent/owner connection */
    protected SefConnectionProxy owner;

    /* may be set by ctor */
    private int ctorResultSetType;
    private int ctorResultSetConcurrency;
    private int ctorResultSetHoldability;
    
    // ------------------------------------------------------------------------

    /** Ctor */
    public SefStatementProxy(SefConnectionProxy owner, Statement to) {
        this.owner = owner;
        this.to = to;
    }

    /** Ctor */
    public SefStatementProxy(SefConnectionProxy owner, Statement to, int resultSetType, int resultSetConcurrency) {
        this.owner = owner;
        this.to = to;
        this.ctorResultSetType = resultSetType;
        this.ctorResultSetConcurrency = resultSetConcurrency;
    }

    /** Ctor */
    public SefStatementProxy(SefConnectionProxy owner, Statement to, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
        this.owner = owner;
        this.to = to;
        this.ctorResultSetType = resultSetType;
        this.ctorResultSetConcurrency = resultSetConcurrency;
        this.ctorResultSetHoldability = resultSetHoldability;
    }

    // ------------------------------------------------------------------------

    public Statement getUnderlying() {
        return to;
    }

    public final SefConnectionProxy getSefWrappedConnection() {
        return owner;
    }

    public final int getCtorResultSetConcurrency() {
        return ctorResultSetConcurrency;
    }
    
    public final int getCtorResultSetType() {
        return ctorResultSetType;
    }
    
    public int getCtorResultSetHoldability() {
        return ctorResultSetHoldability;
    }
    
    
    // Implements java.sql.Statement
    // ------------------------------------------------------------------------

    
    public final Connection getConnection() throws SQLException {
        return owner; // return owner wrapper instead of target.getConnection()  
    }

    public final void close() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("close").push();
        try {
            to.close();
        } catch(SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
        // also notify parent wrapper of closed resource
        if (owner != null) {
            owner.onChildStatementClose(this);
        }
    }

    public boolean isClosed() throws SQLException {
        return to.isClosed();
    }

    public boolean isPoolable() throws SQLException {
        return to.isPoolable();
    }

    public void setPoolable(boolean poolable) throws SQLException {
        to.setPoolable(poolable);
    }

    public void closeOnCompletion() throws SQLException {
        to.closeOnCompletion();
    }

    public boolean isCloseOnCompletion() throws SQLException {
        return to.isCloseOnCompletion();
    }

    
    // ------------------------------------------------------------------------
    
    public final ResultSet getResultSet() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("getResultSet").push();
        try {
            ResultSet tmpres = to.getResultSet();
            SefResultSetProxy res = new SefResultSetProxy(this, tmpres);
            return LocalCallStack.pushPopParentReturn(res);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public final boolean getMoreResults() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("getMoreResults").push();
        try {
            boolean res = to.getMoreResults();
            String resEltName = res? "returnTrue" : "returnFalse";
            return LocalCallStack.pushPopParentReturn(resEltName, res);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    
    public final ResultSet executeQuery(String sql) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("executeQuery")
                .withParam("sql",  sql)
                .push();
        try {
            ResultSet tmpres = to.executeQuery(sql);
            SefResultSetProxy res = new SefResultSetProxy(this, tmpres);
            return LocalCallStack.pushPopParentReturn(res);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public final boolean execute(String sql) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("execute")
                .withParam("sql",  sql)
                .push();
        try {
            boolean res = to.execute(sql);
            return LocalCallStack.pushPopParentReturn(res);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public final boolean execute(String sql, int[] columnIndexes) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("execute(String,int[])")
                .withParam("sql",  sql)
                .withParam("columnIndexes", columnIndexes)
                .push();
        try {
            boolean res = to.execute(sql, columnIndexes);
            return LocalCallStack.pushPopParentReturn(res);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public final boolean execute(String sql, String[] columnNames) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("execute(String,String[])")
                .withParam("sql",  sql)
                .withParam("columnNames", columnNames)
                .push();
        try {
            boolean res = to.execute(sql, columnNames);
            return LocalCallStack.pushPopParentReturn(res);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    
    public final boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        String meth = "execute(String," + StatementUtils.autoGeneratedKeysToString(autoGeneratedKeys);
        StackPopper toPop = LocalCallStack.meth(meth)
                .withParam("sql",  sql)
                .withParam("autoGeneratedKeys", autoGeneratedKeys)
                .push();
        try {
            boolean res = to.execute(sql, autoGeneratedKeys);
            return LocalCallStack.pushPopParentReturn(res);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int executeUpdate(String sql) throws SQLException {
        int res;
        pre("executeUpdate", sql);
        try {
            res = to.executeUpdate(sql);
            owner.incrCountExecuteUpdate(sql);
            postRes(new Integer(res));
        } catch (SQLException ex) {
            if (owner.getConnectionFactoryConfig().isCurrentInterceptExceptionDuplicateKey()
                            && ex.getMessage().equals("ORA-0001")) {
                owner.getLogger().log("INTERCEPTED ORA-0001 ... silently catched, return 1");
                postRes(new Integer(1));
                return 1; // replace by return, ignore, no rethrow!!
            }
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        String meth = "executeUpdate(String," + StatementUtils.autoGeneratedKeysToString(autoGeneratedKeys);
        StackPopper toPop = LocalCallStack.meth(meth)
                .withParam("sql",  sql)
                .withParam("autoGeneratedKeys", autoGeneratedKeys)
                .push();
        try {
            int res = to.executeUpdate(sql, autoGeneratedKeys);
            return LocalCallStack.pushPopParentReturn(res);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("executeUpdate(String,int[])")
                .withParam("sql",  sql)
                .withParam("columnIndexes", columnIndexes)
                .push();
        try {
            int res = to.executeUpdate(sql, columnIndexes);
            return LocalCallStack.pushPopParentReturn(res);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int executeUpdate(String sql, String[] columnNames) throws SQLException {
        StackPopper toPop = LocalCallStack.meth("executeUpdate(String,String[])")
                .withParam("sql",  sql)
                .withParam("columnNames", columnNames)
                .push();
        try {
            int res = to.executeUpdate(sql, columnNames);
            return LocalCallStack.pushPopParentReturn(res);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int[] executeBatch() throws SQLException {
        StackPopper toPop = LocalCallStack.meth("executeBatch")
                .push();
        try {
            int[] res = to.executeBatch();
            return LocalCallStack.pushPopParentReturn(res);
        } catch (SQLException ex) {
            throw LocalCallStack.pushPopParentException(ex);
        } finally {
            toPop.close();
        }
    }


    public final int getMaxFieldSize() throws SQLException {
        int res;
        preIgnoreMsg("getMaxFieldSize", "");
        try {
            res = to.getMaxFieldSize();
            postIgnoreRes(new Integer(res));
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final void setMaxFieldSize(int max) throws SQLException {
        preIgnoreMsg("setMaxFieldSize", "max=" + max);
        try {
            to.setMaxFieldSize(max);
            postIgnoreVoid();
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
    }

    public final int getMaxRows() throws SQLException {
        int res;
        preIgnoreMsg("getMaxRows", "");
        try {
            res = to.getMaxRows();
            postIgnoreRes(new Integer(res));
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final void setMaxRows(int max) throws SQLException {
        preIgnoreMsg("setMaxRows", "max=" + max);
        try {
            to.setMaxRows(max);
            postIgnoreVoid();
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
    }

    public final void setEscapeProcessing(boolean enable) throws SQLException {
        preIgnoreMsg("setEscapeProcessing", "enable=" + enable);
        try {
            to.setEscapeProcessing(enable);
            postIgnoreVoid();
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
    }

    public final int getQueryTimeout() throws SQLException {
        int res;
        preIgnoreMsg("getQueryTimeout", "");
        try {
            res = to.getQueryTimeout();
            postIgnoreRes(new Integer(res));
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final void setQueryTimeout(int seconds) throws SQLException {
        preIgnoreMsg("setQueryTimeout", "sec=" + seconds);
        try {
            to.setQueryTimeout(seconds);
            postIgnoreVoid();
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
    }

    public final void cancel() throws SQLException {
        pre("cancel", "");
        try {
            to.cancel();
            postVoid();
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
    }

    public final SQLWarning getWarnings() throws SQLException {
        SQLWarning res;
        preIgnoreMsg("getWarnings", "");
        try {
            res = to.getWarnings();
            postIgnoreRes(res);
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final void clearWarnings() throws SQLException {
        preIgnoreMsg("clearWarnings", "");
        try {
            to.clearWarnings();
            postIgnoreVoid();
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
    }

    public final void setCursorName(String name) throws SQLException {
        pre("setCursorName", "name=" + name);
        try {
            to.setCursorName(name);
            postVoid();
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
    }

    public final int getUpdateCount() throws SQLException {
        int res = to.getUpdateCount();
        // No log here ??
        //    pre("getUpdateCount", "");
        //    try {
        //      postRes(new Integer(res));
        //    } catch (SQLException ex) { postEx(ex); throw ex; }
        return res;
    }

    public final void addBatch(String sql) throws SQLException {
        pre("addBatch", sql);
        try {
            to.addBatch(sql);
            postVoid();
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
    }

    public final void clearBatch() throws SQLException {
        preIgnoreMsg("clearBatch", "");
        try {
            to.clearBatch();
            postIgnoreVoid();
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
    }

    public final void setFetchDirection(int direction) throws SQLException {
        preIgnoreMsg("setFetchDirection", "direction=" + direction);
        try {
            to.setFetchDirection(direction);
            postIgnoreVoid();
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
    }

    public final int getFetchDirection() throws SQLException {
        int res;
        preIgnoreMsg("getFetchDirection", "");
        try {
            res = to.getFetchDirection();
            postIgnoreRes(new Integer(res));
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final void setFetchSize(int rows) throws SQLException {
        preIgnoreMsg("setFetchSize", "rows=" + rows);
        try {
            to.setFetchSize(rows);
            postIgnoreVoid();
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
    }

    public final int getFetchSize() throws SQLException {
        int res;
        preIgnoreMsg("getFetchSize", "");
        try {
            res = to.getFetchSize();
            postIgnoreRes(new Integer(res));
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final int getResultSetConcurrency() throws SQLException {
        int res;
        pre("getResultSetConcurrency", "");
        try {
            res = to.getResultSetConcurrency();
            postRes(new Integer(res));
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final int getResultSetType() throws SQLException {
        int res;
        pre("getResultSetType", "");
        try {
            res = to.getResultSetType();
            postRes(new Integer(res));
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final ResultSet getGeneratedKeys() throws SQLException {
        ResultSet res;
        pre("getGeneratedKeys", "");
        try {
            res = to.getGeneratedKeys();
            postRes(res);
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final boolean getMoreResults(int current) throws SQLException {
        boolean res;
        pre("getMoreResults", "");
        try {
            res = to.getMoreResults(current);
            postRes(Boolean.valueOf(res));
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final int getResultSetHoldability() throws SQLException {
        int res;
        pre("getResultSetHoldability", "");
        try {
            res = to.getResultSetHoldability();
            postRes(new Integer(res));
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    
    
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return to.isWrapperFor(iface);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return to.unwrap(iface);
    }

    
}
