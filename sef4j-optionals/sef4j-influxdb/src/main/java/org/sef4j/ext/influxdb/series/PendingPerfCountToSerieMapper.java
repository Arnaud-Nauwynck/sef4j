package org.sef4j.ext.influxdb.series;

import java.util.Arrays;
import java.util.List;

import org.influxdb.dto.Serie;
import org.sef4j.callstack.stats.PendingPerfCount;

/**
 * Mapper for PendingPerfCount -> InfluxDB Serie
 */
public class PendingPerfCountToSerieMapper {

	private static final String[] FIELD_NAMES = new String[] { "pendingCount", "pendingSumStartTime" };

	public static final PendingPerfCountToSerieMapper INSTANCE = new PendingPerfCountToSerieMapper("", "");

	
	private final String[] columnNames;

    // ------------------------------------------------------------------------

    public PendingPerfCountToSerieMapper(String prefix, String suffix) {
    	this.columnNames = SerieColNameUtil.wrapNames(prefix, FIELD_NAMES, suffix);
    }
    
    // ------------------------------------------------------------------------

    public String[] getColumnNames() {
    	return columnNames;
    }
    
    public void getColumns(List<String> dest) {
        dest.addAll(Arrays.asList(columnNames));
    }

    public void getValues(List<Object> dest, PendingPerfCount src) {
        int pendingCount = src.getPendingCount();
        long pendingSumStartTime = src.getPendingSumStartTime();
        dest.add(pendingCount);
        dest.add(pendingSumStartTime);
    }
    
    
	public Serie map(PendingPerfCount src, String serieName) {
		 Serie.Builder dest = new Serie.Builder(serieName);
		 dest.columns(columnNames);
		 mapValues(dest, src);
		 return dest.build();
	}
	
	public void mapValues(Serie.Builder dest, PendingPerfCount src) {
        int pendingCount = src.getPendingCount();
        long pendingSumStartTime = src.getPendingSumStartTime();
		dest.values(pendingCount, pendingSumStartTime);
	}
	
}
