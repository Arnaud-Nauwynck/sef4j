package org.sef4j.jdbc.wrappers;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Wrapper;
import java.util.logging.Logger;

import javax.sql.CommonDataSource;

public class SefCommonDataSourceProxy implements CommonDataSource, Wrapper {

    private CommonDataSource to;
    
    // ------------------------------------------------------------------------

    public SefCommonDataSourceProxy(CommonDataSource to) {
        this.to = to;
    }

    // implements java.sql.CommonDataSource
    // ------------------------------------------------------------------------
    
    
    public PrintWriter getLogWriter() throws SQLException {
        return to.getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        to.setLogWriter(out);
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        to.setLoginTimeout(seconds);
    }

    public int getLoginTimeout() throws SQLException {
        return to.getLoginTimeout();
    }

    // JDBC 4.1 
    // @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return to.getParentLogger();
    }

    
    // implements java.sql.Wrapper
    // ------------------------------------------------------------------------
    
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return ((Wrapper) to).unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return ((Wrapper) to).isWrapperFor(iface);
    }

}
