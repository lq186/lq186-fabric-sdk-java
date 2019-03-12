package com.lq186.fabric.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class FabricTransactionActionInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private int responseStatus;

	private String responseMessage;

	private int endorsementsCount;

	private int chaincodeInputArgsCount;

	private FabricChaincode chaincode;

	private int status;

	private String payload;

	private List<FabricEndorserInfo> endorsers = new ArrayList<>();;

	private List<String> args = new ArrayList<>();;

	private FabricTxReadWriteSetInfo rwsetInfo;

	public int getResponseStatus() {
		return responseStatus;
	}

	public FabricTransactionActionInfo setResponseStatus(int responseStatus) {
		this.responseStatus = responseStatus;
		return this;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public FabricTransactionActionInfo setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
		return this;
	}

	public int getEndorsementsCount() {
		return endorsementsCount;
	}

	public FabricTransactionActionInfo setEndorsementsCount(int endorsementsCount) {
		this.endorsementsCount = endorsementsCount;
		return this;
	}

	public int getChaincodeInputArgsCount() {
		return chaincodeInputArgsCount;
	}

	public FabricTransactionActionInfo setChaincodeInputArgsCount(int chaincodeInputArgsCount) {
		this.chaincodeInputArgsCount = chaincodeInputArgsCount;
		return this;
	}

	public int getStatus() {
		return status;
	}

	public FabricTransactionActionInfo setStatus(int status) {
		this.status = status;
		return this;
	}

	public String getPayload() {
		return payload;
	}

	public FabricTransactionActionInfo setPayload(String payload) {
		this.payload = payload;
		return this;
	}

	public List<FabricEndorserInfo> getEndorsers() {
		return endorsers;
	}

	public FabricTransactionActionInfo setEndorsers(List<FabricEndorserInfo> endorserInfos) {
		this.endorsers = endorserInfos;
		return this;
	}

	public FabricTransactionActionInfo addEndorser(FabricEndorserInfo endorserInfo) {
		endorsers.add(endorserInfo);
		return this;
	}

	public List<String> getArgs() {
		return args;
	}

	public FabricTransactionActionInfo setArgs(List<String> args) {
		this.args = args;
		return this;
	}

	public FabricTransactionActionInfo addArg(String arg) {
		args.add(arg);
		return this;
	}

	public FabricTxReadWriteSetInfo getRwsetInfo() {
		return rwsetInfo;
	}

	public FabricTransactionActionInfo setRwsetInfo(FabricTxReadWriteSetInfo rwsetInfo) {
		this.rwsetInfo = rwsetInfo;
		return this;
	}

	public FabricChaincode getChaincode() {
		return chaincode;
	}

	public void setChaincode(FabricChaincode chaincode) {
		this.chaincode = chaincode;
	}

}
