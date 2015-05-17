package org.sef4j.core.api.session;

import java.io.Serializable;
import java.util.Map;

import org.sef4j.core.util.Handle;


public class SubscriptionCommandDTO implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	private String optClientCommandId;
	
	/** one of "ADD", "REMOVE", "RESEND"... */
	private String command;

	private Handle subscriptionId;
	private Object def;
	private Object optKey;
	private String displayName;
	private Map<String,Object> options;
	
	// ------------------------------------------------------------------------

	public SubscriptionCommandDTO() {
	}

	// ------------------------------------------------------------------------
	
	public String getCommand() {
		return command;
	}

	public String getOptClientCommandId() {
		return optClientCommandId;
	}

	public void setOptClientCommandId(String optClientCommandId) {
		this.optClientCommandId = optClientCommandId;
	}

	public Handle getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(Handle subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Object getDef() {
		return def;
	}

	public void setDef(Object def) {
		this.def = def;
	}

	public Object getOptKey() {
		return optKey;
	}

	public void setOptKey(Object p) {
		this.optKey = p;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public Map<String, Object> getOptions() {
		return options;
	}

	public void setOptions(Map<String, Object> options) {
		this.options = options;
	}

	@Override
	public String toString() {
		return "SubscriptionCommandDTO [" + command 
				+ ((displayName != null)? " " + displayName : "")
				+ ", def=" + def
				+ ", optKey=" + optKey 
				+ ((options != null)? ", options=" + options  : "")
				+ "]";
	}
		
}
