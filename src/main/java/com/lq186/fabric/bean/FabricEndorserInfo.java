package com.lq186.fabric.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class FabricEndorserInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String signature;

	private String id;

	private String mspId;

	public String getSignature() {
		return signature;
	}

	public FabricEndorserInfo setSignature(String signature) {
		this.signature = signature;
		return this;
	}

	public String getId() {
		return id;
	}

	public FabricEndorserInfo setId(String id) {
		this.id = id;
		return this;
	}

	public String getMspId() {
		return mspId;
	}

	public FabricEndorserInfo setMspId(String mspId) {
		this.mspId = mspId;
		return this;
	}

}
