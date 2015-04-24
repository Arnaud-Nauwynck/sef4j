package org.sef4j.testwebapp.service;

import org.atmosphere.cpr.MetaBroadcaster;
import org.sef4j.testwebapp.web.AtmosphereStatsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AtmospherePerfStatsPublishService {

	@Autowired
	private MetaBroadcaster metaBroadcaster;
	
	// ------------------------------------------------------------------------

	public AtmospherePerfStatsPublishService() {
	}

	// ------------------------------------------------------------------------

	public void publish(Object message) {
		metaBroadcaster.broadcastTo(AtmosphereStatsResourceService.PATH, message);
	}
		  
}
