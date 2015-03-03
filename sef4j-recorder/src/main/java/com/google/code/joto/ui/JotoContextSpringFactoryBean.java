package com.google.code.joto.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.code.joto.JotoConfig;
import com.google.code.joto.eventrecorder.spy.calls.ObjectReplacementMap;

/**
 * Springframework helper class for creating JotoContext
 */
public class JotoContextSpringFactoryBean implements FactoryBean<JotoContext>, ApplicationContextAware {
	
	private static Logger log = LoggerFactory.getLogger(JotoContextSpringFactoryBean.class);

	/**
	 * injected by spring ApplicationContextAware
	 */
	private ApplicationContext applicationContext;

	/**
	 * optional
	 */
	private JotoConfig config;

	// ------------------------------------------------------------------------
	
	public JotoContextSpringFactoryBean() {
	}

	// ------------------------------------------------------------------------

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public JotoContext getObject() {
		JotoContext res = new JotoContext(config, null);

		// register object to Spring bean name replacement map for code generation
		ObjectReplacementMap objReplMap = res.getObjReplMap();
		if (objReplMap == null) {
			objReplMap = new ObjectReplacementMap();
			res.setObjReplMap(objReplMap);
		}
		String[] beanNames = applicationContext.getBeanDefinitionNames();
		for (String beanName : beanNames) {
			if (!beanName.endsWith("DAO")) {
				continue; // TEMPORARY HACK
			}
			try {
				Object bean = applicationContext.getBean(beanName);

				objReplMap.addObjectInstanceReplacement(bean, beanName, beanName);

			} catch(Exception ex) {
				// ignore, no rethrow!
				log.error("failed to add spring bean replacement for joto", ex);
			}
		}
			
		return res;
	}

	@Override
	public Class<JotoContext> getObjectType() {
		return JotoContext.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
