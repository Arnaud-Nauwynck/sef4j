package org.sef4j.core.helpers.export.senders;

import java.util.List;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.helpers.export.ExportFragmentList;
import org.sef4j.core.helpers.export.ExportFragmentsProvider;
import org.sef4j.core.helpers.export.ExportFragmentsProviderDef;
import org.sef4j.core.helpers.tasks.PollingEventProvider.AbstractPollingEventProvider;
import org.sef4j.core.util.factorydef.AbstractSharedObjByDefFactory;
import org.sef4j.core.util.factorydef.DependencyObjectCreationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * class for collecting data or incremental changes to export as event "ExportFragmentList<T>"
 * 
 * <PRE>
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
 * 
 * @see EventSenderFragmentsExporterTask for using with a PeriodicTask
 * 
 * @param <T> type of fragments to export (example: String for JSon fragments)
 */
public class ExportFragmentsPollingEventProvider<T> extends AbstractPollingEventProvider<ExportFragmentList<T>> {

	private static final Logger LOG = LoggerFactory.getLogger(ExportFragmentsPollingEventProvider.class);
	
    private List<ExportFragmentsProvider<T>> fragmentProviders;
    
    // ------------------------------------------------------------------------

    public ExportFragmentsPollingEventProvider(String displayName, 
    		List<ExportFragmentsProvider<T>> fragmentProviders) {
        super(displayName);
        this.fragmentProviders = fragmentProviders;
    }

    // ------------------------------------------------------------------------

    @Override
    public void poll() {
    	sendEventsForMarkAndCollectChanges();
    }
    

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
    	sendEventsForCollectedFragments(this);
    }
    
    public void sendEventsForCollectedFragments(EventSender<ExportFragmentList<T>> to) {
    	ExportFragmentList<T> fragments = collectFragmentsToExport();
		to.sendEvent(fragments);
    }
    
	public void sendEventsForMarkAndCollectChanges() {
		ExportFragmentList<T> changedFragments = markAndCollectFragmentChanges();
		if (! changedFragments.isEmpty()) {
			super.sendEvent(changedFragments);
		}
	}
    
    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return "ExportFragmentsEventProvider[" + displayName + "]";
    }

    // ------------------------------------------------------------------------
    
    public static class Builder<T> {
        protected String displayName;
        private List<ExportFragmentsProvider<T>> fragmentProviders;

        public ExportFragmentsPollingEventProvider<T> build() {
        	return new ExportFragmentsPollingEventProvider<T>(displayName, fragmentProviders);
        }
        
		public Builder<T> withDisplayName(String displayName) {
			this.displayName = displayName;
			return this;
		}
		public Builder<T> withFragmentProviders(List<ExportFragmentsProvider<T>> fragmentProviders) {
			this.fragmentProviders = fragmentProviders;
			return this;
		}
    }
 
    // ------------------------------------------------------------------------
    
    public static class ExportFragmentsPollingEventProviderFactory<T> 
    	extends AbstractSharedObjByDefFactory<ExportFragmentsPollingEventProviderDef, ExportFragmentsPollingEventProvider<T>> {

		public ExportFragmentsPollingEventProviderFactory() {
			super("ExportFragmentsPollingEventProvider", ExportFragmentsPollingEventProviderDef.class);
		}

		@Override
		public ExportFragmentsPollingEventProvider<T> create(
				ExportFragmentsPollingEventProviderDef def,
				DependencyObjectCreationContext ctx) {
			ImmutableList<ExportFragmentsProviderDef> fragmentProviderDefs = def.getFragmentProviderDefs();
			
			List<ExportFragmentsProvider<T>> fragmentProviders = 
					ctx.getOrCreateDependencyByDefs("fragmentProviders", fragmentProviderDefs);

			String displayName = ctx.getCurrObjectDisplayName();
			return new ExportFragmentsPollingEventProvider<T>(displayName, fragmentProviders);
		}
    	
    }
    
}
