package com.lq186.fabric.bean;

import java.io.Serializable;

import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.sdk.BlockchainInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class FabricBlockchainInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private long height;

	private String currentBlockHash;

	private String previousBlockHash;

	public static FabricBlockchainInfo from(BlockchainInfo blockchainInfo) {
		FabricBlockchainInfo fabricBlockchainInfo = new FabricBlockchainInfo();
		fabricBlockchainInfo.height = blockchainInfo.getHeight();
		fabricBlockchainInfo.currentBlockHash = Hex.toHexString(blockchainInfo.getCurrentBlockHash());
		fabricBlockchainInfo.previousBlockHash = Hex.toHexString(blockchainInfo.getPreviousBlockHash());
		return fabricBlockchainInfo;
	}

	public long getHeight() {
		return height;
	}

	public FabricBlockchainInfo setHeight(long height) {
		this.height = height;
		return this;
	}

	public String getCurrentBlockHash() {
		return currentBlockHash;
	}

	public FabricBlockchainInfo setCurrentBlockHash(String currentBlockHash) {
		this.currentBlockHash = currentBlockHash;
		return this;
	}

	public String getPreviousBlockHash() {
		return previousBlockHash;
	}

	public FabricBlockchainInfo setPreviousBlockHash(String previousBlockHash) {
		this.previousBlockHash = previousBlockHash;
		return this;
	}

}
