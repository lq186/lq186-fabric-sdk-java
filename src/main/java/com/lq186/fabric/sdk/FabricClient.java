package com.lq186.fabric.sdk;

import javax.annotation.Resource;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lq186.fabric.sdk.chaincode.Chaincode;
import com.lq186.fabric.sdk.channel.FabricChannel;
import com.lq186.fabric.sdk.peer.Peer;

@Component
public final class FabricClient {

	private final Logger logger = LoggerFactory.getLogger(FabricClient.class);

	@Resource
	private FabricChannel fabricChannel;

	public FabricResult install(Chaincode chaincode) {
		try {
			return fabricChannel.install(chaincode);
		} catch (Exception e) {
			return errorResult("Install chaincode failed", e);
		}
	}

	public FabricResult instantiate(Chaincode chaincode, String... args) {
		try {
			return fabricChannel.instantiate(chaincode, args);
		} catch (Exception e) {
			return errorResult("Instantiate chaincode failed", e);
		}
	}

	public FabricResult upgrade(Chaincode chaincode, String... args) {
		try {
			return fabricChannel.upgrade(chaincode, args);
		} catch (Exception e) {
			return errorResult("Upgrade chaincode failed", e);
		}
	}

	public FabricResult invoke(Chaincode chaincode, String fcn, String... args) {
		try {
			return fabricChannel.invoke(chaincode, fcn, args);
		} catch (Exception e) {
			return errorResult("Invoke chaincode failed", e);
		}
	}

	public FabricResult query(Chaincode chaincode, String fcn, String... args) {
		try {
			return fabricChannel.query(chaincode, fcn, args);
		} catch (Exception e) {
			return errorResult("Query chaincode failed", e);
		}
	}

	public FabricResult queryBlockByTransactionID(String txID) {
		try {
			return fabricChannel.queryBlockByTransactionID(txID);
		} catch (Exception e) {
			return errorResult("Query block by transaction id failed", e);
		}
	}

	public FabricResult queryBlockByHash(String blockHash) {
		return queryBlockByHash(Hex.decode(blockHash));
	}

	public FabricResult queryBlockByHash(byte[] blockHash) {
		try {
			return fabricChannel.queryBlockByHash(blockHash);
		} catch (Exception e) {
			return errorResult("Query block by hash failed", e);
		}
	}

	public FabricResult queryBlockByNumber(long blockNumber) {
		try {
			return fabricChannel.queryBlockByNumber(blockNumber);
		} catch (Exception e) {
			return errorResult("Query block by number failed", e);
		}
	}

	public FabricResult joinPeer(Peer peer) {
		try {
			return fabricChannel.joinPeer(peer);
		} catch (Exception e) {
			return errorResult("Peer join channel failed", e);
		}
	}

	public FabricResult getBlockchainInfo() {
		try {
			return fabricChannel.getBlockchainInfo();
		} catch (Exception e) {
			return errorResult("Get blockchain info failed", e);
		}
	}

	private FabricResult errorResult(String message, Exception e) {
		String resultMessage = String.format("%s. More info: %s", message, e.getMessage());
		logger.error(resultMessage, e);
		return FabricResult.newInstance(ResultCode.ERROR, resultMessage);
	}

}
