package org.sef4j.core.helpers.export.senders;

import java.util.List;
import java.util.function.Function;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.helpers.export.ExportFragmentList;
import org.sef4j.core.helpers.export.ExportFragmentsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * sub-class of EventSenderFragmentsExporter for retryable support
 * 
 * <PRE>
 * 
 *                              +--------------------------------+     
 *                              |    retryList         -----------------> targetEventSender
 * FragmentProvider(s) <(*)---- |      /\                        | 
 *      /\         ----------------->  merge per id or append    |        | when error
 *       |                      +--------------------------------+        |
 *       |                                     \                         /
 *   +---+---+                                  <----------------------
 *   |   |   |
 *   Provider1 
 *    (example: Json PerfStats per CallTreeNode 
 *      if modified since > 5mn)
 *   
 *     Provider2 (example: Json PendingCount 
 *      if modified since > 1mn )
 *     
 *       Provider3 (..)
 * </PRE>
 * 
 * @param <T> type of fragments to export (example: String for JSon fragments)
 */
public class RetryableEventSenderFragmentsExporter<T,E> extends EventSenderFragmentsExporter<T,E> {

	private static final Logger LOG = LoggerFactory.getLogger(RetryableEventSenderFragmentsExporter.class);

	private RetryableFragmentsHistory<T> retryableHistory;
    
    // ------------------------------------------------------------------------

    public RetryableEventSenderFragmentsExporter(String displayName, 
    		List<ExportFragmentsProvider<T>> fragmentProviders,
    		Function<ExportFragmentList<T>,List<E>> fragmentsToEventsConverter,
    		EventSender<E> exportSender,
    		int retryHistoryLen) {
        super(displayName, fragmentProviders, fragmentsToEventsConverter, exportSender);
        this.retryableHistory = new RetryableFragmentsHistory<T>(retryHistoryLen);
    }

    // ------------------------------------------------------------------------
    
    public ExportFragmentList<T> shiftAndCollectRetryFragmentsToExport() {
    	ExportFragmentList<T> res = retryableHistory.shiftAndCollectRetryFragmentsToExport();
    	return res;
    }

    @Override
    public void sendEventsForCollectedFragments() {
    	ExportFragmentList<T> currFragments = collectFragmentsToExport();
    	ExportFragmentList<T> retryFragments = shiftAndCollectRetryFragmentsToExport();
    	
    	ExportFragmentList<T> mergedFragments = new ExportFragmentList<T>();  
    	mergedFragments.addAll(retryFragments); // may overwrite retryFragment obj? 
    	mergedFragments.addAll(currFragments);
    	
    	if (mergedFragments.isEmpty()) {
    		return; // nothing to export!
    	}

    	ExportFragmentList<T> failedSendFragments = new ExportFragmentList<T>();

    	// *** do send with capture for failed send fragments ***
    	exportFragmentsWithCaptureFailed(mergedFragments, failedSendFragments);

    	if (! failedSendFragments.isEmpty()) {
    		retryableHistory.addShiftedFailedRetryable(failedSendFragments);
    	}
    }
    
    protected void exportFragmentsWithCaptureFailed(
    		ExportFragmentList<T> fragments, 
    		ExportFragmentList<T> failedFragments) {
    	List<E> events = fragmentsToEventsConverter.apply(fragments);
		try {
    		exportSender.sendEvents(events);
    	} catch(Exception ex) {
    		LOG.warn("Failed to export fragments! ex:" + ex.getMessage() + " .. put in retry list");
    		failedFragments.addAll(fragments);
    	}
	}

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "RetryableFragmentsProvidersExporter [" + displayName + "]";
    }
        
}
