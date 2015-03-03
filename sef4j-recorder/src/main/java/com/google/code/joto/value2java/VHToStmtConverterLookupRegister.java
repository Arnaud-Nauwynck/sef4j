package com.google.code.joto.value2java;

import com.google.code.joto.JotoConfig;
import com.google.code.joto.ui.JotoContextFacadePanelSpringFactoryBean;
import com.google.code.joto.value2java.VHToStmtConverterLookupRegister.VHToStmtConverterLookupRegisterEntry;
import com.google.code.joto.value2java.converters.FixedStringVHToStmtConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring helper class to register a VHToStmtConverter into the config lookup 
 *
 */
public class VHToStmtConverterLookupRegister implements FactoryBean<VHToStmtConverterLookupRegisterEntry> {
	
	private static Logger log = LoggerFactory.getLogger(JotoContextFacadePanelSpringFactoryBean.class);
	
	public static class VHToStmtConverterLookupRegisterEntry {
		private JotoConfig config;
		private Map<Class<?>,FixedStringVHToStmtConverter> classNameToFixedStringConverters;
				
		public VHToStmtConverterLookupRegisterEntry(JotoConfig config, Map<Class<?>, FixedStringVHToStmtConverter> classNameToFixedStringConverters) {
			super();
			this.config = config;
			this.classNameToFixedStringConverters = classNameToFixedStringConverters;
		}

		public JotoConfig getConfig() {
			return config;
		}

		public Map<Class<?>, FixedStringVHToStmtConverter> getClassNameToFixedStringConverters() {
			return classNameToFixedStringConverters;
		}
		
	}
	
	/**
	 * optional injected by spring
	 */
	private JotoConfig config;

	private Map<String,String> classNameToFixedStrings = new HashMap<String,String>();
	
	private VHToStmtConverterLookupRegisterEntry resultBean;
	
	// ------------------------------------------------------------------------

	public VHToStmtConverterLookupRegister() {
	}

	// ------------------------------------------------------------------------

	@Override
	public VHToStmtConverterLookupRegisterEntry getObject() throws Exception {
		if (resultBean != null) {
			Map<Class<?>,FixedStringVHToStmtConverter> resConverters = new HashMap<Class<?>,FixedStringVHToStmtConverter>();
			for(Map.Entry<String,String> e : classNameToFixedStrings.entrySet()) {
				String className = e.getKey();
				Class<?> clss = null;
				try {
					clss = Class.forName(className);
				} catch(Exception ex) {
					log.error("Failed to get class '" + className + "' => no register fixed string converter!");	
				}
				if (clss != null) {
					FixedStringVHToStmtConverter converter = new FixedStringVHToStmtConverter();
					resConverters.put(clss, converter);
					config.getVhToStmtConverterLookup().registerConverter(converter, 100);
				}
			}
			resultBean = new VHToStmtConverterLookupRegisterEntry(config, resConverters); 
		}
		return resultBean;
	}

	@Override
	public Class<?> getObjectType() {
		return VHToStmtConverterLookupRegisterEntry.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	
	// ------------------------------------------------------------------------
	
	public JotoConfig getConfig() {
		return config;
	}

	public void setConfig(JotoConfig config) {
		this.config = config;
	}

	public Map<String, String> getClassNameToFixedStrings() {
		return classNameToFixedStrings;
	}

	public void setClassNameToFixedStrings(Map<String, String> p) {
		this.classNameToFixedStrings = p;
	}
	
}
