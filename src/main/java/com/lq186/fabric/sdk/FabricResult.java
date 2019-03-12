package com.lq186.fabric.sdk;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class FabricResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;

	private Object data;

	private String txid;

	public static final FabricResult newInstance(String code, Object data) {
		FabricResult result = new FabricResult();
		result.code = code;
		result.data = data;
		return result;
	}

	public String getCode() {
		return code;
	}

	public FabricResult setCode(String code) {
		this.code = code;
		return this;
	}

	public Object getData() {
		return data;
	}

	public FabricResult setData(Object data) {
		this.data = data;
		return this;
	}

	public String getTxid() {
		return txid;
	}

	public FabricResult setTxid(String txid) {
		this.txid = txid;
		return this;
	}

}
