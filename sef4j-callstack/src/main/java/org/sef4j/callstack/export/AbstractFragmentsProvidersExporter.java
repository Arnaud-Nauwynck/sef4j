package org.sef4j.callstack.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * abstract helper class for exporters
 * 
 * @param <T> type of fragments to export (example: String for JSon fragments)
 */
public abstract class AbstractFragmentsProvidersExporter<T> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractFragmentsProvidersExporter.class);
	
    /**
     * displayName for display/debug message... 
     */
    private String displayName;

    private List<ExportFragmentsProvider<T>> fragmentProviders = new ArrayList<ExportFragmentsProvider<T>>();

    
    private Map<Object,T> retryFragmentValues = new HashMap<Object,T>(); 
    private int maxHistoryLen = 10;
    
    /** optim computed field = sum_i retryNoIdFragmentsRecentHistory.get(i).size() */
    private int retryNoIdFragmentsRecentHistoryLenSum = 0; 
    // TODO .. may optimize as a cyclic modulo arrray + curren tmodulo position
    private List<List<T>> retryNoIdFragmentsRecentHistory = new ArrayList<List<T>>();
    
    // ------------------------------------------------------------------------

    public AbstractFragmentsProvidersExporter(String displayName) {
        this.displayName = displayName;        
    }

    // ------------------------------------------------------------------------

    public ExportFragmentList<T> collectCurrentFragmentsToExport() {
    	ExportFragmentList<T> res = new ExportFragmentList<T>();
    	for (ExportFragmentsProvider<T> provider : fragmentProviders) {
			try {
				provider.provideFragments(res);
			} catch(Exception ex) {
				LOG.error("Failed to collect fragments for " + displayName 
						+ " from fragment provider: " + provider + "... ignore, no rethrow!", ex);
			}
		}
    	return res;
    }
    
    public ExportFragmentList<T> shiftAndCollectRetryFragmentsToExport() {
    	ExportFragmentList<T> res = new ExportFragmentList<T>();
    	if (! retryFragmentValues.isEmpty()) {
			res.putAllFragmentValue(retryFragmentValues);
		}
		if (retryNoIdFragmentsRecentHistoryLenSum != 0) {
			// collect all 
			for(List<T> nthPastRetry : retryNoIdFragmentsRecentHistory) {
				if (nthPastRetry != null && !nthPastRetry.isEmpty()) {
					res.addAllNonIdentifiableFragment(nthPastRetry);
				}
			}
			// shift + truncate last, and update corresponding count
			List<T> insertEmptyFirst = new ArrayList<T>();
			retryNoIdFragmentsRecentHistory.add(0, insertEmptyFirst);
			if (retryNoIdFragmentsRecentHistory.size() >= maxHistoryLen) {
				List<T> last = retryNoIdFragmentsRecentHistory.remove(maxHistoryLen);
				if (last != null) {
					retryNoIdFragmentsRecentHistoryLenSum -= last.size();
				}				
			}
		}
		return res;
    }
    
    public void export() {
    	ExportFragmentList<T> currFragments = collectCurrentFragmentsToExport();
    	ExportFragmentList<T> retryFragments = shiftAndCollectRetryFragmentsToExport();
    	
    	ExportFragmentList<T> mergedFragments = new ExportFragmentList<T>();  
    	mergedFragments.addAll(retryFragments); // may overwrite retryFragment obj? 
    	mergedFragments.addAll(currFragments);
    	
    	if (mergedFragments.isEmpty()) {
    		return; // nothing to export!
    	}
    	List<T> fragments = mergedFragments.copyToList();
    	
    	try {
    		exportFragments(fragments);
    	} catch(Exception ex) {
    		LOG.warn("Failed to export fragments! ex:" + ex.getMessage() + " .. put in retryList or try splitting in sub-list?");
    	}
    }
    
    protected void exportFragments(List<T> fragments) {
		// TODO Auto-generated method stub
		
	}

    
    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "FragmensProvidersExporter [" + displayName + "]";
    }
        
}
