package org.sef4j.core.api.session;

import java.io.Serializable;

import org.sef4j.core.util.Handle;

public class SubscriptionResponseDTO implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	private String clientCommandId;
	
	private boolean status;
	private String reasonText;
	private Handle handle;
	
	// ------------------------------------------------------------------------

	public SubscriptionResponseDTO() {
	}

	public SubscriptionResponseDTO(boolean status, String reasonText) {
		this.status = status;
		this.reasonText = reasonText;
	}

	public SubscriptionResponseDTO(Handle handle) {
		this.status = true;
		this.reasonText = "OK";
		this.handle = handle;
	}

	// ------------------------------------------------------------------------
	
	public String getClientCommandId() {
		return clientCommandId;
	}

	public void setClientCommandId(String clientCommandId) {
		this.clientCommandId = clientCommandId;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getReasonText() {
		return reasonText;
	}

	public void setReasonText(String reasonText) {
		this.reasonText = reasonText;
	}

	public Handle getHandle() {
		return handle;
	}

	public void setHandle(Handle handle) {
		this.handle = handle;
	}

	// ------------------------------------------------------------------------

	
	
}
