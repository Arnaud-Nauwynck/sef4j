package org.sef4j.core.appenders.elasticsearch.client;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.sef4j.api.EventAppender;
import org.sef4j.core.appenders.BulkAsyncAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facade for ElasticSearch client API
 */
public class ElasticSearchClientFacade {

    static final Logger LOG = LoggerFactory.getLogger(ElasticSearchClientFacade.class);

    private String displayName;

    private Client esClient;

    protected PropertyChangeListener innerPropChangeListener = new PropertyChangeListener() {
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	    onEsClientProviderPropertyChange(evt);
	}
    };

    /**
     * low-level adapter to EventSender<ActionRequest>, to send requests to
     * underlying esClient
     */
    private InnerBulkActionRequestEventAppender esClientBulkActionRequestEventSender = new InnerBulkActionRequestEventAppender();

    // private ElasticSearchBulkAsyncDisrupterHelper asyncDisrupterHelper;
    private BulkAsyncAppender<ActionRequest<?>> bulkActionRequestAsyncSender;

    private static final Function<ActionRequest<?>, Integer> asyncRequestToByteLengthProvider = new Function<ActionRequest<?>, Integer>() {
	@Override
	public Integer apply(ActionRequest<?> t) {
	    BulkRequest bulk = new BulkRequest();
	    bulk.add(t);
	    return (int) bulk.estimatedSizeInBytes();
	    // return ((IndexRequest)t).source().length() + 50; // cf
	    // BulkRequest.estimatedSizeInBytes() BulkRequest.REQUEST_OVERHEAD
	}
    };

    // ------------------------------------------------------------------------

    public ElasticSearchClientFacade(String displayName, 
	    Client esClient,
	    BulkAsyncAppender.Builder<ActionRequest<?>> asyncSettingsBuilder) {
	this.displayName = displayName;
	this.esClient = esClient;
	if (asyncSettingsBuilder == null) {
	    asyncSettingsBuilder = new BulkAsyncAppender.Builder<ActionRequest<?>>();// use default
	}
	asyncSettingsBuilder.eventByteLengthProvider(asyncRequestToByteLengthProvider);

	if (asyncSettingsBuilder.getAsyncDisruptorErrorHandler() == null) {
	    asyncSettingsBuilder.asyncDisruptorErrorHandler(new DefaultAsyncDisrupterErrorHandler<ActionRequest<?>>());
	}

	this.bulkActionRequestAsyncSender = asyncSettingsBuilder.build(esClientBulkActionRequestEventSender);
    }

    // support for ES Client init / dispose and support for update from Client
    // Provider
    // ------------------------------------------------------------------------

    public void start() {
	bulkActionRequestAsyncSender.start();
    }

    public void stop() {
	Client toClose = esClient;
	if (toClose != null) {
	    this.esClient = null;
	    try {
		toClose.close();
	    } catch (Exception ex) {
		LOG.warn("Failed to close esClient, ex=" + ex.getMessage() + " ... ignore, no rethrow!");
	    }
	}
	bulkActionRequestAsyncSender.stop();
    }

    protected void onEsClientProviderPropertyChange(PropertyChangeEvent evt) {
	Client toClose = esClient;
	if (toClose != null) {
	    stop();
	    start();
	}
    }

    // delegate to esClient
    // ------------------------------------------------------------------------

    public Client getEsClient() {
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
	bulkActionRequestAsyncSender.sendEvent(req);
    }

    public void asyncRequests(Collection<ActionRequest<?>> reqs) {
	bulkActionRequestAsyncSender.sendEvents(reqs);
    }

    public void asyncIndexRequest(String index, String type, Object... source) {
	IndexRequest req = new IndexRequest(index, type).source(source);
	bulkActionRequestAsyncSender.sendEvent(req);
    }

    /**
     * Internal adapter to send ElasticSearch ActionRequest (or grouped by
     * BulkRequest), using the EventSender API
     * 
     */
    private class InnerBulkActionRequestEventAppender implements EventAppender<ActionRequest<?>> {

	@Override
	public void sendEvent(ActionRequest<?> event) {
	    // send 1 ActionRequest : using bulk?
	    BulkRequest bulkReq = new BulkRequest();
	    bulkReq.add(event);
	    ActionFuture<BulkResponse> resp = bulk(bulkReq);
	    waitAndRethrowEx(resp);
	}

	@Override
	public void sendEvents(Collection<ActionRequest<?>> events) {
	    BulkRequest bulkReq = new BulkRequest();
	    for (ActionRequest<?> r : events) {
		bulkReq.add(r);
	    }
	    ActionFuture<BulkResponse> resp = bulk(bulkReq);
	    waitAndRethrowEx(resp);
	}

	private void waitAndRethrowEx(ActionFuture<?> resp) {
	    // force get synchronous result to throws exception on error in caller thread!
	    try {
		resp.get();
	    } catch (InterruptedException e) {
		throw new RuntimeException(e);
	    } catch (ExecutionException e) {
		throw new RuntimeException(e);
	    }
	}

    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
	return "ElasticSearchClient[" + displayName + "]";
    }

}
