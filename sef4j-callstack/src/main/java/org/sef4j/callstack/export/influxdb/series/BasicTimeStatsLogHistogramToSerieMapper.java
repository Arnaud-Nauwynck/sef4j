package org.sef4j.callstack.export.influxdb.series;

import org.influxdb.dto.Serie;
import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;
import org.sef4j.callstack.stats.BasicTimeStatsSlotInfo;

/**
 * Mapper for BasicTimeStatsLogHistogram -> influxDB Serie
 * 
 */
public class BasicTimeStatsLogHistogramToSerieMapper {

	public static final BasicTimeStatsLogHistogramToSerieMapper INSTANCE = new BasicTimeStatsLogHistogramToSerieMapper("", "");
	
	private static final String[] FIELD_NAMES = new String[] { "count", "sum" };
	
	private final String[] columnNames;

    // ------------------------------------------------------------------------

    public BasicTimeStatsLogHistogramToSerieMapper(String prefix, String suffix) {
    	final int len = BasicTimeStatsLogHistogram.SLOT_LEN;
		this.columnNames = SerieColNameUtil.wrapNamesRange(prefix, FIELD_NAMES, suffix, 0, len);
    }
    
    // ------------------------------------------------------------------------

    
    public String[] getColumnNames() {
    	return columnNames;
    }
    
	public Serie map(BasicTimeStatsLogHistogram src, String serieName) {
		 Serie.Builder dest = new Serie.Builder(serieName);
		 dest.columns(columnNames);
		 mapValues(dest, src);
		 return dest.build();
	}

	public void mapValues(Serie.Builder dest, BasicTimeStatsLogHistogram src) {
        BasicTimeStatsSlotInfo[] timeStatsInfo = src.getSlotInfoCopy();
        mapValues(dest, timeStatsInfo);
    }

	public void mapValues(Serie.Builder dest, BasicTimeStatsSlotInfo[] timeStatsInfo) {
		final int len = BasicTimeStatsLogHistogram.SLOT_LEN;
        Object[] values = new Object[len+len];
        for (int index = 0, i = 0; i < len; i++) {
        	values[index++] = timeStatsInfo[i].getCount();
        	values[index++] = timeStatsInfo[i].getSum();
        }
        dest.values(values);
	}

}
