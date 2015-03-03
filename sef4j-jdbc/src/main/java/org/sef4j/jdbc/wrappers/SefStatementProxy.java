package org.sef4j.jdbc.wrappers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * Proxy for java.sql.Statement + wrapp all calls with push()/pop()
 *
 */
public class SefStatementProxy implements Statement {

    /** underlying of proxy */
    protected Statement to;


    /** parent/owner connection */
    protected SefConnectionProxy ownerSefWrappedConnection;

    /* may be set by ctor or method.. (redondant with to.getXXX ) */
    private int localResultSetType;

    private int localResultSetConcurrency;

    // ------------------------------------------------------------------------

    /** Ctor */
    public SefStatementProxy(SefConnectionProxy owner, Statement to) {
        this.to = to;
        this.ownerSefWrappedConnection = owner;
        callInfo.setLogMsgPrefix("[" + owner.getSpid() + "] ");
        callInfoLogger = new CallInfoLoggerHelper(callInfo, owner.getLogger());
        owner.onOpenStatement(this);
    }

    /** Ctor */
    public SefStatementProxy(SefConnectionProxy owner, Statement to, int resultSetType, int resultSetConcurrency) {
        this.to = to;
        this.ownerSefWrappedConnection = owner;
        this.localResultSetType = resultSetType;
        this.localResultSetConcurrency = resultSetConcurrency;
        callInfo.setLogMsgPrefix("[" + owner.getSpid() + "] ");
        callInfoLogger = new CallInfoLoggerHelper(callInfo, owner.getLogger());
        owner.onOpenStatement(this);
    }

    // ------------------------------------------------------------------------

    public Statement getUnderlying() {
        return to;
    }

    public final SefConnectionProxy getSefWrappedConnection() {
        return ownerSefWrappedConnection;
    }

    public final int getLocalResultSetConcurrency() {
        return localResultSetConcurrency;
    }
    
    public final int getLocalResultSetType() {
        return localResultSetType;
    }
    
    // ------------------------------------------------------------------------
    
    /** internal : fill + call logger logStatementPre */
    protected void pre(String meth, String arg) {
        callInfoLogger.pre(meth, arg);
    }

    /** internal : fill + call logger logStatementPost */
    protected void postVoid() {
        callInfoLogger.postVoid();
    }

    protected void postRes(Object res) {
        callInfoLogger.postRes(res);
    }

    protected void postDefaultRes(Object res) {
        callInfoLogger.postDefaultRes(res);
    }

    protected void postDefaultRes() {
        callInfoLogger.postDefaultRes();
    }

    /** internal : fill + call logger logStatementEx  */
    protected void postEx(SQLException ex) throws SQLException {
        callInfoLogger.postEx(ex);
    }

    /** internal : fill + call logger logStatementPre */
    protected void preIgnoreMsg(String meth, String arg) {
        //        callInfoLogger.preIgnore(meth, arg);
    }

    protected void postIgnoreVoid() {
        callInfoLogger.postIgnoreVoid();
    }

    protected void postIgnoreRes(Object res) {
        callInfoLogger.postIgnoreRes(res);
    }

    /**
     * NOT USED YET
     */
    protected ResultSet wrapResultSet(ResultSet res) {
        return res;
    }

    
    
    // Implements java.sql.Statement
    // ------------------------------------------------------------------------
    

    /** see also getOwnerSefWrappedConnection() */
    public final Connection getConnection() throws SQLException {
        return ownerSefWrappedConnection;
    }

    public final void close() throws SQLException {
        to.close();
        ownerSefWrappedConnection.onCloseStatement(this);
        // **** TODO ****
        // Log here results for output Params / count ResultSet rows ... (after read!)

    }

