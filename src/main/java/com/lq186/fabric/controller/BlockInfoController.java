package com.lq186.fabric.controller;

import java.util.concurrent.Callable;

import javax.annotation.Resource;

import com.lq186.fabric.sdk.FabricClient;
import com.lq186.fabric.sdk.FabricResult;
import com.lq186.fabric.sdk.ResultCode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lq186.fabric.bean.FabricBlockInfo;
import com.lq186.fabric.bean.FabricEnvelopeInfo;

@RestController
@RequestMapping(value = "/block")
public class BlockInfoController {

	@Resource
	private FabricClient fabricClient;

	@PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Callable<FabricResult> queryBlockByNumber(@RequestParam(name = "number", required = false) Long number,
                                                     @RequestParam(name = "hash", required = false) String hash,
                                                     @RequestParam(name = "txid", required = false) String txid) {
		return new Callable<FabricResult>() {
			@Override
			public FabricResult call() throws Exception {
				if (null != number) {
					FabricResult result = fabricClient.queryBlockByNumber(number);
					FabricBlockInfo blockInfo = (FabricBlockInfo) result.getData();
					long max = 0;
					long min = 0;
					System.out.println("count >>>> " + blockInfo.getEnvelopeCount());
					for (FabricEnvelopeInfo fei : blockInfo.getEnvelopes()) {
						long timestamp = fei.getTimestamp();
						if (min == 0 || min > timestamp) {
							min = timestamp;
						}
						if (max < timestamp) {
							max = timestamp;
						}
					}
					System.out.println("min timestamp >>>> " + min);
					System.out.println("max timestamp >>>> " + max);
					return result;
				} else if (null != hash && hash.length() > 0) {
					return fabricClient.queryBlockByHash(hash);
				} else if (null != txid && txid.length() > 0) {
					return fabricClient.queryBlockByTransactionID(txid);
				} else {
					return FabricResult.newInstance(ResultCode.ERROR, "One of the number, hash or txid must provided.");
				}
			}
		};
	}

	@PostMapping(value = "/hash/{blockHash}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Callable<FabricResult> queryBlockByHash(@PathVariable(name = "blockHash") String blockHash) {
		return new Callable<FabricResult>() {
			@Override
			public FabricResult call() throws Exception {
				return fabricClient.queryBlockByHash(blockHash);
			}
		};
	}
}
