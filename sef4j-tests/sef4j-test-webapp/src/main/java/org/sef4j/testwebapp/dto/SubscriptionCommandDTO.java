package org.sef4j.testwebapp.dto;

public class SubscriptionCommandDTO {

	/** one of "ADD", "REMOVE", "RESEND"... */
	private String command;

	private String path;
	
	private String propMapper;
	
	// ------------------------------------------------------------------------

	public SubscriptionCommandDTO() {
	}

	// ------------------------------------------------------------------------
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPropMapper() {
		return propMapper;
	}

	public void setPropMapper(String propMapper) {
		this.propMapper = propMapper;
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "SubscriptionCommandDTO [" + command + ", path=" + path + ", propMapper=" + propMapper + "]";
	}
		
}
