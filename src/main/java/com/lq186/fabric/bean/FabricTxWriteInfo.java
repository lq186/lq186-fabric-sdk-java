package com.lq186.fabric.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class FabricTxWriteInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String namespace;

	private int writeSetIndex;

	private String key;

	private String value;

	public String getNamespace() {
		return namespace;
	}

	public FabricTxWriteInfo setNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	public int getWriteSetIndex() {
		return writeSetIndex;
	}

	public FabricTxWriteInfo setWriteSetIndex(int writeSetIndex) {
		this.writeSetIndex = writeSetIndex;
		return this;
	}

	public String getKey() {
		return key;
	}

	public FabricTxWriteInfo setKey(String key) {
		this.key = key;
		return this;
	}

	public String getValue() {
		return value;
	}

	public FabricTxWriteInfo setValue(String value) {
		this.value = value;
		return this;
	}

}
