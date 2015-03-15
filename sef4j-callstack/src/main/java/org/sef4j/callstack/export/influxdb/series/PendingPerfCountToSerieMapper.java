package org.sef4j.callstack.export.influxdb.series;

import org.influxdb.dto.Serie;
import org.sef4j.callstack.stats.PendingPerfCount;

/**
 * Mapper for PendingPerfCount -> InfluxDB Serie
 */
public class PendingPerfCountToSerieMapper {

	public static final PendingPerfCountToSerieMapper INSTANCE = new PendingPerfCountToSerieMapper("", "");

	private static final String[] FIELD_NAMES = new String[] { "pendingCount", "pendingSumStartTime" };
	
	private final String[] columnNames;

    // ------------------------------------------------------------------------

    public PendingPerfCountToSerieMapper(String prefix, String suffix) {
    	this.columnNames = SerieColNameUtil.wrapNames(prefix, FIELD_NAMES, suffix);
    }
    
    // ------------------------------------------------------------------------

    public String[] getColumnNames() {
    	return columnNames;
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
