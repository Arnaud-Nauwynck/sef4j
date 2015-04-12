package org.sef4j.testwebapp.dto;

import java.io.Serializable;

public class ProductDTO implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String shortDescr;
	
	// ------------------------------------------------------------------------
	
	public ProductDTO() {
	}

	public ProductDTO(int id, String name, String shortDescr) {
		this.id = id;
		this.name = name;
		this.shortDescr = shortDescr;
	}


	// ------------------------------------------------------------------------
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortDescr() {
		return shortDescr;
	}

	public void setShortDescr(String shortDescr) {
		this.shortDescr = shortDescr;
	}
	
	
	
}
