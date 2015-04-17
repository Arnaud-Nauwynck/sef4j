package org.sef4j.jdbc.optional.tomcatpool;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.sef4j.jdbc.wrappers.SefDataSourceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomcatPoolDataSourceProxyInstrumenter {

    private static final Logger LOG = LoggerFactory.getLogger(TomcatPoolDataSourceProxyInstrumenter.class);
    
    public static SefDataSourceProxy injectSefDataSourceProxyInto(DataSource ds) {
        if (!(ds instanceof org.apache.tomcat.jdbc.pool.DataSource)) {
            LOG.warn("can not instrument tomcat pool DataSource: bad object type");
            return null;
        }
        org.apache.tomcat.jdbc.pool.DataSource ds2 = (org.apache.tomcat.jdbc.pool.DataSource) ds;
        PoolConfiguration poolConf = ds2.getPoolProperties();
        DataSource delegateDS = (DataSource) poolConf.getDataSource();
        if (delegateDS == null) { // re-try lazy init...
            try {
                ds2.createPool();
//                Connection conn = ds2.getConnection();
//                conn.close(); // <= should destroy from pool!
            } catch (SQLException e) {
            }
            delegateDS = (DataSource) poolConf.getDataSource();
        }
        if (delegateDS == null) {
            LOG.warn("can not instrument tomcat pool DataSource: Driver mode instead of underlying DataSource not supported");
            return null;
        }
        SefDataSourceProxy newDS = new SefDataSourceProxy(delegateDS);

        LOG.info("replacing tomcat Pool underlying DataSource by instrumented sef4j DataSource");
        poolConf.setDataSource(newDS);
        
        return newDS;
    }
    
}
