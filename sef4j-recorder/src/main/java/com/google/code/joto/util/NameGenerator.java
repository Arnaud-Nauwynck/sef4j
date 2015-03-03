package com.google.code.joto.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class NameGenerator {


	private static class IdGenerator {
		private String namePrefix;
		int idGenerator = 0;
		
		public IdGenerator(String namePrefix) {
			this.namePrefix = namePrefix;
		}

		public String generateId() { 
			int id = idGenerator++;
			String res = namePrefix;
			if (id > 0) {
				char lastChar = res.charAt(res.length() - 1);
				if (Character.isDigit(lastChar)) {
					res += "_";
				}
				res += id;
			}
			return res;
		}
	}
	
	private Map<Class<?>,String> classAlias = new HashMap<Class<?>,String>();
	
	private Map<String,IdGenerator> prefixToIdGenerator =
		new HashMap<String,IdGenerator>();

	// -------------------------------------------------------------------------
	
	public NameGenerator() {
		super();
	} 

	// -------------------------------------------------------------------------

	public String newName(Class<?> clss) {
		String prefix = classToAlias(clss);
		return newName(prefix);
	}

	public String newName(String prefix) {
		return nameToIdGenerator(prefix).generateId();
	}
	
	// -------------------------------------------------------------------------
	
	protected IdGenerator nameToIdGenerator(String prefix) {
		IdGenerator res = prefixToIdGenerator.get(prefix);
		if (res == null) {
			res = new IdGenerator(prefix);
			prefixToIdGenerator.put(prefix, res);
		}
		return res;
	}

	public String classToAlias(Class<?> clss) {
		String res = classAlias.get(clss);
		if (res == null) {
			res = clss.getSimpleName();
			// use lower case for first letter
			res = Character.toLowerCase(res.charAt(0)) + ((res.length()> 1)? res.substring(1): "");

			classAlias.put(clss, res);
		}
		return res;
	}
	
}
