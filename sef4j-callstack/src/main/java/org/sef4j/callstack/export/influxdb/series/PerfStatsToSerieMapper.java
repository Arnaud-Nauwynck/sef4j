package org.sef4j.callstack.export.influxdb.series;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.influxdb.dto.Serie;
import org.sef4j.callstack.stats.BasicTimeStatsSlotInfo;
import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stats.PerfStats;

/**
 * Mapper for PerfStats -> InfluxDB Serie
 */
public class PerfStatsToSerieMapper {

    private final boolean printPendings;
    private final boolean printElapsed;
    private final boolean printCpu;
    private final boolean printUser;

    private PendingPerfCountToSerieMapper pendingPerfCountMapper;
    private BasicTimeStatsLogHistogramToSerieMapper elapsedTimeStatsToSerieMapper;
    private BasicTimeStatsLogHistogramToSerieMapper userTimeStatsToSerieMapper;
    private BasicTimeStatsLogHistogramToSerieMapper cpuTimeStatsToSerieMapper;

    private final String[] columnNames;

    // ------------------------------------------------------------------------

    public PerfStatsToSerieMapper(String prefix, String suffix,
    		boolean printPendings, boolean printElapsed, boolean printCpu, boolean printUser) {
        this.printPendings = printPendings;
        this.printElapsed = printElapsed;
        this.printCpu = printCpu;
        this.printUser = printUser;
        
        List<String> tmpColNames = new ArrayList<String>();
        if (printPendings) {
        	pendingPerfCountMapper = new PendingPerfCountToSerieMapper(prefix, suffix);
        	tmpColNames.addAll(Arrays.asList(pendingPerfCountMapper.getColumnNames()));
        }
        if (printElapsed) {
        	elapsedTimeStatsToSerieMapper = new BasicTimeStatsLogHistogramToSerieMapper(
        			SerieColNameUtil.prefixed("elapsed", prefix), suffix);
        	tmpColNames.addAll(Arrays.asList(elapsedTimeStatsToSerieMapper.getColumnNames()));
        }
        if (printCpu) {
        	cpuTimeStatsToSerieMapper = new BasicTimeStatsLogHistogramToSerieMapper(
        			SerieColNameUtil.prefixed("cpu", prefix), suffix);
        	tmpColNames.addAll(Arrays.asList(cpuTimeStatsToSerieMapper.getColumnNames()));
        }
        if (printUser) {
        	userTimeStatsToSerieMapper = new BasicTimeStatsLogHistogramToSerieMapper(
        			SerieColNameUtil.prefixed("user", prefix), suffix);
        	tmpColNames.addAll(Arrays.asList(userTimeStatsToSerieMapper.getColumnNames()));
        }
        this.columnNames = tmpColNames.toArray(new String[tmpColNames.size()]);
    }
    
    // ------------------------------------------------------------------------

	public Serie map(PerfStats src, String serieName) {
		 Serie.Builder dest = new Serie.Builder(serieName);
		 dest.columns(columnNames);
		 mapValues(dest, src);
		 return dest.build();
	}
	
	public void mapValues(Serie.Builder dest, PerfStats src) {
		 if (printPendings) {
			 PendingPerfCount pendingCounts = src.getPendingCounts();
			 pendingPerfCountMapper.mapValues(dest, pendingCounts);
		 }
		 final BasicTimeStatsSlotInfo[] timeStatsInfo = src.getElapsedTimeStats().getSlotInfoCopy();
		 final BasicTimeStatsSlotInfo[] cpuStatsInfo = src.getThreadCpuTimeStats().getSlotInfoCopy();
		 final BasicTimeStatsSlotInfo[] userStatsInfo = src.getThreadUserTimeStats().getSlotInfoCopy();
		 if (printElapsed) {
			 elapsedTimeStatsToSerieMapper.mapValues(dest, timeStatsInfo);
		 }
		 if (printCpu) {
			 cpuTimeStatsToSerieMapper.mapValues(dest, cpuStatsInfo);
		 }
		 if (printUser) {
			 userTimeStatsToSerieMapper.mapValues(dest, userStatsInfo);
		 }
	}
	
}
