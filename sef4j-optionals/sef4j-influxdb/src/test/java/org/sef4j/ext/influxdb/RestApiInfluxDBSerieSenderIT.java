package org.sef4j.ext.influxdb;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class RestApiInfluxDBSerieSenderIT extends AbstractInfluxDBSerieSenderIT {

	private static InfluxDB influxDB;
    private static RestApiInfluxDBSerieSender sut;
    
    @BeforeClass
    public static void beforeClass() {
		influxDB = InfluxDBFactory.connect(url, username, password);
		sut = new RestApiInfluxDBSerieSender(url, influxDB, dbName);
    }
    
    @Test
    public void testPing() {
        // Prepare
        // Perform
        influxDB.ping();
        // Post-check
    }

    @Test
    public void testQuery() {
        // Prepare
        // Perform
        List<Serie> res = influxDB.query("db1", "select field1, field2 from metric1 limit 10", TimeUnit.SECONDS);
        // Post-check
        Assert.assertEquals(1, res.size());
        Serie serie = res.get(0); 
        Assert.assertEquals("metric1", serie.getName());
        String[] cols = serie.getColumns();
        Assert.assertEquals(4, cols.length);
        Assert.assertEquals("time", cols[0]);
        Assert.assertEquals("sequence_number", cols[1]);
        Assert.assertEquals("field1", cols[2]);
        Assert.assertEquals("field2", cols[3]);
        
        List<Map<String, Object>> rows = serie.getRows();
        Assert.assertTrue(rows.size() <= 10);
        Map<String,Object> pt0 = rows.get(0);
        Assert.assertNotNull(pt0);
        double pt0_time = (Double) pt0.get("time");
        double pt0_field1 = (Double) pt0.get("field1");
        double pt0_field2 = (Double) pt0.get("field2");
        Assert.assertTrue(10 < pt0_field1 && pt0_field1 < 15.0); // cf 11.5 as of test ...
//        Serie field2Serie = metric1Res.get(1);
//        Assert.assertEquals("field1", field1Serie.getName());
    }

}
