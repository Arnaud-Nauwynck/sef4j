package org.sef4j.ext.influxdb.series;

import java.util.Arrays;
import java.util.List;

import org.influxdb.dto.Serie;
import org.sef4j.callstack.stats.BasicTimeStatsLogHistogram;
import org.sef4j.callstack.stats.BasicTimeStatsSlotInfo;

/**
 * Mapper for BasicTimeStatsLogHistogram -> influxDB Serie
 * 
 */
public class BasicTimeStatsLogHistogramToSerieMapper {

	private static final String[] FIELD_NAMES = new String[] { "count", "sum" };

	public static final BasicTimeStatsLogHistogramToSerieMapper INSTANCE = new BasicTimeStatsLogHistogramToSerieMapper("", "");
	
	
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
    
    public void getColumns(List<String> dest) {
        dest.addAll(Arrays.asList(columnNames));
    }

    public void getValues(List<Object> dest, BasicTimeStatsLogHistogram src) {
        BasicTimeStatsSlotInfo[] timeStatsInfo = src.getSlotInfoCopy();
        getValues(dest, timeStatsInfo);
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

	public void getValues(List<Object> dest, BasicTimeStatsSlotInfo[] timeStatsInfo) {
        final int len = BasicTimeStatsLogHistogram.SLOT_LEN;
        for (int i = 0; i < len; i++) {
            dest.add(timeStatsInfo[i].getCount());
            dest.add(timeStatsInfo[i].getSum());
        }
    }
	
}
