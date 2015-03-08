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
public class SefStatementProxy implements Statement { // .. implements Wrapper, AutoCloseable

	private static final String CNAME = SefStatementProxy.class.getName();
	
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
        StackPopper toPop = LocalCallStack.meth(CNAME, "close").push();
        try {
            to.close();
        } catch(SQLException ex) {
            throw toPop.returnException(ex);
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
        StackPopper toPop = LocalCallStack.meth(CNAME, "isPoolable").push();
        try {
        	boolean res = to.isPoolable();
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public void setPoolable(boolean poolable) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "setPoolable")
        		.withParam("poolable", poolable)
        		.push();
        try {
        	to.setPoolable(poolable);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public void closeOnCompletion() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "closeOnCompletion").push();
        try {
        	to.closeOnCompletion();
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public boolean isCloseOnCompletion() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "isCloseOnCompletion").push();
        try {
        	boolean res = to.isCloseOnCompletion();
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    
    // ------------------------------------------------------------------------
    
    public final ResultSet getResultSet() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getResultSet").push();
        try {
            ResultSet tmpres = to.getResultSet();
            SefResultSetProxy res = new SefResultSetProxy(this, tmpres);
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final boolean getMoreResults() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getMoreResults").push();
        try {
            boolean res = to.getMoreResults();
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    
    public final ResultSet executeQuery(String sql) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "executeQuery")
                .withParam("sql",  sql)
                .push();
        try {
            ResultSet tmpres = to.executeQuery(sql);
            SefResultSetProxy res = new SefResultSetProxy(this, tmpres);
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final boolean execute(String sql) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "execute")
                .withParam("sql",  sql)
                .push();
        try {
            boolean res = to.execute(sql);
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int getUpdateCount() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getUpdateCount").push();
        try {
        	int res = to.getUpdateCount();
        	return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final boolean execute(String sql, int[] columnIndexes) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "execute(String,int[])")
                .withParam("sql",  sql)
                .withParam("columnIndexes", columnIndexes)
                .push();
        try {
            boolean res = to.execute(sql, columnIndexes);
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final boolean execute(String sql, String[] columnNames) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "execute(String,String[])")
                .withParam("sql",  sql)
                .withParam("columnNames", columnNames)
                .push();
        try {
            boolean res = to.execute(sql, columnNames);
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    
    public final boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        String meth = "execute(String," + StatementUtils.autoGeneratedKeysToString(autoGeneratedKeys) +")";
        StackPopper toPop = LocalCallStack.meth(CNAME, meth)
                .withParam("sql",  sql)
                .withParam("autoGeneratedKeys", autoGeneratedKeys)
                .push();
        try {
            boolean res = to.execute(sql, autoGeneratedKeys);
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int executeUpdate(String sql) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "executeUpdate")
                .withParam("sql",  sql)
                .push();
        try {
            int res = to.executeUpdate(sql);
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        String meth = "executeUpdate(String," + StatementUtils.autoGeneratedKeysToString(autoGeneratedKeys);
        StackPopper toPop = LocalCallStack.meth(CNAME, meth)
                .withParam("sql",  sql)
                .withParam("autoGeneratedKeys", autoGeneratedKeys)
                .push();
        try {
            int res = to.executeUpdate(sql, autoGeneratedKeys);
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "executeUpdate(String,int[])")
                .withParam("sql",  sql)
                .withParam("columnIndexes", columnIndexes)
                .push();
        try {
            int res = to.executeUpdate(sql, columnIndexes);
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int executeUpdate(String sql, String[] columnNames) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "executeUpdate(String,String[])")
                .withParam("sql",  sql)
                .withParam("columnNames", columnNames)
                .push();
        try {
            int res = to.executeUpdate(sql, columnNames);
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }
    
    public final void addBatch(String sql) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "addBatch")
                .withParam("sql",  sql)
                .push();
        try {
            to.addBatch(sql);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final void clearBatch() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "clearBatch").push();
        try {
            to.clearBatch();
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int[] executeBatch() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "executeBatch").push();
        try {
            int[] res = to.executeBatch();
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int getMaxFieldSize() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getMaxFieldSize").push();
        try {
            int res = to.getMaxFieldSize();
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final void setMaxFieldSize(int max) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "setMaxFieldSize")
        		.withParam("max", max)
        		.push();
        try {
            to.setMaxFieldSize(max);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int getMaxRows() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getMaxRows").push();
        try {
            int res = to.getMaxRows();
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final void setMaxRows(int max) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "setMaxRows")
        		.withParam("max", max)
        		.push();
        try {
            to.setMaxRows(max);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final void setEscapeProcessing(boolean enable) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "setEscapeProcessing")
        		.withParam("enable", enable)
        		.push();
        try {
            to.setEscapeProcessing(enable);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int getQueryTimeout() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getQueryTimeout").push();
        try {
            int res = to.getQueryTimeout();
            return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final void setQueryTimeout(int seconds) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "setQueryTimeout")
        		.withParam("seconds", seconds)
        		.push();
        try {
            to.setQueryTimeout(seconds);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final void cancel() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "cancel").push();
        try {
            to.cancel();
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final SQLWarning getWarnings() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getWarnings").push();
        try {
        	SQLWarning  tmpres = to.getWarnings();
        	SQLWarning res = tmpres; // TODO may wrap with SefSQLWarningProxy()
        	return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final void clearWarnings() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "clearWarnings").push();
        try {
            to.clearWarnings();
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final void setCursorName(String name) throws SQLException {
    	StackPopper toPop = LocalCallStack.meth(CNAME, "setCursorName")
    			.withParam("name", name)
    			.push();
    	try {
    		to.setCursorName(name);
    	} catch (SQLException ex) {
    		throw toPop.returnException(ex);
    	} finally {
    		toPop.close();
    	}
    }

    public final void setFetchDirection(int direction) throws SQLException {
    	StackPopper toPop = LocalCallStack.meth(CNAME, "setFetchDirection")
    			.withParam("direction", direction)
    			.push();
    	try {
    		to.setFetchDirection(direction);
    	} catch (SQLException ex) {
    		throw toPop.returnException(ex);
    	} finally {
    		toPop.close();
    	}
    }

    public final int getFetchDirection() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getFetchDirection").push();
        try {
        	int res = to.getFetchDirection();
        	return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final void setFetchSize(int rows) throws SQLException {
    	StackPopper toPop = LocalCallStack.meth(CNAME, "setFetchSize")
    			.withParam("rows", rows)
    			.push();
    	try {
    		to.setFetchSize(rows);
    	} catch (SQLException ex) {
    		throw toPop.returnException(ex);
    	} finally {
    		toPop.close();
    	}
    }

    public final int getFetchSize() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getFetchSize").push();
        try {
        	int res = to.getFetchSize();
        	return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int getResultSetConcurrency() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getResultSetConcurrency").push();
        try {
        	int res = to.getResultSetConcurrency();
        	return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int getResultSetType() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getResultSetType").push();
        try {
        	int res = to.getResultSetType();
        	return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final ResultSet getGeneratedKeys() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getGeneratedKeys").push();
        try {
        	ResultSet tmpres = to.getGeneratedKeys();
        	ResultSet res = new SefResultSetProxy(this, tmpres);
        	return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final boolean getMoreResults(int current) throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getMoreResults")
        		.withParam("current", current)
        		.push();
        try {
        	boolean res = to.getMoreResults(current);
        	return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }

    public final int getResultSetHoldability() throws SQLException {
        StackPopper toPop = LocalCallStack.meth(CNAME, "getResultSetHoldability").push();
        try {
        	int res = to.getResultSetHoldability();
        	return toPop.returnValue(res);
        } catch (SQLException ex) {
            throw toPop.returnException(ex);
        } finally {
            toPop.close();
        }
    }    
    
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return to.isWrapperFor(iface);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return to.unwrap(iface);
    }

    // ------------------------------------------------------------------------

    @Override
	public String toString() {
		return "SefStatementProxy [to=" + to + "]";
	}
    
}
