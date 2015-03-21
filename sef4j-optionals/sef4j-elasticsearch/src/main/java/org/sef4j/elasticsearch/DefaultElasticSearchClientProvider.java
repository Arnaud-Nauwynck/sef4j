package org.sef4j.elasticsearch;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.sef4j.core.helpers.IPropertyChangeListenerSupport;

/**
 * a provider for ElasticSearch Client using a list of TransportClient URLs
 * 
 * the list of URLs may change at runtime, as cluster may change at runtime (node are up/down/created/destroyed...)
 */
public class DefaultElasticSearchClientProvider implements Callable<Client>, IPropertyChangeListenerSupport {

	private Settings settings;
	
	private TransportAddress[] transportAddresses;
	
	private PropertyChangeSupport propChangeSupport = new PropertyChangeSupport(this);
	
	// ------------------------------------------------------------------------
	
	public DefaultElasticSearchClientProvider(Settings settings, TransportAddress[] transportAddresses) {
		this.settings = (settings != null)? settings: ImmutableSettings.Builder.EMPTY_SETTINGS;
		this.transportAddresses = transportAddresses;
	}

	// ------------------------------------------------------------------------
	
	@SuppressWarnings("resource")
	@Override
	public Client call() throws Exception {
		return new TransportClient(settings)
			.addTransportAddresses(transportAddresses);
	}

	/** implements IPropertyChangeListenerSupport, so that client may be reconfigured at runtime when cluster change */
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propChangeSupport.addPropertyChangeListener(listener);
	}

	/** implements IPropertyChangeListenerSupport, so that client may be reconfigured at runtime when cluster change */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propChangeSupport.removePropertyChangeListener(listener);
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "ElasticSearchClientProvider [" 
				+ "addresses=" + Arrays.toString(transportAddresses)
				+ "]";
	}

}