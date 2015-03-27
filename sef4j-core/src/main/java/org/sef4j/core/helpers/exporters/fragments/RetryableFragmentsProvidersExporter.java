package org.sef4j.core.helpers.exporters.fragments;

import java.util.ArrayList;
import java.util.List;

import org.sef4j.core.api.EventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * sub-class of FragmentsProvidersExporter for retryable support
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
public class RetryableFragmentsProvidersExporter<T> extends FragmentsProvidersExporter<T> {

	private static final Logger LOG = LoggerFactory.getLogger(RetryableFragmentsProvidersExporter.class);

	private RetryableFragmentsHistory<T> retryableHistory;
    
    // ------------------------------------------------------------------------

    public RetryableFragmentsProvidersExporter(String displayName, 
    		List<ExportFragmentsProvider<T>> fragmentProviders, 
    		EventSender<T> exportSender,
    		int retryHistoryLen) {
        super(displayName, fragmentProviders, exportSender);
        this.retryableHistory = new RetryableFragmentsHistory<T>(retryHistoryLen);
    }

    // ------------------------------------------------------------------------
    
    public ExportFragmentList<T> shiftAndCollectRetryFragmentsToExport() {
    	ExportFragmentList<T> res = retryableHistory.shiftAndCollectRetryFragmentsToExport();
    	return res;
    }

    @Override
    public void export() {
    	ExportFragmentList<T> currFragments = collectCurrentFragmentsToExport();
    	ExportFragmentList<T> retryFragments = shiftAndCollectRetryFragmentsToExport();
    	
    	ExportFragmentList<T> mergedFragments = new ExportFragmentList<T>();  
    	mergedFragments.addAll(retryFragments); // may overwrite retryFragment obj? 
    	mergedFragments.addAll(currFragments);
    	
    	if (mergedFragments.isEmpty()) {
    		return; // nothing to export!
    	}
    	List<ExportFragment<T>> fragmentEntries = mergedFragments.copyToPriorityList();

    	List<ExportFragment<T>> failedSendFragmentEntries = new ArrayList<ExportFragment<T>>();
    	try {
	    	// *** do send with capture for failed send fragments ***
	    	exportFragmentsWithCaptureFailed(fragmentEntries, failedSendFragmentEntries);
    	} catch(Exception ex) {
    		// should not occur!
    		failedSendFragmentEntries = fragmentEntries;
    		LOG.warn("Failed exportFragmentsWithCaptureFailed() ex=" + ex.getMessage() + ", consider all failed fragments as captured ... ignore, no rethrow!");
    	}
    	if (! failedSendFragmentEntries.isEmpty()) {
    		retryableHistory.addShiftedFailedRetryable(failedSendFragmentEntries);
    	}
    }
    
    protected void exportFragmentsWithCaptureFailed(List<ExportFragment<T>> fragmentEntries, 
    		List<ExportFragment<T>> failedFragmentEntries) {
    	List<T> fragmentValues = ExportFragment.lsToValues(fragmentEntries);
    	try {
    		exportSender.sendEvents(fragmentValues);
    	} catch(Exception ex) {
    		LOG.warn("Failed to export fragments! ex:" + ex.getMessage() + " .. put in retry list");
    		failedFragmentEntries.addAll(fragmentEntries);
    	}
	}

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "RetryableFragmentsProvidersExporter [" + displayName + "]";
    }
        
}
