package com.lq186.fabric.controller;

import java.util.concurrent.Callable;

import javax.annotation.Resource;

import com.lq186.fabric.sdk.FabricClient;
import com.lq186.fabric.sdk.FabricResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/blockchain")
public class BlockchainInfoController {

	@Resource
	private FabricClient fabricClient;

	@RequestMapping(value = { "/", "/info" }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Callable<FabricResult> info() {
		return new Callable<FabricResult>() {
			@Override
			public FabricResult call() throws Exception {
				return fabricClient.getBlockchainInfo();
			}
		};
	}

}
