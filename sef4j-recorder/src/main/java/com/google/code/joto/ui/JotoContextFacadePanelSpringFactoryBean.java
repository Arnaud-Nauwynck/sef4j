package com.google.code.joto.ui;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStoppedEvent;

/**
 * Springframework Graphical User Interface for JOTO
 * 
 * spring FactoryBean helper class, for creating (+opening) an admin JFrame in the application
 */
public class JotoContextFacadePanelSpringFactoryBean implements FactoryBean<JFrame>, ApplicationListener<ApplicationEvent> {
	
	private static Logger log = LoggerFactory.getLogger(JotoContextFacadePanelSpringFactoryBean.class);

	/**
	 * injected by spring
	 */
	private JotoContext context;

	/**
	 * result object 
	 */
	private JFrame frame; 
	
	// ------------------------------------------------------------------------
	
	public JotoContextFacadePanelSpringFactoryBean() {
	}

	public JotoContextFacadePanelSpringFactoryBean(JotoContext context) {
		this.context = context;
	}

	// ------------------------------------------------------------------------

	public JotoContext getContext() {
		return context;
	}

	public void setContext(JotoContext context) {
		this.context = context;
	}

	@Override
	public JFrame getObject() {
		if (frame == null) {
			log.info("creating Joto frame");
			JotoContextFacadePanel recordEventPanel = new JotoContextFacadePanel(context);
						
			frame = new JFrame();
			frame.getContentPane().add(recordEventPanel.getJComponent());
			frame.pack();
			frame.setVisible(true);
		}
		return frame;
	}

	@Override
	public Class<JFrame> getObjectType() {
		return JFrame.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextStoppedEvent) {
			if (frame != null) {
				frame.dispose();
				frame = null;
			}
		}
	}
	
}
