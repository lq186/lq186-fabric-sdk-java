package com.lq186.fabric.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class FabricBlockInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private long blockNumber;

	private String dataHash;

	private String previousHashID;

	private String calculatedBlockHash;

	private int envelopeCount;

	private List<FabricEnvelopeInfo> envelopes = new ArrayList<>();;

	public long getBlockNumber() {
		return blockNumber;
	}

	public FabricBlockInfo setBlockNumber(long blockNumber) {
		this.blockNumber = blockNumber;
		return this;
	}

	public String getDataHash() {
		return dataHash;
	}

	public FabricBlockInfo setDataHash(String dataHash) {
		this.dataHash = dataHash;
		return this;
	}

	public String getPreviousHashID() {
		return previousHashID;
	}

	public FabricBlockInfo setPreviousHashID(String previousHashID) {
		this.previousHashID = previousHashID;
		return this;
	}

	public String getCalculatedBlockHash() {
		return calculatedBlockHash;
	}

	public FabricBlockInfo setCalculatedBlockHash(String calculatedBlockHash) {
		this.calculatedBlockHash = calculatedBlockHash;
		return this;
	}

	public int getEnvelopeCount() {
		return envelopeCount;
	}

	public FabricBlockInfo setEnvelopeCount(int envelopeCount) {
		this.envelopeCount = envelopeCount;
		return this;
	}

	public List<FabricEnvelopeInfo> getEnvelopes() {
		return envelopes;
	}

	public FabricBlockInfo setEnvelopes(List<FabricEnvelopeInfo> envelopes) {
		this.envelopes = envelopes;
		return this;
	}

	public FabricBlockInfo addEnvelope(FabricEnvelopeInfo envelopeInfo) {
		envelopes.add(envelopeInfo);
		return this;
	}
}
