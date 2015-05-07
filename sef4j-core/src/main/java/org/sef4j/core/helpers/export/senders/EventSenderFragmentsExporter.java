package org.sef4j.core.helpers.export.senders;

import java.util.List;
import java.util.function.Function;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.helpers.export.ExportFragmentList;
import org.sef4j.core.helpers.export.ExportFragmentsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * helper class for exporters
 * collect from a list of FragmentProvider(s), and delegate to eventSender to send
 * 
 *  * <PRE>
 *             exporter.sendEventsForCollectedFragments(): 
 *                  { ls=collectFragments(); sendEvents(ls) }
 *                                   /                     \
 *                               <--/                       \-->
 *       ls=providers.provideFragments()                          targetSender.sendEvents(ls)
 * 
 *                                 +-----------------------+     
 * FragmentProvider(s)    <(*)---- |                       |------(1)> targetEventSender
 *      /\         +------------------                     |
 *       |         +----------------------> ...      -  --------->  
 *   +---+---+                     +-----------------------+
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


 * @param <T> type of fragments to export (example: String for JSon fragments)
 */
public class EventSenderFragmentsExporter<T,E> {

	private static final Logger LOG = LoggerFactory.getLogger(EventSenderFragmentsExporter.class);
	
    /**
     * displayName for display/debug message... 
     */
    protected String displayName;

    private List<ExportFragmentsProvider<T>> fragmentProviders;
    
	protected Function<ExportFragmentList<T>,List<E>> fragmentsToEventsConverter;

    protected EventSender<E> exportSender;
    
    // ------------------------------------------------------------------------

    public EventSenderFragmentsExporter(String displayName, 
    		List<ExportFragmentsProvider<T>> fragmentProviders, 
    		Function<ExportFragmentList<T>,List<E>> fragmentsToEventsConverter,
    		EventSender<E> exportSender) {
        this.displayName = displayName;
        this.fragmentProviders = fragmentProviders;
        this.fragmentsToEventsConverter = fragmentsToEventsConverter;
        this.exportSender = exportSender;
    }

    // ------------------------------------------------------------------------

    public ExportFragmentList<T> collectFragmentsToExport() {
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

	protected ExportFragmentList<T> markAndCollectFragmentChanges() {
		ExportFragmentList<T> changedFragments = new ExportFragmentList<T>();
    	for (ExportFragmentsProvider<T> provider : fragmentProviders) {
			try {
				provider.markAndCollectChanges(changedFragments);
			} catch(Exception ex) {
				LOG.error("Failed to collect fragment changes for " + displayName 
						+ " from fragment provider: " + provider + "... ignore, no rethrow!", ex);
			}
		}
		return changedFragments;
	}

    
    public void sendEventsForCollectedFragments() {
    	ExportFragmentList<T> fragments = collectFragmentsToExport();
    	List<E> events = fragmentsToEventsConverter.apply(fragments);
		exportSender.sendEvents(events);
    }
    
	public void sendEventsForMarkedAndCollectedChangedFragments() {
		ExportFragmentList<T> changedFragments = markAndCollectFragmentChanges();
		if (! changedFragments.isEmpty()) {
			List<E> events = fragmentsToEventsConverter.apply(changedFragments);
    		exportSender.sendEvents(events);
		}
	}
    
    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "FragmentsProvidersExporter [" + displayName + "]";
    }

    // ------------------------------------------------------------------------
    
    public static class Builder<T,E> {
        protected String displayName;
        private List<ExportFragmentsProvider<T>> fragmentProviders;
    	protected Function<ExportFragmentList<T>,List<E>> fragmentsToEventsConverter;
        protected EventSender<E> exportSender;

        public EventSenderFragmentsExporter<T,E> build() {
        	return new EventSenderFragmentsExporter<T, E>(displayName, fragmentProviders, fragmentsToEventsConverter, exportSender);
        }
        
		public Builder<T,E> withDisplayName(String displayName) {
			this.displayName = displayName;
			return this;
		}
		public Builder<T,E> withFragmentProviders(List<ExportFragmentsProvider<T>> fragmentProviders) {
			this.fragmentProviders = fragmentProviders;
			return this;
		}
		public Builder<T,E> withFragmentsToEventsConverter(Function<ExportFragmentList<T>, List<E>> fragmentsToEventsConverter) {
			this.fragmentsToEventsConverter = fragmentsToEventsConverter;
			return this;
		}
		public Builder<T,E> withExportSender(EventSender<E> exportSender) {
			this.exportSender = exportSender;
			return this;
		}
    }
    
}
