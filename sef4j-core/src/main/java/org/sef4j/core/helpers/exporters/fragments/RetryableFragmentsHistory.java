package org.sef4j.core.helpers.exporters.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * helper class for retryable fragments support: 
 * - keep lastest value of each identifiable fragments
 * - keep recent history (example: 10 last failed) for non-identifiable fragments
 * 
 * @param <T> type of fragments to export (example: String for JSon fragments)
 */
public class RetryableFragmentsHistory<T> {

	private static final Logger LOG = LoggerFactory.getLogger(RetryableFragmentsHistory.class);

	public static final int DEFAULT_RETRY_HISTORY_LEN = 5;
	
	
	private Map<Object,ExportFragment<T>> retryIdentifiableFragments = new HashMap<Object,ExportFragment<T>>(); 
    
	private int retryHistoryLen = DEFAULT_RETRY_HISTORY_LEN;
    
    /** optim computed field = sum_i retryNoIdFragmentsRecentHistory.get(i).size() */
    private int retryNoIdFragmentsRecentHistoryLenSum = 0;
    
    // recent history of failed fragment... each list may(should) be empty!...
    // optimized storage as a cyclic array (using modulo + current modulo position)
    private int retryRecentHistoryIndex;
    private List<ExportFragment<T>>[] retryNoIdFragmentsRecentHistory;
    
    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
	public RetryableFragmentsHistory(int retryHistoryLen) {
    	this.retryHistoryLen = retryHistoryLen;
    	this.retryNoIdFragmentsRecentHistory = new List[retryHistoryLen];
    }

    // ------------------------------------------------------------------------
    
    public ExportFragmentList<T> shiftAndCollectRetryFragmentsToExport() {
    	ExportFragmentList<T> res = new ExportFragmentList<T>();
    	if (! retryIdentifiableFragments.isEmpty()) {
			res.addAllEntries(retryIdentifiableFragments.values());
		}
		if (retryNoIdFragmentsRecentHistoryLenSum != 0) {
			// collect all 
			for(int i = retryRecentHistoryIndex-1; i != retryRecentHistoryIndex; 
					i = modulo(i + retryHistoryLen - 1)) {
				List<ExportFragment<T>> nthPastRetry = retryNoIdFragmentsRecentHistory[i];
				if (nthPastRetry != null && !nthPastRetry.isEmpty()) {
					res.addAllEntries(nthPastRetry);
				}
			}
			// shift + truncate last (insert null), and update corresponding count
			retryRecentHistoryIndex = modulo(retryRecentHistoryIndex + 1);
			List<ExportFragment<T>> last = retryNoIdFragmentsRecentHistory[retryRecentHistoryIndex];
			if (last != null) {
				retryNoIdFragmentsRecentHistoryLenSum -= last.size();
			}
			retryNoIdFragmentsRecentHistory[retryRecentHistoryIndex] = null;
		}
		return res;
    }

	private int modulo(int i) {
		assert -retryHistoryLen <= i && i <= 2*retryHistoryLen-1;
		// return i % retryHistoryLen;
		if (i >= retryHistoryLen) return i - retryHistoryLen;
		else if (i < 0) return i + retryHistoryLen;
		else return i;
	}

    public ExportFragmentList<T> mergeWithPrevRetryable(ExportFragmentList<T> currFragments) {
    	ExportFragmentList<T> res = shiftAndCollectRetryFragmentsToExport();
    	res.addAll(currFragments);
    	return res;
    }
    
    public void addShiftedFailedRetryable(List<ExportFragment<T>> failedFragments) {
    	List<ExportFragment<T>> ls = retryNoIdFragmentsRecentHistory[retryRecentHistoryIndex];
    	if (ls == null) {
    		ls = new ArrayList<ExportFragment<T>>();
    		retryNoIdFragmentsRecentHistory[retryRecentHistoryIndex] = ls;
    	}
    	for(ExportFragment<T> fragment : failedFragments) {
    		Object id = fragment.getId();
    		if (id != null) {
    			ExportFragment<T> prev = retryIdentifiableFragments.put(id, fragment);
    			if (prev != null) {
    				try {
    					fragment.getProvider().onOverrideIdentifiableFragment(fragment, prev);
    				} catch(Exception ex) {
    					LOG.error("Failed for failedFragment onOverrideIdentifiableFragment() ex=" + ex.getMessage() + " ... ignore, no rethrow!");
    				}
    			}
    		} else {
    			ls.add(fragment);
    		}
    		try {
    			fragment.getProvider().onExportFragmentFailed(fragment);
			} catch(Exception ex) {
				LOG.error("Failed onExportFragmentFailed() ex=" + ex.getMessage() + " ... ignore, no rethrow!");
			}
    	}
	}

    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "RetryableFragmentHistory["
        		+ ((! retryIdentifiableFragments.isEmpty())?
        				"" + retryIdentifiableFragments.size() + " elt(s)" : "")
        		+ ((retryNoIdFragmentsRecentHistoryLenSum != 0)? 
        				" " + retryNoIdFragmentsRecentHistoryLenSum + " elt(s) no-id" : "")
        		+ "]";
    }

}
