package org.sef4j.testwebapp.config;

import org.sef4j.callstack.stats.dto.BasicStatPropTreeValueProviderDef;
import org.sef4j.callstack.stats.dto.PendingCountPropTreeValueProviderDef;
import org.sef4j.callstack.stattree.changes.BasicStatPropTreeValueProvider;
import org.sef4j.callstack.stattree.changes.PendingCountPropTreeValueProvider;
import org.sef4j.core.helpers.export.senders.ExportFragmentsPollingEventProvider.ExportFragmentsPollingEventProviderFactory;
import org.sef4j.core.helpers.export.senders.ExportFragmentsPollingEventProviderDef;
import org.sef4j.core.helpers.ioeventchain.PeriodicTaskInputEventChain;
import org.sef4j.core.helpers.proptree.dto.PropTreeRootNodeDef;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;
import org.sef4j.core.helpers.proptree.model.PropTreeRootNodeFactory;
import org.sef4j.core.helpers.tasks.PeriodicTaskInputEventChainDef;
import org.sef4j.core.util.factorydef.ObjectByDefRepositories;
import org.sef4j.testwebapp.service.MetricsStatsTreeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsFactoryConfig {

    // @Autowired protected MetricsStatsTreeRegistry metricsStatsTreeRegistry;
    
	@Bean
    public ObjectByDefRepositories sharedObjByDefRepositories() {
    	ObjectByDefRepositories res = new ObjectByDefRepositories();

    	// register factories
    	res.registerFactoryFor(PeriodicTaskInputEventChainDef.class, 
    			PeriodicTaskInputEventChain.Factory.instance());
		res.registerFactoryFor(ExportFragmentsPollingEventProviderDef.class,
				new ExportFragmentsPollingEventProviderFactory<Object>());

		PropTreeNode rootWSNode = MetricsStatsTreeRegistry.getRootWSStatsNode();

		PropTreeRootNodeFactory propTreeRootFactory = PropTreeRootNodeFactory.INSTANCE;
		propTreeRootFactory.putRootNode("ws", rootWSNode);
		res.registerFactoryFor(PropTreeRootNodeDef.class, propTreeRootFactory);

		res.registerFactoryFor(BasicStatPropTreeValueProviderDef.class,
				new BasicStatPropTreeValueProvider.Factory());
		res.registerFactoryFor(PendingCountPropTreeValueProviderDef.class,
				new PendingCountPropTreeValueProvider.Factory());

    	return res;
    }
    

}
