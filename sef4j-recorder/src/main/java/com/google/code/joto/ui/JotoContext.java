package com.google.code.joto.ui;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.SwingPropertyChangeSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.JotoConfig;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.impl.DefaultMemoryRecordEventStore;
import com.google.code.joto.eventrecorder.spy.calls.MethodCallEventUtils;
import com.google.code.joto.eventrecorder.spy.calls.MethodEventWriterAopInterceptor;
import com.google.code.joto.eventrecorder.spy.calls.MethodEventWriterProxyTransformer;
import com.google.code.joto.eventrecorder.spy.calls.ObjectReplacementMap;
import com.google.code.joto.eventrecorder.writer.FilteringRecordEventWriter;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;
import com.google.code.joto.ui.filter.RecordEventFilterCategoryModel;
import com.google.code.joto.ui.filter.RecordEventFilterFile;
import com.google.code.joto.ui.spy.calls.MethodCallCaptureCategoryPanel;

/**
 *
 */
public class JotoContext {

	public static final String PROP_RECORDING_STATUS = "recordingStatus";
	
	public static enum RecordingStatus {
		RECORDING,
		IDDLE
	}
	
    
	private static Logger log = LoggerFactory.getLogger(JotoContext.class);
	
	// ------------------------------------------------------------------------
	
	private JotoConfig config;

	private RecordEventStore eventStore;

	
	private SwingPropertyChangeSupport changeSupport = new SwingPropertyChangeSupport(this);

	private RecordingStatus recordingStatus = RecordingStatus.IDDLE;

	private RecordEventFilterCategoryModel captureFilterCategoryModel;

    protected Map<String,RecordEventFilterCategoryModel> filterCategoryModels = new HashMap<String,RecordEventFilterCategoryModel>();

	protected ObjectReplacementMap objReplMap = new ObjectReplacementMap();
	
	protected MethodEventWriterAopInterceptor defaultMethodEventWriterAopInterceptor;
	protected MethodEventWriterProxyTransformer defaultMethodEventWriterProxyTransformer;
	
	// ------------------------------------------------------------------------
	
	public JotoContext(JotoConfig optConfig, RecordEventStore optEventStore) {
		this.config = (optConfig != null)? optConfig : new JotoConfig();
		this.eventStore = (optEventStore != null)? optEventStore : this.config.getEventStoreFactory().create();
		
		
		RecordEventWriter asyncEventWriter = eventStore.getAsyncEventWriter();
		this.captureFilterCategoryModel = new RecordEventFilterCategoryModel(asyncEventWriter);
		captureFilterCategoryModel.setName("jotoContext.captureFiltersWriterModel");
		captureFilterCategoryModel.setOwner(this);
		captureFilterCategoryModel.getResultFilteringEventWriter().setOwner(captureFilterCategoryModel);
		
		// getOrCreateFilteringEventWriterModelCategory(MethodCallCaptureCategoryPanel.METHODCALL_CAPTURE_CATEGORY);		
        // getOrCreateFilteringEventWriterModelCategory(LogCallCaptureCategoryPanel.LOGS_CAPTURE_CATEGORY);
        // getOrCreateFilteringEventWriterModelCategory(AWTEventCaptureCategoryPanel.AWTSPY_CAPTURE_CATEGORY);

	
		eventStore.open("rw");		
	}

	
	/** helper constructor, for test */
    public JotoContext(JotoConfig optConfig) {
        this(optConfig, null);
    }

	/** helper constructor, for test */
	public JotoContext() {
		this(new JotoConfig(), new DefaultMemoryRecordEventStore());
	}

	// ------------------------------------------------------------------------
	
	public void addPropertyChangeListener(PropertyChangeListener p) {
		changeSupport.addPropertyChangeListener(p);
	}
	public void removePropertyChangeListener(PropertyChangeListener p) {
		changeSupport.removePropertyChangeListener(p);
	}

	public RecordEventStore getEventStore() {
		return eventStore;
	}

	public JotoConfig getConfig() {
		return config;
	}

	public void setConfig(JotoConfig p) {
		JotoConfig old = config;
		this.config = p;
		changeSupport.firePropertyChange("config", old, p);
	}
	
	
	public void startRecord() {
		if (isEnableStartRecord()) {
			// do start record..
			// TODO 
			
			setRecordingStatus(RecordingStatus.RECORDING);
		}
	}
	
	public boolean isEnableStartRecord() {
		return recordingStatus == RecordingStatus.IDDLE;
	}
	
	public void stopRecord() {
		if (isEnableStopRecord()) {
			// do stop record
			// TODO 
			
			setRecordingStatus(RecordingStatus.IDDLE);
		}
	}

	public boolean isEnableStopRecord() {
		return recordingStatus == RecordingStatus.RECORDING;
	}

	private void setRecordingStatus(RecordingStatus p) {
		if (p != recordingStatus) {
			RecordingStatus old = recordingStatus;
			this.recordingStatus = p;
			log.info("setRecordingStatus " + p);
			changeSupport.firePropertyChange(PROP_RECORDING_STATUS, old, p);
		}
	}

    public RecordEventWriter getEventWriter() {
        return eventStore.getEventWriter();
    }

    public RecordEventWriter getUnfilteredAsyncEventWriter() {
	    return eventStore.getAsyncEventWriter();
	}

    public RecordEventWriter getAsyncEventWriter() {
        return captureFilterCategoryModel.getResultFilteringEventWriter();
    }

