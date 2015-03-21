package org.sef4j.elasticsearch;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.Callable;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.sef4j.core.helpers.IPropertyChangeListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facade for ElasticSearch client API
 */
public class ElasticSearchClientFacade {

	private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchClientFacade.class);
	
	private String displayName;
	
	private Callable<Client> esClientProvider;
	private Client esClient;

	protected PropertyChangeListener innerPropChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onEsClientProviderPropertyChange(evt);
		}
	};

	private ElasticSearchBulkAsyncDisrupterHelper asyncDisrupterHelper;
	
	// ------------------------------------------------------------------------

	public ElasticSearchClientFacade(String displayName, Callable<Client> esClientFactory,
			ElasticSearchBulkAsyncDisrupterHelper.Builder asyncSettingsBuilder) {
		this.displayName = displayName;
		this.esClientProvider = esClientFactory;
		if (esClientProvider instanceof IPropertyChangeListenerSupport) { 
			IPropertyChangeListenerSupport listenerSupport = (IPropertyChangeListenerSupport) esClientProvider;
			listenerSupport.addPropertyChangeListener(innerPropChangeListener);
		}
		if (asyncSettingsBuilder == null) {
			asyncSettingsBuilder = new ElasticSearchBulkAsyncDisrupterHelper.Builder();// use default
		}
		this.asyncDisrupterHelper = asyncSettingsBuilder.build(this);
	}

	// support for ES Client init / dispose and support for update from Client Provider
	// ------------------------------------------------------------------------

	public void start() {
		Client cli = esClient;
		if (cli != null) {
			return;
		}
		try {
			this.esClient = esClientProvider.call();
		} catch (Exception ex) {
			throw new RuntimeException("Failed to create Elasticsearch client!", ex);
		}
		asyncDisrupterHelper.start();
	}
	
	public void stop() {
		Client toClose = esClient;
		if (toClose != null) {
			this.esClient = null;
			try {
				toClose.close();
			} catch(Exception ex) {
				LOG.warn("Failed to close esClient, ex=" + ex.getMessage() + " ... ignore, no rethrow!");
			}
		}
		asyncDisrupterHelper.stop();
	}
	
	protected  void onEsClientProviderPropertyChange(PropertyChangeEvent evt) {
		Client toClose = esClient;
		if (toClose != null) {
			stop();
			start();
		}
	}

	// delegate to esClient
	// ------------------------------------------------------------------------
	
	public Client getEsClient() {
		// TOADD ... may return a wrapper, to increment counter stats
		return esClient;
	}

    public void bulk(BulkRequest request, ActionListener<BulkResponse> listener) {
    	esClient.bulk(request, listener);
    }

    public ActionFuture<BulkResponse> bulk(BulkRequest request) {
    	return esClient.bulk(request);
    }

//    public ActionFuture<BulkResponse> bulkIndex(IndexRequest... indexRequests) {
//    	BulkRequest bulkReq = new BulkRequest();
//    	for (IndexRequest r : indexRequests) {
//    		bulkReq.add(r);
//    	}
//    	return bulk(bulkReq);
//    }
//
//    public static void bulkAddIndex(BulkRequest bulkRequest,
//    		String index, String type,
//    		Object... source) {
//    	IndexRequest req = new IndexRequest(index, type).source(source);
//    	bulkRequest.add(req);
//    }
    
    // Support for asynchronous buffering + and periodic flushing
    // ------------------------------------------------------------------------
    
    public void asyncRequest(ActionRequest<?> req) {
    	asyncDisrupterHelper.asyncRequest(req);
    }

    public void asyncIndexRequest(String index, String type, Object... source) {
    	IndexRequest req = new IndexRequest(index, type).source(source);
    	asyncDisrupterHelper.asyncRequest(req);
    }

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "ElasticSearchClient[" + displayName + "]";
	}
	
	
}
