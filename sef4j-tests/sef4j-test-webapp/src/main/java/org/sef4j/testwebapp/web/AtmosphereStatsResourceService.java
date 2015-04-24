package org.sef4j.testwebapp.web;

import org.apache.commons.io.Charsets;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.Get;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 */
@Component
@ManagedService(path = AtmosphereStatsResourceService.PATH)
public class AtmosphereStatsResourceService {

	private static final Logger LOG = LoggerFactory.getLogger(AtmosphereStatsResourceService.class);

	public static final String PATH = "/websocket/perfstats";

	@Get
	public void init(AtmosphereResource resource) {
		LOG.info("init WebSocket Atmosphere resource for path: " + PATH);
		// Set the character encoding as atmospheres default is not unicode.
		resource.getResponse().setCharacterEncoding(Charsets.UTF_8.name());
	}

	@Ready
	public void onReady(final AtmosphereResource resource) {
		LOG.info("Browser " + resource.uuid() + " connected.");
	}

	@Disconnect
	public void onDisconnect(AtmosphereResourceEvent event) {
		if (event.isCancelled()) {
			LOG.info("Browser " + event.getResource().uuid() + " unexpectedly disconnected");
		} else if (event.isClosedByClient()) {
			LOG.info("Browser " + event.getResource().uuid() + " closed the connection");
		}
	}

}
