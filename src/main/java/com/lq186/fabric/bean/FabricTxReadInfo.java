package com.lq186.fabric.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class FabricTxReadInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String namespace;

	private int readSetIndex;

	private String key;

	private long readVersionBlockNum;

	private long readVersionTxNum;

	public String getNamespace() {
		return namespace;
	}

	public FabricTxReadInfo setNamespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	public int getReadSetIndex() {
		return readSetIndex;
	}

	public FabricTxReadInfo setReadSetIndex(int readSetIndex) {
		this.readSetIndex = readSetIndex;
		return this;
	}

	public String getKey() {
		return key;
	}

	public FabricTxReadInfo setKey(String key) {
		this.key = key;
		return this;
	}

	public long getReadVersionBlockNum() {
		return readVersionBlockNum;
	}

	public FabricTxReadInfo setReadVersionBlockNum(long readVersionBlockNum) {
		this.readVersionBlockNum = readVersionBlockNum;
		return this;
	}

	public long getReadVersionTxNum() {
		return readVersionTxNum;
	}

	public FabricTxReadInfo setReadVersionTxNum(long readVersionTxNum) {
		this.readVersionTxNum = readVersionTxNum;
		return this;
	}

}
