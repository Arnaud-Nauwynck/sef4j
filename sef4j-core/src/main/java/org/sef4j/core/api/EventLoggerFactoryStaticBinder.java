package org.sef4j.core.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the infamous ugly anti-pattern "singleton" but fortunatly, there is 1 singleton per ClassLoader,
 * which allow separation per application deployed on application servers (osgi,webapps,ejb,.. )
 * 
 * This is the user responsibility to (re-)initialize this singleton if default inititialisation doesn't fit user needs.
 * 
 * It is safe to use code like this:
 * <code>
 *    public static final EventLogger EVENT_LOGGER = EventLoggerFactoryStaticBinder.getEventLogger(..); // alias: EventLogger.getInstance(..) 
 * </code>
 * because EventLogger are "owned" by the default static EventLoggerFactory, and may be reconfigured at runtime with new appenders
 *
 */
public class EventLoggerFactoryStaticBinder {
	
	private static final Logger LOG = LoggerFactory.getLogger(EventLoggerFactoryStaticBinder.class);
	
	private static EventLoggerFactory INSTANCE;
	
	static {
		try {
			initDefaultInstance();
		} catch(Exception ex) {
			
		}
	}

	public static EventLoggerFactory getInstance() {
		return INSTANCE;
	}

	/**
	 * by default ... "sef4j.config.xml" file is loaded, and used to instanciate appender and logger config
	 * using XStream serialisation (or joran?)
	 * 
	 * Notice that such appenders instances will not be managed by a real IOC Container, such as spring context...
	 * so the best approach is still to use your application container (Spring..) to manage the EventLoggerFactory and all its appenders
	 */
	private static void initDefaultInstance() {
	    LOG.info("EventLoggerFactoryStaticBinder.iniDefaultInstance() : load default \"sef4j.config.xml\"");
	    EventLoggerContext eventLoggerContext = new EventLoggerContext();
		// TODO load default "sef4j.config.xml"
		
		INSTANCE = new EventLoggerFactory(eventLoggerContext);
	}
	
	

}
