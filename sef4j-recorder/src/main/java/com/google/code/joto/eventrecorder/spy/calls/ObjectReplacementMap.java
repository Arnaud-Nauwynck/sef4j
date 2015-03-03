package com.google.code.joto.eventrecorder.spy.calls;

import java.io.Serializable;
import java.util.IdentityHashMap;

/**
 * binding to replace some objects when recording calls as events
 * 
 * Typical usage examples:
 * <ul>
 * <li>object not serializable => can not record using Serialization! </li>
 * <li>global singleton objects from application</li>
 * <li>singleton objects defined in Spring application context</li>
 * <li>special wrapper objects use to call methods (rmi stub, ejb remote/local, proxy, aop, ...)</li>
 * </ul>
 * 
 * Implementation note:
 * This is much less general than the overriding java.io.ObjectOutputStream mecanism.
 * It is used only for serializing Method Calls.
 * It is not recursive (sub object in a complex graph are not replaced...only top-level objects in calls)
 * 
 */
public class ObjectReplacementMap {
	
	public static class ObjectInstanceReplacement implements Serializable {
		
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private String replacedObjName;
		private Serializable replacedObjData;
		
		public ObjectInstanceReplacement(String replacedObjName, Serializable replacedObjData) {
			this.replacedObjName = replacedObjName;
			this.replacedObjData = replacedObjData;
		}

		public String getReplacedObjName() {
			return replacedObjName;
		}
		public Serializable getReplacedObjData() {
			return replacedObjData;
		}
		
	}
	
	private IdentityHashMap<Object, ObjectInstanceReplacement> 
		objectInstanceReplacements = new IdentityHashMap<Object, ObjectInstanceReplacement>();
	
	//-------------------------------------------------------------------------

	public ObjectReplacementMap() {
	}

	//-------------------------------------------------------------------------

	public void addObjectInstanceReplacement(Object targetObj, String replacedObjName, Serializable replacedObjData) {
		ObjectInstanceReplacement repl = new ObjectInstanceReplacement(replacedObjName, replacedObjData);
		objectInstanceReplacements.put(targetObj, repl);
	}

	public Object checkReplace(Object obj) {
		ObjectInstanceReplacement tmp = objectInstanceReplacements.get(obj);
		if (tmp != null) {
			return tmp;
		}
		return obj;
	}

	public Object[] checkReplaceArray(Object[] objs) {
		Object[] res = objs;
		if (objs != null && objs.length != 0) {
			for(int i = 0, len = objs.length; i < len; i++) {
				Object elt = objs[i];
				Object tmpelt = checkReplace(elt);
				if (tmpelt != elt) {
					// do at least one replacement.. check/copy array
					if (res == objs) {
						Object[] prevres = res;
						res = new Object[len];
						System.arraycopy(prevres, 0, res, 0, len);
					}
					res[i] = tmpelt;
				}
			}
		}
		return res;
	}

}
