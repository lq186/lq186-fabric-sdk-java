package com.lq186.fabric.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class FabricNsRwsetInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<FabricTxReadInfo> readSet = new ArrayList<>();;

	private List<FabricTxWriteInfo> writeSet = new ArrayList<>();;

	public List<FabricTxReadInfo> getReadSet() {
		return readSet;
	}

	public FabricNsRwsetInfo setReadSet(List<FabricTxReadInfo> readSet) {
		this.readSet = readSet;
		return this;
	}

	public FabricNsRwsetInfo addReadInfo(FabricTxReadInfo readInfo) {
		readSet.add(readInfo);
		return this;
	}

	public List<FabricTxWriteInfo> getWriteSet() {
		return writeSet;
	}

	public FabricNsRwsetInfo setWriteSet(List<FabricTxWriteInfo> writeSet) {
		this.writeSet = writeSet;
		return this;
	}

	public FabricNsRwsetInfo addWriteInfo(FabricTxWriteInfo writeInfo) {
		writeSet.add(writeInfo);
		return this;
	}
}
