package org.sef4j.callstack.export;

import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stattree.changes.BasicStatIgnorePendingChangeCollector;
import org.sef4j.callstack.stattree.changes.PendingCountChangeCollector;
import org.sef4j.callstack.stattree.changes.PendingPerfCountChangesEvent;
import org.sef4j.callstack.stattree.changes.PerfStatsChangesEvent;
import org.sef4j.core.api.proptree.PropTreeNode;
import org.sef4j.core.helpers.AsyncUtils;
import org.sef4j.core.helpers.proptree.changes.AsyncChangeCollectorSender.Builder;

public class CallStatsExportBuilderUtils {


    public static Builder<PerfStats,PerfStatsChangesEvent> newBuilderPerfStatChange(PropTreeNode srcRoot) {
        BasicStatIgnorePendingChangeCollector changeCollector = 
                new BasicStatIgnorePendingChangeCollector(srcRoot);
        return new Builder<PerfStats,PerfStatsChangesEvent>()
                .withScheduledExecutor(AsyncUtils.defaultScheduledThreadPool())
                .withPeriod(5*60)
                .withTaskChangeCollector(changeCollector)
                .withTaskChangesToEventBuilder(PerfStatsChangesEvent.FACTORY);
    }

    public static Builder<PendingPerfCount,PendingPerfCountChangesEvent> newBuilderPendingCountChange(PropTreeNode srcRoot) {
        PendingCountChangeCollector changeCollector = 
                new PendingCountChangeCollector(srcRoot);
        return new Builder<PendingPerfCount,PendingPerfCountChangesEvent>()
                .withScheduledExecutor(AsyncUtils.defaultScheduledThreadPool())
                .withPeriod(30)
                .withTaskChangeCollector(changeCollector)
                .withTaskChangesToEventBuilder(PendingPerfCountChangesEvent.FACTORY);
    }
    
}
