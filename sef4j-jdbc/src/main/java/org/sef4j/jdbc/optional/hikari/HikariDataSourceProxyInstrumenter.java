package org.sef4j.jdbc.optional.hikari;

import java.sql.Connection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.sef4j.jdbc.optional.InstrumenterHelper;
import org.sef4j.jdbc.wrappers.SefConnectionProxy;
import org.sef4j.jdbc.wrappers.SefDataSourceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import com.zaxxer.hikari.pool.PoolBagEntry;
import com.zaxxer.hikari.util.ConcurrentBag;
import com.zaxxer.hikari.util.ConcurrentBag.BagEntry;

public class HikariDataSourceProxyInstrumenter extends InstrumenterHelper {

	private static final Logger LOG = LoggerFactory.getLogger(HikariDataSourceProxyInstrumenter.class);
    
	public static SefDataSourceProxy injectSefDataSourceProxyInto(HikariDataSource ds) {
		// before:   HikariDS -> .datasource=UnderlyngDS
		// after :   HikariDS -> .datasource=SefDataSourceProxy -> .to=UnderlyngDS
		DataSource delegateDataSource = ds.getDataSource();
		SefDataSourceProxy newDS = new SefDataSourceProxy(delegateDataSource);
		ds.setDataSource(newDS);
		
		// problem: ... should also rewrap Pool and all already opened Pooled Connection
		// HikariDataSource (final).pool .... contains final reference to UnderlyingDS !!!
		// => must wrap underlying DataSource directly ...
		// or use reflect (or UNSAFE) to force change final field ?!! 
		HikariPool pool = (HikariPool) unsafeGetField(ds, "pool");
		unsafeSetField(pool, "dataSource", newDS);
		
		int totalConnections = pool.getTotalConnections();
		int activeConnections = pool.getActiveConnections();
		if (totalConnections != 0) {
			LOG.info("injectSefDataSourceProxyInto()... pool already contains " + activeConnections + " active /" + totalConnections + " total connections! ... also injecting");
			
			ConcurrentBag<PoolBagEntry> connectionBag = unsafeGetField(pool, "connectionBag");
			// borrow all ?... instrument them,  and requite them
			CopyOnWriteArrayList<PoolBagEntry> sharedList = unsafeGetField(connectionBag, "sharedList");
			for(int i = 0; i < sharedList.size(); i++) {
				PoolBagEntry bagEntry = sharedList.get(i);
				if (bagEntry != null) {
					AtomicInteger bagEntryState = (AtomicInteger) unsafeGetField(bagEntry, BagEntry.class, "state");
					if (bagEntryState.compareAndSet(ConcurrentBag.STATE_NOT_IN_USE, ConcurrentBag.STATE_IN_USE)) {
	
						injectSefConnectionProxy(newDS, bagEntry);
						
						bagEntryState.compareAndSet(ConcurrentBag.STATE_IN_USE, ConcurrentBag.STATE_NOT_IN_USE);
					} else {
						LOG.error("Connection already in used?! ... should call injectSefDataSourceProxyInto(); only at startup");
						// do anyway?! ... may forece error, like  bagEntry.connection.close();
						injectSefConnectionProxy(newDS, bagEntry);
					}
				}
			}
		}
		return newDS;
	}
	
	private static void injectSefConnectionProxy(SefDataSourceProxy newDS, PoolBagEntry bagEntry) {
		Connection prevConnection = bagEntry.connection;
		Connection newConnection = new SefConnectionProxy(newDS, prevConnection);
		unsafeSetField(bagEntry, "connection", newConnection);
	}
	

}
