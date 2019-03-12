package com.lq186.fabric.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class FabricIdentitiesInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String mspid;
    
	private String id;

	public String getMspid() {
		return mspid;
	}

	public FabricIdentitiesInfo setMspid(String mspid) {
		this.mspid = mspid;
		return this;
	}

	public String getId() {
		return id;
	}

	public FabricIdentitiesInfo setId(String id) {
		this.id = id;
		return this;
	}
	
	
}
