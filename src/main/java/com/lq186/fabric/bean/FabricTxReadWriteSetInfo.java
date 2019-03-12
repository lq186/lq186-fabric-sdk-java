package com.lq186.fabric.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class FabricTxReadWriteSetInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private int nsRWsetCount;

	private List<FabricNsRwsetInfo> nsRwsetInfos = new ArrayList<>();;

	public int getNsRWsetCount() {
		return nsRWsetCount;
	}

	public FabricTxReadWriteSetInfo setNsRWsetCount(int nsRWsetCount) {
		this.nsRWsetCount = nsRWsetCount;
		return this;
	}

	public List<FabricNsRwsetInfo> getNsRwsetInfos() {
		return nsRwsetInfos;
	}

	public FabricTxReadWriteSetInfo setNsRwsetInfos(List<FabricNsRwsetInfo> nsRwsetInfos) {
		this.nsRwsetInfos = nsRwsetInfos;
		return this;
	}

	public FabricTxReadWriteSetInfo addNsRwsetInfo(FabricNsRwsetInfo nsRwsetInfo) {
		nsRwsetInfos.add(nsRwsetInfo);
		return this;
	}

}
