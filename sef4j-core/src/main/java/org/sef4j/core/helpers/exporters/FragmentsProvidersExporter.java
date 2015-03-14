package org.sef4j.core.helpers.exporters;

import java.util.List;

import org.sef4j.core.api.EventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * helper class for exporters
 * collect from a list of FragmentProvider(s), and delegate to eventSender to send
 * 
 * @param <T> type of fragments to export (example: String for JSon fragments)
 */
public class FragmentsProvidersExporter<T> {

	private static final Logger LOG = LoggerFactory.getLogger(FragmentsProvidersExporter.class);
	
    /**
     * displayName for display/debug message... 
     */
    protected String displayName;

    private List<ExportFragmentsProvider<T>> fragmentProviders;
    
    protected EventSender<T> exportSender;
    
    // ------------------------------------------------------------------------

    public FragmentsProvidersExporter(String displayName, 
    		List<ExportFragmentsProvider<T>> fragmentProviders, EventSender<T> exportSender) {
        this.displayName = displayName;
        this.fragmentProviders = fragmentProviders;
        this.exportSender = exportSender;
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
    
    public void export() {
    	ExportFragmentList<T> fragmentList = collectCurrentFragmentsToExport();
    	List<T> fragmentValues = fragmentList.toValues();
    	try {
    		exportSender.sendEvents(fragmentValues);
    	} catch(Exception ex) {
    		LOG.warn("Failed to export fragments! ex:" + ex.getMessage() + " ... ignore, no rethrow!");
    	}
    }
    
    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "FragmentsProvidersExporter [" + displayName + "]";
    }
        
}
