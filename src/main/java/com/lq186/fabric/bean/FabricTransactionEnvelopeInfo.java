package com.lq186.fabric.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class FabricTransactionEnvelopeInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private int txCount;

	private boolean isValid;

	private byte validationCode;

	private List<FabricTransactionActionInfo> transactionActionInfos = new ArrayList<>();

	public int getTxCount() {
		return txCount;
	}

	public FabricTransactionEnvelopeInfo setTxCount(int txCount) {
		this.txCount = txCount;
		return this;
	}

	public boolean isValid() {
		return isValid;
	}

	public FabricTransactionEnvelopeInfo setValid(boolean isValid) {
		this.isValid = isValid;
		return this;
	}

	public byte getValidationCode() {
		return validationCode;
	}

	public FabricTransactionEnvelopeInfo setValidationCode(byte validationCode) {
		this.validationCode = validationCode;
		return this;
	}

	public List<FabricTransactionActionInfo> getTransactionActionInfos() {
		return transactionActionInfos;
	}

	public FabricTransactionEnvelopeInfo setTransactionActionInfos(
			List<FabricTransactionActionInfo> transactionActionInfos) {
		this.transactionActionInfos = transactionActionInfos;
		return this;
	}

	public FabricTransactionEnvelopeInfo addTransactionActionInfos(FabricTransactionActionInfo transactionActionInfo) {
		transactionActionInfos.add(transactionActionInfo);
		return this;
	}

}