    public final ResultSet executeQuery(String sql) throws SQLException {
        ResultSet res;
        pre("executeQuery", sql);
        try {
            res = to.executeQuery(sql);
            if (res != null)
                postDefaultRes(res);
            else
                postRes("NULL");
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return wrapResultSet(res);
    }

    public final boolean execute(String sql) throws SQLException {
        boolean res;
        pre("execute", sql);
        try {
            res = to.execute(sql);
            ownerSefWrappedConnection.incrCountExecute(sql);
            if (res)
                postDefaultRes();
            else
                postRes("false");
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final boolean execute(String sql, int[] columnIndexes) throws SQLException {
        boolean res;
        pre("execute", sql);
        try {
            res = to.execute(sql, columnIndexes);
            ownerSefWrappedConnection.incrCountExecute(sql);
            postRes(Boolean.valueOf(res));
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final boolean execute(String sql, String[] columnNames) throws SQLException {
        boolean res;
        pre("execute", sql);
        try {
            res = to.execute(sql, columnNames);
            ownerSefWrappedConnection.incrCountExecute(sql);
            postRes(Boolean.valueOf(res));
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        boolean res;
        pre("execute", sql);
        try {
            res = to.execute(sql, autoGeneratedKeys);
            ownerSefWrappedConnection.incrCountExecute(sql);
            postRes(Boolean.valueOf(res));
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final int executeUpdate(String sql) throws SQLException {
        int res;
        pre("executeUpdate", sql);
        try {
            res = to.executeUpdate(sql);
            ownerSefWrappedConnection.incrCountExecuteUpdate(sql);
            postRes(new Integer(res));
        } catch (SQLException ex) {
            if (ownerSefWrappedConnection.getConnectionFactoryConfig().isCurrentInterceptExceptionDuplicateKey()
                            && ex.getMessage().equals("ORA-0001")) {
                ownerSefWrappedConnection.getLogger().log("INTERCEPTED ORA-0001 ... silently catched, return 1");
                postRes(new Integer(1));
                return 1; // replace by return, ignore, no rethrow!!
            }
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        int res;
        pre("execute", sql);
        try {
            res = to.executeUpdate(sql, autoGeneratedKeys);
            ownerSefWrappedConnection.incrCountExecuteUpdate(sql);
            postRes(new Integer(res));
        } catch (SQLException ex) {
            if (ownerSefWrappedConnection.getConnectionFactoryConfig().isCurrentInterceptExceptionDuplicateKey()
                            && ex.getMessage().equals("ORA-0001")) {
                ownerSefWrappedConnection.getLogger().log("INTERCEPTED ORA-0001 ... silently catched, return 1");
                postRes(new Integer(1));
                return 1; // replace by return, ignore, no rethrow!!
            }
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        int res;
        pre("execute", sql);
        try {
            res = to.executeUpdate(sql, columnIndexes);
            ownerSefWrappedConnection.incrCountExecuteUpdate(sql);
            postRes(new Integer(res));
        } catch (SQLException ex) {
            if (ownerSefWrappedConnection.getConnectionFactoryConfig().isCurrentInterceptExceptionDuplicateKey()
                            && ex.getMessage().equals("ORA-0001")) {
                ownerSefWrappedConnection.getLogger().log("INTERCEPTED ORA-0001 ... silently catched, return 1");
                postRes(new Integer(1));
                return 1; // replace by return, ignore, no rethrow!!
            }
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final int executeUpdate(String sql, String[] columnNames) throws SQLException {
        int res;
        pre("execute", sql);
        try {
            res = to.executeUpdate(sql, columnNames);
            ownerSefWrappedConnection.incrCountExecuteUpdate(sql);
            postRes(new Integer(res));
        } catch (SQLException ex) {
            postEx(ex);
            throw ex;
        }
        return res;
    }

    public final int[] executeBatch() throws SQLException {
        int res[];
        pre("executeBatch", "");
        try {
            res = to.executeBatch();
            ownerSefWrappedConnection.incrCountExecuteBatch(null);
            postRes(res);
        } catch (SQLException ex) {
            if (ownerSefWrappedConnection.getConnectionFactoryConfig().isCurrentInterceptExceptionDuplicateKey()
                            && ex.getMessage().equals("ORA-0001")) {
                ownerSefWrappedConnection.getLogger().log("INTERCEPTED ORA-0001 ... silently catched, return new int[0]");
                postRes(new Integer(1));
                return new int[0]; // replace by return, ignore, no rethrow!!
            }
            postEx(ex);
            throw ex;
        }
        return res;
    }

    // TODO???  wrap ResultSet in ResultSetLog for adding rows counter...
    // => usable only after ResultSet.close() / Statement.close() ... !!!
    public final ResultSet getResultSet() throws SQLException {
        // special code to make no log on normal case: return non null ResultSet....
        //    long t1 = System.currentTimeMillis();
        ResultSet res = to.getResultSet();
        //    if (res == null) {
        //      // unnormmal case (=> pre+post)
        //      pre("getResultSet", "");
        //      //? super.setTimePre (t1); // <= restore time as in normal case
        //      postRes("NULL");
        //    } // else normal case! no log...
        return wrapResultSet(res);
    }

    public final boolean getMoreResults() throws SQLException {
        boolean res = to.getMoreResults();
        // No log here ??
        //    pre("getMoreResults", "");
        //    try {
        //      res =
        //      if (res)
        //        postRes("true");
        //      else
        //        postDefaultRes("false");
        //    } catch (SQLException ex) { postEx(ex); throw ex; }
        return res;
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
    
}
