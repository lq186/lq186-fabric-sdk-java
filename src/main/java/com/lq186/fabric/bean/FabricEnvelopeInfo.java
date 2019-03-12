package com.lq186.fabric.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class FabricEnvelopeInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String channelId;
	
	private String transactionID;
	
	private byte validationCode;
	
	private long timestamp;
	
	private String type;
	
	private FabricIdentitiesInfo creator;
	
	private boolean isValid;
	
	private String nonce;
	
	private FabricTransactionEnvelopeInfo transactionEnvelope;

	public String getChannelId() {
		return channelId;
	}

	public FabricEnvelopeInfo setChannelId(String channelId) {
		this.channelId = channelId;
		return this;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public FabricEnvelopeInfo setTransactionID(String transactionID) {
		this.transactionID = transactionID;
		return this;
	}

	public byte getValidationCode() {
		return validationCode;
	}

	public FabricEnvelopeInfo setValidationCode(byte validationCode) {
		this.validationCode = validationCode;
		return this;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public FabricEnvelopeInfo setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public String getType() {
		return type;
	}

	public FabricEnvelopeInfo setType(String type) {
		this.type = type;
		return this;
	}

	public FabricIdentitiesInfo getCreator() {
		return creator;
	}

	public FabricEnvelopeInfo setCreator(FabricIdentitiesInfo creator) {
		this.creator = creator;
		return this;
	}

	public boolean isValid() {
		return isValid;
	}

	public FabricEnvelopeInfo setValid(boolean isValid) {
		this.isValid = isValid;
		return this;
	}

	public String getNonce() {
		return nonce;
	}

	public FabricEnvelopeInfo setNonce(String nonce) {
		this.nonce = nonce;
		return this;
	}

	public FabricTransactionEnvelopeInfo getTransactionEnvelope() {
		return transactionEnvelope;
	}

	public FabricEnvelopeInfo setTransactionEnvelope(FabricTransactionEnvelopeInfo transactionEnvelope) {
		this.transactionEnvelope = transactionEnvelope;
		return this;
	}
	
	
}
