package org.sef4j.core.helpers.export.senders;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.helpers.export.ExportFragmentList;
import org.sef4j.core.helpers.senders.DelegateEventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * retryable support for EventSender<FragmentList><br/>
 * when sending event fails => keep in memory map/list of data still to be sent.<br/>
 * (map for fragments with associated id-value, and list for fragments without id)
 * 
 * <PRE>
 *                              +--------------------------------+     
 *                              | transformer: fragments->event  |
 *                              | retryList                      |
 * sendEvent(FragmentList) ---> |      /\                --+-->  | --->sendEvents(FragmentList)
 *                              | merge per id or append   |     |
 *                              |      |                   |     |
 *                              |      +--------onError----+     |
 *                              +--------------------------------+
 * </PRE>
 * 
 * @param <T> type of fragments to export (example: String for JSon fragments)
 */
public class RetryableFragmentListDelegateEventSender<T> extends DelegateEventSender<ExportFragmentList<T>> {

	private static final Logger LOG = LoggerFactory.getLogger(RetryableFragmentListDelegateEventSender.class);

	private RetryableFragmentsHistory<T> retryableHistory;
    
    // ------------------------------------------------------------------------

    public RetryableFragmentListDelegateEventSender(EventSender<ExportFragmentList<T>> delegate, 
    		int retryHistoryLen) {
    	super(delegate);
    	this.retryableHistory = new RetryableFragmentsHistory<T>(retryHistoryLen);
    }

    // ------------------------------------------------------------------------

    @Override
    public void sendEvent(ExportFragmentList<T> currFragments) {
    	ExportFragmentList<T> retryFragments = retryableHistory.shiftAndCollectRetryFragmentsToExport();
    	
    	ExportFragmentList<T> mergedFragments = new ExportFragmentList<T>();  
    	mergedFragments.addAll(retryFragments); // may overwrite retryFragment obj? 
    	mergedFragments.addAll(currFragments);
    	
    	if (mergedFragments.isEmpty()) {
    		return; // nothing to export!
    	}

    	ExportFragmentList<T> failedSendFragments = new ExportFragmentList<T>();

    	// *** do send with capture for failed send fragments ***
		try {
    		super.sendEvent(mergedFragments);
    	} catch(Exception ex) {
    		LOG.warn("Failed to export fragments! ex:" + ex.getMessage() + " .. put in retry list");
    		failedSendFragments.addAll(mergedFragments);
    	}

    	if (! failedSendFragments.isEmpty()) {
    		retryableHistory.addShiftedFailedRetryable(failedSendFragments);
    	}
    }

}
