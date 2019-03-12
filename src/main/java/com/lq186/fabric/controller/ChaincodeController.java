package com.lq186.fabric.controller;

import java.util.concurrent.Callable;

import javax.annotation.Resource;

import com.lq186.fabric.controller.request.ChaincodeRequest;
import com.lq186.fabric.sdk.FabricClient;
import com.lq186.fabric.sdk.FabricResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/chaincode")
public class ChaincodeController {

	@Resource
	private FabricClient fabricClient;

	@RequestMapping(value = "/query", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Callable<FabricResult> query(@RequestBody ChaincodeRequest chaincodeRequest) {
		return new Callable<FabricResult>() {
			@Override
			public FabricResult call() throws Exception {
				return fabricClient.query(chaincodeRequest.getChaincode(), "query",
						chaincodeRequest.getArgs().toArray(new String[chaincodeRequest.getArgs().size()]));
			}
		};
	}

	@RequestMapping(value = "/invoke", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Callable<FabricResult> invoke(@RequestBody ChaincodeRequest chaincodeRequest) {
		return new Callable<FabricResult>() {
			@Override
			public FabricResult call() throws Exception {
				return fabricClient.invoke(chaincodeRequest.getChaincode(), "invoke",
						chaincodeRequest.getArgs().toArray(new String[chaincodeRequest.getArgs().size()]));
			}
		};
	}
}
