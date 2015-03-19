package org.sef4j.jdbc.optional.hikari;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hsqldb.jdbc.JDBCConnection;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.sef4j.jdbc.optional.InstrumenterHelper;
import org.sef4j.jdbc.wrappers.SefConnectionProxy;
import org.sef4j.jdbc.wrappers.SefDataSourceProxy;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.PoolBagEntry;
import com.zaxxer.hikari.proxy.ConnectionProxy;


public class HikariDataSourceProxyInstrumenterTest extends InstrumenterHelper {

	private static final String URL_DB1 = "jdbc:hsqldb:file:src/test/hsqldb1/db;create=true;";

	@Test
	public void testInjectSefDataSourceProxyInto_minIddle0() throws Exception {
		// Prepare
		HikariConfig config = new HikariConfig();
		config.setMinimumIdle(0);
		final AtomicBoolean allowConnCreate = new AtomicBoolean();
		JDBCDataSource underlyingDS = new JDBCDataSource() {
            private static final long serialVersionUID = 1L;

            public Connection getConnection() throws SQLException {
				if (!allowConnCreate.get()) {
					throw new IllegalStateException();
				}
				return super.getConnection();
			}
			public Connection getConnection(String user, String password) throws SQLException {
				if (!allowConnCreate.get()) {
					throw new IllegalStateException();
				}
				return super.getConnection(user, password);
			}
		};
		underlyingDS.setUrl(URL_DB1);
		config.setDataSource(underlyingDS);
		HikariDataSource ds = new HikariDataSource(config);
		// Perform
		SefDataSourceProxy newIntermediateSefDS = HikariDataSourceProxyInstrumenter.injectSefDataSourceProxyInto(ds);
//		try {
//			ds.getConnection(); // ==> will retry several time, in async ThreadPool ...
//			Assert.fail();
//		} catch(IllegalStateException ex) {
//			// OK.. .do not allow creation yet
//		}
		allowConnCreate.set(true);
		Connection pooledWrappedConn = ds.getConnection();
		// Post-check
		assertPooledWrappedConnection(newIntermediateSefDS, pooledWrappedConn);
	}
	
	
	@Test
	public void testInjectSefDataSourceProxyInto() throws Exception {
		// Prepare
		HikariConfig config = new HikariConfig();
		JDBCDataSource underlyingDS = new JDBCDataSource();
		underlyingDS.setUrl(URL_DB1);
		config.setDataSource(underlyingDS);
		HikariDataSource ds = new HikariDataSource(config);
		// Perform
		SefDataSourceProxy newIntermediateSefDS = HikariDataSourceProxyInstrumenter.injectSefDataSourceProxyInto(ds);

		Connection pooledWrappedConn = ds.getConnection();
		// conn.prepareStatement("select 1 from ");
		
		// Post-check
		assertPooledWrappedConnection(newIntermediateSefDS, pooledWrappedConn);
	}


	private void assertPooledWrappedConnection(SefDataSourceProxy newIntermediateSefDS, Connection pooledWrappedConn) throws SQLException {
		Assert.assertNotNull(pooledWrappedConn);
		// ?... real class : ConnectionJAvaassistProxy generated at runtime!  
		Assert.assertTrue(pooledWrappedConn instanceof ConnectionProxy);
		ConnectionProxy hikariConnProxy = (ConnectionProxy) pooledWrappedConn;
		PoolBagEntry poolBagEntry = hikariConnProxy.getPoolBagEntry();
		Assert.assertNotNull(poolBagEntry);
		Connection delegateConn = pooledWrappedConn.unwrap(SefConnectionProxy.class);
				// (Connection) InstrumenterHelper.unsafeGetField(conn, ConnectionProxy.class, "delegate");
		
		SefConnectionProxy sefConnProxy = (SefConnectionProxy) delegateConn;
		Assert.assertSame(sefConnProxy.getOwner(), newIntermediateSefDS);
		Assert.assertTrue(sefConnProxy.getUnderlyingConnection() instanceof JDBCConnection);
	}
	
}
