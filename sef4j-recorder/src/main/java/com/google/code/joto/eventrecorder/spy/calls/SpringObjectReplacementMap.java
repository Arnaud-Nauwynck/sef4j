package com.google.code.joto.eventrecorder.spy.calls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 */
public class SpringObjectReplacementMap extends ObjectReplacementMap implements ApplicationContextAware {
	
	private static Logger log = LoggerFactory.getLogger(SpringObjectReplacementMap.class);
	
	protected ApplicationContext applicationContext;
	private boolean cached = false;
	
	//-------------------------------------------------------------------------

	public SpringObjectReplacementMap() {
	}

	//-------------------------------------------------------------------------

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public Object checkReplace(Object obj) {
		if (!cached) {
			cached = true;
			addSpringBeanReplacements();
		}
		Object res = super.checkReplace(obj);
		return res;
	}

	private void addSpringBeanReplacements() {
		String[] beanNames = applicationContext.getBeanDefinitionNames();
		for (String beanName : beanNames) {
//			if (!beanName.endsWith("DAO")) {
//				continue; // TEMPORARY HACK
//			}
			try {
				Object bean = applicationContext.getBean(beanName);

				super.addObjectInstanceReplacement(bean, beanName, beanName);

			} catch(Exception ex) {
				// ignore, no rethrow!
				log.error("failed to add spring bean replacement for joto", ex);
			}
		}
	}

	 
}