    public RecordEventFilterCategoryModel getCaptureFilterCategoryModel() {
        return captureFilterCategoryModel;
    }

	public RecordEventFilterCategoryModel getOrCreateFilterCategoryModel(String name) {
	    RecordEventFilterCategoryModel res = filterCategoryModels.get(name);
	    if (res == null) {
	        FilteringRecordEventWriter mainAsyncEventWriter = captureFilterCategoryModel.getResultFilteringEventWriter();
	        res = new RecordEventFilterCategoryModel(mainAsyncEventWriter);

            res.setName(name);

	        boolean debugOwner = true;
	        if (debugOwner) {
	            res.setOwner(this);
                
	            FilteringRecordEventWriter resFilter = res.getResultFilteringEventWriter();
    	        resFilter.setOwner(res);
    	        resFilter.setName(name);
	        }
	        
	        filterCategoryModels.put(name, res);
	    }
	    return res;
	}

	public FilteringRecordEventWriter getOrCreateFilterCategoryEventWriter(String name) {
	    RecordEventFilterCategoryModel tmpres = getOrCreateFilterCategoryModel(name);
	    return tmpres.getResultFilteringEventWriter();
	}

	/** helper method for getOrCreateFilteringEventWriterCategory(METHODCALL_CAPTURE_CATEGORY) */
	public RecordEventFilterCategoryModel getMethodCallFilterCategoryModel() {
	    return getOrCreateFilterCategoryModel(MethodCallCaptureCategoryPanel.METHODCALL_CAPTURE_CATEGORY);
	}

	/** helper method for getOrCreateFilteringEventWriterCategory(DEFAULT_METHODCALL_CAPTURE_CATEGORY) */
	public FilteringRecordEventWriter getMethodCallFilterCategoryEventWriter() {
	    return getMethodCallFilterCategoryModel().getResultFilteringEventWriter();
	}

	/** helper method for getOrCreateFilteringEventWriterCategory(DEFAULT_METHODCALL_CAPTURE_CATEGORY).addFilter(filter) */
    public void addMethodCallFilter(RecordEventFilterFile filter) {
    	getMethodCallFilterCategoryModel().addFilterRow(filter);
    }

	/** 
	 * helper method to create a default MethodEventWriterAopInterceptor, 
	 * with writer filtering on category MethodCallEventUtils.METHODCALL_EVENT_TYPE
	 * 
	 * typical example in Spring configuration file:
	 * <code><PRE>
	 * &lt;bean id="javaVersion" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
	 *    &lt;property name="targetObject" ref="jotoContext"/>
     *    &lt;property name="targetMethod" value="getDefaultMethodEventWriterAopInterceptor"/>
     * &lt;/bean>
	 * </PRE>
	 * </code>  
	 */ 
	public MethodEventWriterAopInterceptor getDefaultMethodEventWriterAopInterceptor() {
		if (defaultMethodEventWriterAopInterceptor == null) {
			String writerCategory = MethodCallEventUtils.METHODCALL_EVENT_TYPE;
			String eventType = MethodCallEventUtils.METHODCALL_EVENT_TYPE;
			defaultMethodEventWriterAopInterceptor =
					createMethodEventWriterAopInterceptor(writerCategory, eventType);
		}
		return defaultMethodEventWriterAopInterceptor;
	}

	/**
	 * helper method to create a MethodEventWriterAopInterceptor, with writer filtering on <code>writerCategory</code>
	 * @param writerCategory
	 * @param eventType
	 * @return
	 */
	public MethodEventWriterAopInterceptor createMethodEventWriterAopInterceptor(String writerCategory, String eventType) {
		FilteringRecordEventWriter eventWriter = getOrCreateFilterCategoryEventWriter(writerCategory);
		MethodEventWriterAopInterceptor res = 
				new MethodEventWriterAopInterceptor(eventWriter, eventType); 
		return res;
	}

	public MethodEventWriterProxyTransformer getDefaultMethodEventWriterProxyTransformer() {
		if (defaultMethodEventWriterProxyTransformer == null) {
			RecordEventWriter methodCallWriter = 
					getOrCreateFilterCategoryEventWriter(MethodCallEventUtils.METHODCALL_EVENT_TYPE);
			defaultMethodEventWriterProxyTransformer = 
					new MethodEventWriterProxyTransformer(methodCallWriter, this.getObjReplMap());
		}
		return defaultMethodEventWriterProxyTransformer;
	}

	public <T> T createDefaultMethodEventWriterProxy(T targetObjCallToRecord) {
		Class<?> objClass = targetObjCallToRecord.getClass();
		Class<?>[] objInterfaces = objClass.getInterfaces();
		@SuppressWarnings("unchecked")
		T resProxy = (T) createDefaultMethodEventWriterProxy(objInterfaces, targetObjCallToRecord);
		return resProxy;
	}
	
	public Object createDefaultMethodEventWriterProxy(Class<?>[] proxyInterfaces, Object targetObjCallToRecord) {
		MethodEventWriterProxyTransformer tf = getDefaultMethodEventWriterProxyTransformer();
		Object resProxy = tf.createProxy(proxyInterfaces, targetObjCallToRecord);
		return resProxy;
	}
	
    public ObjectReplacementMap getObjReplMap() {
		return objReplMap;
	}

	public void setObjReplMap(ObjectReplacementMap p) {
		this.objReplMap = p;
	}
	

}
