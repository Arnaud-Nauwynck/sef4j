package org.sef4j.testwebapp.config;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stats.dto.BasicStatPropTreeValueProviderDef;
import org.sef4j.core.api.EventSender;
import org.sef4j.core.api.ioeventchain.InputEventChain.ListenerHandle;
import org.sef4j.core.helpers.export.ExportFragmentList;
import org.sef4j.core.helpers.export.senders.ExportFragmentsPollingEventProviderDef;
import org.sef4j.core.helpers.ioeventchain.PeriodicTaskInputEventChain;
import org.sef4j.core.helpers.proptree.dto.PropTreeRootNodeDef;
import org.sef4j.core.helpers.senders.InMemoryEventSender;
import org.sef4j.core.helpers.tasks.PeriodicTaskInputEventChainDef;
import org.sef4j.core.helpers.tasks.PeriodicityDef;
import org.sef4j.core.util.factorydef.ObjectByDefRepositories;
import org.sef4j.core.util.factorydef.SharedRef;


public class MetricsFactoryConfigTest extends AbstractSpringTestWebappTestCase {

	MetricsFactoryConfig sut = new MetricsFactoryConfig();
	ObjectByDefRepositories repositories = sut.sharedObjByDefRepositories();
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSharedObjByDefRepositories() {
		// Prepare
		Object def = new PeriodicTaskInputEventChainDef(
				new PeriodicityDef(15, TimeUnit.SECONDS, null),
    			new ExportFragmentsPollingEventProviderDef(
    					Collections.singletonList(
    							new BasicStatPropTreeValueProviderDef(
    									new PropTreeRootNodeDef("ws")))));
		// Perform
		SharedRef<Object> inputChainRef = repositories.getOrCreateByDef(def, null);
		try {
			Object inputChainObj = inputChainRef.getObject();
			// Post-check
			Assert.assertNotNull(inputChainObj);
			PeriodicTaskInputEventChain<ExportFragmentList<PerfStats>> inputChain = (PeriodicTaskInputEventChain<ExportFragmentList<PerfStats>>) inputChainObj;
			
			// inputChain.poll();
			// inputChain.start();
			// inputChain.stop();
			
			// re-call getOrCreateByDef => check same object returned
			{
				SharedRef<Object> ref2 = repositories.getOrCreateByDef(def, null);
				Object obj2 = ref2.getObject();
				Assert.assertSame(inputChainObj, obj2);
				ref2.close();
			}
			
			// test behavior or create InputChain : periodic task exporter for PerfStats change...
			EventSender<ExportFragmentList<PerfStats>> listener = new InMemoryEventSender<ExportFragmentList<PerfStats>>();
			ListenerHandle<ExportFragmentList<PerfStats>> listenerHandle = inputChain.registerEventListener(listener);
			try {
				inputChain.poll(); // ==> send to callback listener 
				// ...
				
			} finally {
				inputChain.unregisterEventListener(listenerHandle);
			}
			
			inputChain.close(); // useless?
		} finally {
			inputChainRef.close();
		}
	}
}
